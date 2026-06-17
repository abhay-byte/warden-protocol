package com.ivarna.wardenprotocol.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ivarna.wardenprotocol.R
import com.ivarna.wardenprotocol.data.model.*

@Composable
fun radiationDisplayName(value: RadiationLevel): String = when (value) {
    RadiationLevel.NONE -> stringResource(R.string.radiation_none)
    RadiationLevel.LOW -> stringResource(R.string.radiation_low)
    RadiationLevel.MODERATE -> stringResource(R.string.radiation_moderate)
    RadiationLevel.HIGH -> stringResource(R.string.radiation_high)
    RadiationLevel.LETHAL -> stringResource(R.string.radiation_lethal)
}

@Composable
fun waterDisplayName(value: WaterAvailability): String = when (value) {
    WaterAvailability.ABUNDANT -> stringResource(R.string.water_abundant)
    WaterAvailability.SCARCE -> stringResource(R.string.water_scarce)
    WaterAvailability.NONE -> stringResource(R.string.water_none)
}

@Composable
fun foodDisplayName(value: FoodPotential): String = when (value) {
    FoodPotential.FERTILE -> stringResource(R.string.food_fertile)
    FoodPotential.MARGINAL -> stringResource(R.string.food_marginal)
    FoodPotential.BARREN -> stringResource(R.string.food_barren)
}

@Composable
fun shelterDisplayName(value: ShelterQuality): String = when (value) {
    ShelterQuality.EXCELLENT -> stringResource(R.string.shelter_excellent)
    ShelterQuality.GOOD -> stringResource(R.string.shelter_good)
    ShelterQuality.POOR -> stringResource(R.string.shelter_poor)
    ShelterQuality.NONE -> stringResource(R.string.shelter_none)
}

@Composable
fun resourceDisplayName(value: ResourceRichness): String = when (value) {
    ResourceRichness.RICH -> stringResource(R.string.resource_rich)
    ResourceRichness.MODERATE -> stringResource(R.string.resource_moderate)
    ResourceRichness.POOR -> stringResource(R.string.resource_poor)
}

@Composable
fun hostilityDisplayName(value: Hostility): String = when (value) {
    Hostility.NONE -> stringResource(R.string.hostility_none)
    Hostility.BANDITS -> stringResource(R.string.hostility_bandits)
    Hostility.WARLORD -> stringResource(R.string.hostility_warlord)
    Hostility.WASTELAND_CULT -> stringResource(R.string.hostility_wasteland_cult)
}

@Composable
fun travelRiskDisplayName(value: TravelRisk): String = when (value) {
    TravelRisk.LOW -> stringResource(R.string.travel_risk_low)
    TravelRisk.MODERATE -> stringResource(R.string.travel_risk_moderate)
    TravelRisk.HIGH -> stringResource(R.string.travel_risk_high)
    TravelRisk.EXTREME -> stringResource(R.string.travel_risk_extreme)
}

@Composable
fun anomalyDisplayName(value: SurfaceAnomaly): String = when (value) {
    SurfaceAnomaly.SEED_VAULT -> stringResource(R.string.anomaly_seed_vault)
    SurfaceAnomaly.FUNCTIONING_REACTOR -> stringResource(R.string.anomaly_functioning_reactor)
    SurfaceAnomaly.ALIEN_SIGNAL -> stringResource(R.string.anomaly_alien_signal)
    SurfaceAnomaly.SURVIVOR_CAMP -> stringResource(R.string.anomaly_survivor_camp)
    SurfaceAnomaly.ANCIENT_LIBRARY -> stringResource(R.string.anomaly_ancient_library)
    SurfaceAnomaly.CLEAN_WATER_SPRING -> stringResource(R.string.anomaly_clean_water_spring)
    SurfaceAnomaly.WEAPONS_CACHE -> stringResource(R.string.anomaly_weapons_cache)
    SurfaceAnomaly.DISEASE_OUTBREAK_SITE -> stringResource(R.string.anomaly_disease_outbreak_site)
}

@Composable
fun locationTypeDisplayName(value: LocationType): String = when (value) {
    LocationType.RUINED_CITY -> stringResource(R.string.loc_type_ruined_city)
    LocationType.FOREST -> stringResource(R.string.loc_type_forest)
    LocationType.MILITARY_BASE -> stringResource(R.string.loc_type_military_base)
    LocationType.FARMLAND -> stringResource(R.string.loc_type_farmland)
    LocationType.UNDERGROUND_RIVER -> stringResource(R.string.loc_type_underground_river)
    LocationType.MOUNTAIN_PASS -> stringResource(R.string.loc_type_mountain_pass)
    LocationType.COASTAL_TOWN -> stringResource(R.string.loc_type_coastal_town)
    LocationType.RESEARCH_FACILITY -> stringResource(R.string.loc_type_research_facility)
    LocationType.RADIOACTIVE_SWAMP -> stringResource(R.string.loc_type_radioactive_swamp)
    LocationType.MEGACRATER -> stringResource(R.string.loc_type_megacrater)
    LocationType.PLAGUE_ZONE -> stringResource(R.string.loc_type_plague_zone)
    LocationType.SCRAP_HEAP -> stringResource(R.string.loc_type_scrap_heap)
    LocationType.ABANDONED_SUBWAY -> stringResource(R.string.loc_type_abandoned_subway)
    LocationType.FUNGAL_WASTES -> stringResource(R.string.loc_type_fungal_wastes)
    LocationType.GLASS_DESERT -> stringResource(R.string.loc_type_glass_desert)
    LocationType.CULT_TERRITORY -> stringResource(R.string.loc_type_cult_territory)
}
