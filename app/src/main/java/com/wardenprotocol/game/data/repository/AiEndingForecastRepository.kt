package com.wardenprotocol.game.data.repository

import com.wardenprotocol.game.BuildConfig
import com.wardenprotocol.game.data.model.AiEndingReport
import com.wardenprotocol.game.data.model.AiTimelineEntry
import com.wardenprotocol.game.data.model.ColonyOutcome
import com.wardenprotocol.game.data.model.OutcomeStats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

sealed class AiEndingForecastResult {
    data class Success(val outcome: ColonyOutcome) : AiEndingForecastResult()
    data class Fallback(val reason: String) : AiEndingForecastResult()
}

class AiEndingForecastRepository {

    suspend fun enhanceOutcome(baseOutcome: ColonyOutcome): AiEndingForecastResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.NVIDIA_NIM_API_KEY
        if (apiKey.isBlank()) {
            return@withContext AiEndingForecastResult.Fallback("Forecast engine offline: missing NVIDIA API key.")
        }

        val requestBody = JSONObject().apply {
            put("model", BuildConfig.NVIDIA_NIM_MODEL)
            put("temperature", 0.2)
            put("top_p", 0.7)
            put("max_tokens", 1024)
            put("stream", false)
            put(
                "messages",
                JSONArray().apply {
                    put(
                        JSONObject().apply {
                            put("role", "system")
                            put("content", SYSTEM_PROMPT)
                        }
                    )
                    put(
                        JSONObject().apply {
                            put("role", "user")
                            put("content", buildUserPrompt(baseOutcome))
                        }
                    )
                }
            )
        }

