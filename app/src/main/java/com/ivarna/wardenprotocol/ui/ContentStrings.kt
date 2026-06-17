package com.ivarna.wardenprotocol.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.ivarna.wardenprotocol.R
import com.ivarna.wardenprotocol.data.model.Hostility
import com.ivarna.wardenprotocol.data.model.RadiationLevel
import com.ivarna.wardenprotocol.data.model.WaterAvailability
import com.ivarna.wardenprotocol.data.model.FoodPotential
import com.ivarna.wardenprotocol.data.model.ShelterQuality
import com.ivarna.wardenprotocol.data.model.ResourceRichness
import com.ivarna.wardenprotocol.data.model.SurfaceAnomaly
import com.ivarna.wardenprotocol.data.model.LocationType

private fun Context.stringOrNull(name: String): String? {
    val resId = resources.getIdentifier(name, "string", packageName)
    return if (resId != 0) getString(resId) else null
}

@Composable
fun localizedEventTitle(eventId: String): String? {
    val ctx = LocalContext.current
    return ctx.stringOrNull("event_${eventId}_title")
}

@Composable
fun localizedEventDescription(eventId: String): String? {
    val ctx = LocalContext.current
    return ctx.stringOrNull("event_${eventId}_desc")
}

@Composable
fun localizedChoiceLabel(eventId: String, index: Int): String? {
    val suffix = when (index) {
        0 -> "choice_a_label"
        1 -> "choice_b_label"
        2 -> "choice_c_label"
        else -> return null
    }
    val ctx = LocalContext.current
    return ctx.stringOrNull("event_${eventId}_$suffix")
}

@Composable
fun localizedChoiceDescription(eventId: String, index: Int): String? {
    val suffix = when (index) {
        0 -> "choice_a_desc"
        1 -> "choice_b_desc"
        2 -> "choice_c_desc"
        else -> return null
    }
    val ctx = LocalContext.current
    return ctx.stringOrNull("event_${eventId}_$suffix")
}

@Composable
fun localizedChoiceKnownEffect(eventId: String, index: Int): String? {
    val suffix = when (index) {
        0 -> "choice_a_effect"
        1 -> "choice_b_effect"
        2 -> "choice_c_effect"
        else -> return null
    }
    val ctx = LocalContext.current
    return ctx.stringOrNull("event_${eventId}_$suffix")
}

@Composable
fun localizedChoiceOutcome(eventId: String, index: Int): String? {
    val suffix = when (index) {
        0 -> "choice_a_outcome"
        1 -> "choice_b_outcome"
        2 -> "choice_c_outcome"
        else -> return null
    }
    val ctx = LocalContext.current
    return ctx.stringOrNull("event_${eventId}_$suffix")
}

@Composable
fun localizedIntelTerrain(type: LocationType): String? {
    val ctx = LocalContext.current
    return ctx.stringOrNull("intel_terrain_${type.name.lowercase()}")
}

@Composable
fun localizedIntelRadiation(level: RadiationLevel): String? {
    val ctx = LocalContext.current
    return ctx.stringOrNull("intel_radiation_${level.name.lowercase()}")
}

@Composable
fun localizedIntelWater(level: WaterAvailability): String? {
    val ctx = LocalContext.current
    return ctx.stringOrNull("intel_water_${level.name.lowercase()}")
}

@Composable
fun localizedIntelFood(level: FoodPotential): String? {
    val ctx = LocalContext.current
    return ctx.stringOrNull("intel_food_${level.name.lowercase()}")
}

@Composable
fun localizedIntelShelter(level: ShelterQuality): String? {
    val ctx = LocalContext.current
    return ctx.stringOrNull("intel_shelter_${level.name.lowercase()}")
}

@Composable
fun localizedIntelResource(level: ResourceRichness): String? {
    val ctx = LocalContext.current
    return ctx.stringOrNull("intel_resource_${level.name.lowercase()}")
}

@Composable
fun localizedIntelHostility(level: Hostility): String? {
    val ctx = LocalContext.current
    return ctx.stringOrNull("intel_hostility_${level.name.lowercase()}")
}

@Composable
fun localizedIntelAnomaly(anomaly: SurfaceAnomaly): String? {
    val ctx = LocalContext.current
    return ctx.stringOrNull("intel_anomaly_${anomaly.name.lowercase()}")
}

@Composable
fun localizedIntelAnomalyNone(): String? {
    val ctx = LocalContext.current
    return ctx.stringOrNull("intel_anomaly_none")
}

@Composable
fun localizedIntelLongPrefix(name: String): String? {
    val ctx = LocalContext.current
    return ctx.stringOrNull("intel_long_prefix")
}

@Composable
fun localizedTravelRouteName(type: LocationType): String? {
    val ctx = LocalContext.current
    return ctx.stringOrNull("travel_route_${type.name.lowercase()}")
}

@Composable
fun localizedTravelDuration(type: LocationType): String? {
    val ctx = LocalContext.current
    return ctx.stringOrNull("travel_duration_${type.name.lowercase()}")
}

@Composable
fun localizedTravelRiskSummary(empty: Boolean): String? {
    val ctx = LocalContext.current
    return ctx.stringOrNull(if (empty) "travel_risk_summary_empty" else "travel_risk_summary_drivers")
}

@Composable
fun localizedSystemLabel(key: String): String? {
    val ctx = LocalContext.current
    return ctx.stringOrNull("system_${key.lowercase()}")
}

@Composable
fun localizedArchiveLabel(key: String): String? {
    val ctx = LocalContext.current
    return ctx.stringOrNull("archive_${key.lowercase()}")
}

@Composable
fun localizedTransitLead(level: String): String? {
    val ctx = LocalContext.current
    return ctx.stringOrNull("transit_lead_${level.lowercase()}")
}

@Composable
fun localizedSettlementPrefix(type: LocationType): List<String> {
    val ctx = LocalContext.current
    val raw = ctx.stringOrNull("settlement_prefixes_${type.name.lowercase()}") ?: return emptyList()
    return raw.split("|")
}

@Composable
fun localizedSettlementSuffixes(): List<String> {
    val ctx = LocalContext.current
    val raw = ctx.stringOrNull("settlement_suffixes") ?: return emptyList()
    return raw.split("|")
}

