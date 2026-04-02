package com.ivarna.wardenprotocol.ui.screen

import com.ivarna.wardenprotocol.R
import com.ivarna.wardenprotocol.data.model.GameEvent
import com.ivarna.wardenprotocol.data.repository.ExpandedEventCatalog

internal enum class EventImageKey(
    val drawableRes: Int
) {
    VAULT_POWER_INFRASTRUCTURE(R.drawable.event_vault_power_infrastructure),
    VAULT_MEDICAL_BIOHAZARD(R.drawable.event_vault_medical_biohazard),
    VAULT_SOCIAL_BREAKDOWN(R.drawable.event_vault_social_breakdown),
    VAULT_SUPPLY_SHORTAGE(R.drawable.event_vault_supply_shortage),
    VAULT_WINDFALL_CACHE(R.drawable.event_vault_windfall_cache),
    VAULT_CATASTROPHE(R.drawable.event_vault_catastrophe),
    SURFACE_CONTACT_SIGNAL(R.drawable.event_surface_contact_signal),
    SURFACE_HOSTILE_ENCOUNTER(R.drawable.event_surface_hostile_encounter),
    SURFACE_WEATHER_HAZARD(R.drawable.event_surface_weather_hazard),
    SURFACE_TOXIC_HAZARD(R.drawable.event_surface_toxic_hazard),
    SURFACE_SALVAGE_OPPORTUNITY(R.drawable.event_surface_salvage_opportunity),
    SURFACE_CATASTROPHE(R.drawable.event_surface_catastrophe),
    COSMIC_SIGNAL_MACHINE(R.drawable.event_cosmic_signal_machine),
    COSMIC_PSYCHIC_TEMPORAL(R.drawable.event_cosmic_psychic_temporal),
    COSMIC_LIVING_ANOMALY(R.drawable.event_cosmic_living_anomaly),
    APEX_HUMAN_WARLORD(R.drawable.event_apex_human_warlord),
    APEX_AI_OVERMIND(R.drawable.event_apex_ai_overmind),
    APEX_ALIEN_INCURSION(R.drawable.event_apex_alien_incursion);

    val displayLabel: String
        get() = name.lowercase()
}

internal fun GameEvent.resolveEventImageKey(): EventImageKey {
    val idLower = id.lowercase()
    val haystack = listOf(id, title, description)
        .joinToString(" ")
        .lowercase()

    return when {
        idLower.startsWith("apex_human_") -> EventImageKey.APEX_HUMAN_WARLORD
        idLower.startsWith("apex_ai_") -> EventImageKey.APEX_AI_OVERMIND
        idLower.startsWith("apex_alien_") -> EventImageKey.APEX_ALIEN_INCURSION

        id in ExpandedEventCatalog.vaultWindfallIds -> EventImageKey.VAULT_WINDFALL_CACHE
        id in ExpandedEventCatalog.vaultDoomIds -> EventImageKey.VAULT_CATASTROPHE
        id in ExpandedEventCatalog.surfaceWindfallIds -> EventImageKey.SURFACE_SALVAGE_OPPORTUNITY
        id in ExpandedEventCatalog.surfaceCatastropheIds -> EventImageKey.SURFACE_CATASTROPHE

        idLower.startsWith("vault_") -> when {
            haystack.hasAny("plague", "medical", "gene", "blood", "quarantine", "epidemic", "mercy", "organ") ->
                EventImageKey.VAULT_MEDICAL_BIOHAZARD
            haystack.hasAny("power", "reactor", "generator", "scrubber", "oxygen", "fuel", "static") ->
                EventImageKey.VAULT_POWER_INFRASTRUCTURE
            haystack.hasAny("food", "water", "birth", "slurry", "root", "cistern", "seed") ->
                EventImageKey.VAULT_SUPPLY_SHORTAGE
            haystack.hasAny("coup", "mutiny", "child", "chair", "security", "lottery", "brand", "chapel", "market", "grief") ->
                EventImageKey.VAULT_SOCIAL_BREAKDOWN
            else -> EventImageKey.VAULT_CATASTROPHE
        }

        idLower.startsWith("surface_") -> when {
            haystack.hasAny("signal", "knock", "survivor", "refugee", "nomad", "keepers", "monks", "hunters") ->
                EventImageKey.SURFACE_CONTACT_SIGNAL
            haystack.hasAny("raider", "hostile", "warlord", "band", "drone", "scavenger", "dealers") ->
                EventImageKey.SURFACE_HOSTILE_ENCOUNTER
            haystack.hasAny("rain", "ash", "hail", "snow", "flare", "storm", "lightning", "ice") ->
                EventImageKey.SURFACE_WEATHER_HAZARD
            haystack.hasAny("radiation", "toxic", "disease", "fungal", "bioweapon", "bloom", "plume") ->
                EventImageKey.SURFACE_TOXIC_HAZARD
            haystack.hasAny("cache", "vehicle", "depot", "train", "lab", "tower", "greenhouse", "spring", "hospital", "substation") ->
                EventImageKey.SURFACE_SALVAGE_OPPORTUNITY
            else -> EventImageKey.SURFACE_CATASTROPHE
        }

        idLower.startsWith("cosmic_") -> when {
            haystack.hasAny("signal", "broadcast", "echo", "tower", "ai", "probe", "map", "vault", "door") ->
                EventImageKey.COSMIC_SIGNAL_MACHINE
            haystack.hasAny("time", "clock", "dream", "memory", "double", "mirror", "name", "shadows") ->
                EventImageKey.COSMIC_PSYCHIC_TEMPORAL
            else -> EventImageKey.COSMIC_LIVING_ANOMALY
        }

        else -> EventImageKey.VAULT_CATASTROPHE
    }
}

private fun String.hasAny(vararg needles: String): Boolean {
    return needles.any { contains(it) }
}