        return@withContext runCatching {
            val connection = (URL("${BuildConfig.NVIDIA_NIM_BASE_URL}/chat/completions").openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 6_000
                readTimeout = 6_000
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Authorization", "Bearer $apiKey")
            }

            connection.outputStream.use { output ->
                output.write(requestBody.toString().toByteArray())
            }

            val responseCode = connection.responseCode
            val stream = if (responseCode in 200..299) connection.inputStream else connection.errorStream
            val responseText = stream?.use { input ->
                BufferedReader(InputStreamReader(input)).readText()
            }.orEmpty()

            if (responseCode !in 200..299) {
                val message = responseText.takeIf { it.isNotBlank() } ?: "HTTP $responseCode"
                return@runCatching AiEndingForecastResult.Fallback("Forecast engine failed: $message")
            }

            val content = JSONObject(responseText)
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .optString("content")

            val report = parseAiReport(
                rawContent = content,
                baseOutcome = baseOutcome
            )

            AiEndingForecastResult.Success(
                outcome = baseOutcome.copy(
                    score = report.adjustedScore,
                    narrative = report.detailedNarrative,
                    aiReport = report
                )
            )
        }.getOrElse { error ->
            AiEndingForecastResult.Fallback(
                reason = "Forecast engine unavailable: ${error.message ?: "unknown error"}"
            )
        }
    }

    private fun parseAiReport(rawContent: String, baseOutcome: ColonyOutcome): AiEndingReport {
        val jsonText = extractJsonObject(rawContent)
        val payload = JSONObject(jsonText)

        val scoreBlock = payload.getJSONObject("score_adjustment")
        val rawDelta = scoreBlock.optInt("delta", 0)
        val clampedDelta = rawDelta.coerceIn(-1500, 1500)
        val adjustedScore = (baseOutcome.score + clampedDelta).coerceAtLeast(0)

        val timeline = payload.optJSONArray("timeline")
            ?.let(::parseTimeline)
            .orEmpty()
            .ifEmpty {
                listOf(
                    AiTimelineEntry("10 YEARS", "Short-Term Survival", "Uncertain", "Unknown", "The model failed to provide a usable forecast beat."),
                    AiTimelineEntry("50 YEARS", "Mid-Century Outlook", "Uncertain", "Unknown", "Fallback chronology in effect."),
                    AiTimelineEntry("100 YEARS", "Century Outlook", "Uncertain", "Unknown", "No structured century projection returned."),
                    AiTimelineEntry("FINAL", "Terminal Verdict", "Undetermined", "Unknown", "The deterministic narrative remains authoritative.")
                )
            }

        return AiEndingReport(
            scoreDelta = clampedDelta,
            adjustedScore = adjustedScore,
            scoreReason = scoreBlock.optString("reason", "No score reason returned by the forecast engine."),
            headline = payload.optString("headline", baseOutcome.classification.uppercase()),
            civilizationVerdict = payload.optString("civilization_verdict", "No final civilization verdict returned."),
            failureCauses = payload.optJSONArray("failure_causes").toStringList(),
            survivalDrivers = payload.optJSONArray("survival_drivers").toStringList(),
            timeline = timeline,
            detailedNarrative = payload.optString("detailed_narrative", baseOutcome.narrative)
        )
    }

    private fun parseTimeline(array: JSONArray): List<AiTimelineEntry> =
        List(array.length()) { index ->
            val item = array.getJSONObject(index)
            AiTimelineEntry(
                marker = item.optString("marker", "UNKNOWN"),
                title = item.optString("title", "Unnamed Phase"),
                status = item.optString("status", "Unknown"),
                populationEstimate = item.optString("population_estimate", "Unknown"),
                narrative = item.optString("narrative", "No narrative returned.")
            )
        }

    private fun JSONArray?.toStringList(): List<String> {
        if (this == null) return emptyList()
        return List(length()) { index -> optString(index) }.filter { it.isNotBlank() }
    }

    private fun extractJsonObject(rawContent: String): String {
        val start = rawContent.indexOf('{')
        val end = rawContent.lastIndexOf('}')
        require(start >= 0 && end > start) { "Model response did not contain a valid JSON object." }
        return rawContent.substring(start, end + 1)
    }

    private fun buildUserPrompt(outcome: ColonyOutcome): String {
        val stats = outcome.detailedStats
        return buildString {
            appendLine("You are the ending-analysis engine for a brutal post-nuclear survival game.")
            appendLine("You must produce a dark, brutally honest, long-range settlement forecast based on the full run state.")
            appendLine("Return ONLY valid JSON. No markdown fences. No commentary outside JSON.")
            appendLine("Score adjustment delta must be an integer between -1500 and 1500.")
            appendLine()
            appendLine("RUN SNAPSHOT")
            appendLine("Settlement: ${outcome.settlementName}")
            appendLine("Classification: ${outcome.classification}")
            appendLine("Base Score: ${outcome.score}")
            appendLine("Fallback Deterministic Narrative: ${outcome.narrative}")
            appendLine()
            appendLine("FULL STATE")
            appendLine(formatStats(stats))
            appendLine()
            appendLine("TASK")
            appendLine("1. Judge whether this colony meaningfully stabilizes, decays slowly, collapses violently, or re-establishes civilization.")
            appendLine("2. Explain why with material causes, social causes, environmental causes, and travel consequences.")
            appendLine("3. Project what happens after 10 years, 50 years, 100 years, and then give a final terminal verdict.")
            appendLine("4. Be unsentimental, concrete, and dark. If they fail, say exactly why they fail. If they survive, say what it cost and whether it is truly civilization or only a harsher machine for living.")
            appendLine("5. Adjust the score based on long-term sustainability, logistics, environmental fitness, archive preservation, and realism.")
            appendLine()
            appendLine("RETURN THIS EXACT JSON SHAPE")
            appendLine(
                """
                {
                  "score_adjustment": {
                    "delta": -420,
                    "reason": "The route and long-term water scarcity make the initial score too optimistic."
                  },
                  "headline": "RESOURCE DEPLETION",
                  "civilization_verdict": "The settlement survives its first decade but never escapes scarcity and dies before a true state can form.",
                  "failure_causes": [
                    "Water access never becomes dependable.",
                    "Travel attrition removed too much skilled labor before construction began.",
                    "Hostility pressure forced resources into defense instead of renewal."
                  ],
                  "survival_drivers": [
                    "Archive continuity preserved technical memory.",
                    "Security discipline delayed immediate collapse."
                  ],
                  "timeline": [
                    {
                      "marker": "10 YEARS",
                      "title": "The First Scarcity Decade",
                      "status": "Bare Survival",
                      "population_estimate": "640 survivors",
                      "narrative": "They are alive, but only because rationing is absolute and every machine is cannibalized faster than it is repaired."
                    },
                    {
                      "marker": "50 YEARS",
                      "title": "Inheritance of Ruin",
                      "status": "Declining",
                      "population_estimate": "210 survivors",
                      "narrative": "The second generation inherits doctrine instead of abundance. The settlement still stands, but mostly as a harder method of starving slowly."
                    },
                    {
                      "marker": "100 YEARS",
                      "title": "The Last Ledger",
                      "status": "Near Collapse",
                      "population_estimate": "43 survivors",
                      "narrative": "By the century mark the archives matter more than the bloodline because there are barely enough people left to read them."
                    },
                    {
                      "marker": "FINAL",
                      "title": "Terminal Verdict",
                      "status": "Extinction",
                      "population_estimate": "0 survivors",
                      "narrative": "The colony dies without re-establishing civilization. Its failure is logistical first, environmental second, and only then moral."
                    }
                  ],
                  "detailed_narrative": "A long-form, dark, honest narrative that synthesizes all of the above into one cohesive ending report."
                }
                """.trimIndent()
            )
        }
    }

    private fun formatStats(stats: OutcomeStats?): String {
        if (stats == null) return "No detailed telemetry available."
        return buildString {
            appendLine("Location Name: ${stats.locationName}")
            appendLine("Location Type: ${stats.locationTypeName}")
            appendLine("Years Since War: ${stats.yearsSinceWar}")
            appendLine("Survivors: ${stats.survivors}")
            appendLine("Deaths: ${stats.deaths}")
            appendLine("Travel Route: ${stats.travelRoute}")
            appendLine("Travel Time: ${stats.travelTime}")
            appendLine("Travel Risk: ${stats.travelRisk}")
            appendLine("Travel Deaths: ${stats.travelDeaths}")
            appendLine("Radiation: ${stats.radiation}")
            appendLine("Water: ${stats.water}")
            appendLine("Food: ${stats.food}")
            appendLine("Shelter: ${stats.shelter}")
            appendLine("Resources: ${stats.resources}")
            appendLine("Threats: ${stats.threats}")
            appendLine("Power Grid: ${stats.powerGrid}")
            appendLine("Food Stores: ${stats.foodStores}")
            appendLine("Medical Bay: ${stats.medicalBay}")
            appendLine("Security System: ${stats.securitySystem}")
            appendLine("Construction Gear: ${stats.constructionGear}")
            appendLine("Atmosphere Scrubbers: ${stats.atmosphereScrubbers}")
            appendLine("Cultural Archive: ${stats.culturalArchive}")
            appendLine("Scientific Archive: ${stats.scientificArchive}")
        }
    }

    private companion object {
        private const val SYSTEM_PROMPT = """
You are a ruthless but grounded post-collapse worldbuilding analyst.
You are not a cheerleader.
You do not soften bad outcomes.
You judge long-term viability by logistics, environment, demography, governance, knowledge retention, and violence pressure.
When survival is temporary, say so.
When civilization is re-established, explain why it is durable instead of merely hopeful.
Return only strict JSON matching the requested schema.
        """
    }
}
