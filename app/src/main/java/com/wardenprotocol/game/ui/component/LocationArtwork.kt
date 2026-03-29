package com.wardenprotocol.game.ui.component

import androidx.annotation.DrawableRes
import com.wardenprotocol.game.R
import com.wardenprotocol.game.data.model.LocationType

@DrawableRes
fun locationArtworkRes(type: LocationType?): Int = when (type ?: LocationType.RUINED_CITY) {
    LocationType.RUINED_CITY -> R.drawable.loc_ruined_city
    LocationType.FOREST -> R.drawable.loc_forest
    LocationType.MILITARY_BASE -> R.drawable.loc_military_base
    LocationType.FARMLAND -> R.drawable.loc_farmland
    LocationType.UNDERGROUND_RIVER -> R.drawable.loc_underground_river
    LocationType.MOUNTAIN_PASS -> R.drawable.loc_mountain_pass
    LocationType.COASTAL_TOWN -> R.drawable.loc_coastal_town
    LocationType.RESEARCH_FACILITY -> R.drawable.loc_research_facility
    LocationType.RADIOACTIVE_SWAMP -> R.drawable.loc_radioactive_swamp
    LocationType.MEGACRATER -> R.drawable.loc_megacrater
    LocationType.PLAGUE_ZONE -> R.drawable.loc_plague_zone
    LocationType.SCRAP_HEAP -> R.drawable.loc_scrap_heap
    LocationType.ABANDONED_SUBWAY -> R.drawable.loc_abandoned_subway
    LocationType.FUNGAL_WASTES -> R.drawable.loc_fungal_wastes
    LocationType.GLASS_DESERT -> R.drawable.loc_glass_desert
    LocationType.CULT_TERRITORY -> R.drawable.loc_cult_territory
}
