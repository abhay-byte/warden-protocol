package com.wardenprotocol.game.data.repository

import com.wardenprotocol.game.data.model.EventChoice
import com.wardenprotocol.game.data.model.EventOutcome
import com.wardenprotocol.game.data.model.GameEvent

internal object ExpandedEventCatalog {
    val vaultEvents: List<GameEvent> by lazy {
        buildVaultSystemCrises() + buildVaultTabooEvents() + buildVaultWindfalls() + buildVaultDoomEvents()
    }

    val surfaceEvents: List<GameEvent> by lazy {
        buildSurfaceHazards() + buildSurfaceWindfalls() + buildSurfaceContacts() + buildSurfaceCatastrophes()
    }

    val cosmicEvents: List<GameEvent> by lazy {
        buildCosmicDreadEvents() + buildCosmicBoonEvents()
    }

    val apexThreatEvents: List<GameEvent> by lazy {
        buildApexThreatEvents()
    }

    val vaultWindfallIds: Set<String> by lazy {
        buildVaultWindfalls().mapTo(linkedSetOf()) { it.id }
    }

    val vaultDoomIds: Set<String> by lazy {
        buildVaultDoomEvents().mapTo(linkedSetOf()) { it.id }
    }

    val surfaceWindfallIds: Set<String> by lazy {
        buildSurfaceWindfalls().mapTo(linkedSetOf()) { it.id }
    }

    val surfaceCatastropheIds: Set<String> by lazy {
        buildSurfaceCatastrophes().mapTo(linkedSetOf()) { it.id }
    }
}

private data class TwoSystemSpec(
    val id: String,
    val title: String,
    val description: String,
    val primarySystem: String,
    val secondarySystem: String
)

private data class ArchiveSpec(
    val id: String,
    val title: String,
    val description: String,
    val primarySystem: String,
    val secondarySystem: String,
    val archiveTrack: String
)

private data class ContactSpec(
    val id: String,
    val title: String,
    val description: String,
    val rewardSystem: String,
    val riskSystem: String,
    val archiveTrack: String
)

private data class ExtremeSpec(
    val id: String,
    val title: String,
    val description: String,
    val primarySystem: String,
    val secondarySystem: String,
    val archiveTrack: String
)

private fun systemLabel(key: String): String = when (key) {
    "powerGrid" -> "power grid"
    "foodStores" -> "food stores"
    "medicalBay" -> "medical bay"
    "securitySystem" -> "security system"
    "constructionGear" -> "construction gear"
    "atmosphereScrubbers" -> "atmosphere scrubbers"
    "radiationScanner" -> "radiation scanners"
    "waterScanner" -> "water scanners"
    "agriculturalScanner" -> "agricultural scanners"
    "structureScanner" -> "structural scanners"
    "resourceScanner" -> "resource scanners"
    "threatAssessment" -> "threat assessment net"
    else -> key
}

private fun archiveLabel(key: String): String = when (key) {
    "culturalArchive" -> "cultural archive"
    "scientificArchive" -> "scientific archive"
    else -> key
}

private fun buildVaultSystemCrises(): List<GameEvent> {
    val specs = listOf(
        TwoSystemSpec("vault_exp_bone_dust_filters", "Bone Dust Filters", "The air scrubbers are choking on a fine white powder drifting up from the cremation chute. Nobody signed off on that much ash. Nobody wants to say where it really came from.", "atmosphereScrubbers", "constructionGear"),
        TwoSystemSpec("vault_exp_slurry_line_failure", "Slurry Line Failure", "Protein slurry is backing up through the service ducts. The smell is rank, sweet, and unmistakably human enough to start rumors that don't need proof.", "foodStores", "waterScanner"),
        TwoSystemSpec("vault_exp_quarantine_hinge", "The Quarantine Hinge", "A containment door in the plague wing is hanging half-open, warped by heat from the last sterilization burn. Something on the other side keeps striking it from within.", "securitySystem", "medicalBay"),
        TwoSystemSpec("vault_exp_reactor_glass", "Reactor Glass", "A viewing blister near the reactor has turned cloudy black from the inside. The technicians say it isn't soot. The Geiger counters say nothing useful at all.", "powerGrid", "radiationScanner"),
        TwoSystemSpec("vault_exp_heatstroke_deck", "Heatstroke On Level Five", "Cooling fans have died across the worker dormitories. Sweat runs down the walls. People are sleeping beside open maintenance vents and not always waking up.", "powerGrid", "medicalBay"),
        TwoSystemSpec("vault_exp_root_blindness", "Root Blindness", "Hydroponic roots have fused into a pale mat that is clogging pumps and drinking half the reclaimed water before anyone can stop it.", "foodStores", "waterScanner"),
        TwoSystemSpec("vault_exp_sleep_gas_leak", "Sleep Gas Leak", "A cracked sedation manifold has flooded an entire residential corridor with surgical mist. Half the floor is unconscious. The other half is stealing from them.", "medicalBay", "securitySystem"),
        TwoSystemSpec("vault_exp_cold_blood_freezer", "Cold Blood In The Freezer", "The meat lockers are frosting over from the inside and the refrigeration pumps are drawing far more current than they should. Something in storage is still metabolizing.", "foodStores", "powerGrid"),
        TwoSystemSpec("vault_exp_latrine_uprising", "Latrine Uprising", "The sanitation trench on the lowest deck has overflowed again. Workers are refusing to clear it without hazard pay you cannot give them.", "waterScanner", "securitySystem"),
        TwoSystemSpec("vault_exp_funeral_queue", "The Funeral Queue", "The dead are accumulating faster than the furnaces can process them. Families are sleeping beside wrapped bodies because there is nowhere else to put them.", "constructionGear", "atmosphereScrubbers"),
        TwoSystemSpec("vault_exp_night_shift_cult", "Night Shift Cult", "The overnight maintenance crews have started shutting off their headlamps to pray in the dark. Repairs are being delayed because 'the machine prefers silence.'", "constructionGear", "securitySystem"),
        TwoSystemSpec("vault_exp_oxygen_fire", "Oxygen Fire", "An oxygen-rich maintenance pocket flashed into white fire and turned two corridor junctions into blistered steel. The flames are gone; the heat is not.", "atmosphereScrubbers", "constructionGear"),
        TwoSystemSpec("vault_exp_gene_vat_spill", "Gene Vat Spill", "A bioreactor in the research annex ruptured and dumped warm cellular sludge through three floors of cable trench. The med techs want samples. Everyone else wants flame.", "medicalBay", "scientificArchive"),
        TwoSystemSpec("vault_exp_blackwater_pumps", "Blackwater Pumps", "The recycler pumps are drawing up a greasy fluid no one remembers storing. It leaves burns on exposed skin and pits the impellers overnight.", "waterScanner", "medicalBay"),
        TwoSystemSpec("vault_exp_command_static", "Command Static", "The command deck intercom has started replaying old emergency orders over live channels, drowning out real instructions and sending crews to the wrong sectors.", "securitySystem", "threatAssessment")
    )

    return specs.map { spec ->
        GameEvent(
            id = spec.id,
            title = spec.title,
            description = spec.description,
            choiceA = EventChoice(
                label = "Seal it and study it",
                description = "Lock the problem behind steel and buy time for analysis.",
                knownEffect = "Fewer deaths. ${systemLabel(spec.primarySystem)} degrades.",
                hiddenRisk = 0.15f,
                outcome = EventOutcome(
                    survivorDelta = -2,
                    systemDeltas = mapOf(spec.primarySystem to -12, spec.secondarySystem to -4),
                    narrativeText = "You lock the sector down and let the screams, hissing, or pounding spend themselves behind the bulkheads. It works. The vault survives another night by sacrificing time and hardware."
                )
            ),
            choiceB = EventChoice(
                label = "Send a repair gang",
                description = "Throw tools, bodies, and nerve directly at the failure.",
                knownEffect = "Best chance to recover ${systemLabel(spec.primarySystem)}. Casualties likely.",
                hiddenRisk = 0.35f,
                outcome = EventOutcome(
                    survivorDelta = -5,
                    systemDeltas = mapOf(spec.primarySystem to 14, spec.secondarySystem to -6),
                    narrativeText = "The repair gang goes in fast and loses people almost immediately, but the job gets done. The system steadies. The blood on the deck plates is the cost of competence."
                )
            ),
            choiceC = EventChoice(
                label = "Strip another wing for parts",
                description = "Cannibalize a secondary system and keep the critical failure from spreading.",
                knownEffect = "Repairs ${systemLabel(spec.primarySystem)}. Harms ${systemLabel(spec.secondarySystem)}.",
                outcome = EventOutcome(
                    systemDeltas = mapOf(spec.primarySystem to 20, spec.secondarySystem to -18),
                    narrativeText = "You tear intact machinery out of a still-living wing and bolt it into the dying one. The crisis ends. Another part of the vault goes dark in silence."
                )
            )
        )
    }
}

private fun buildVaultTabooEvents(): List<GameEvent> {
    val specs = listOf(
        ArchiveSpec("vault_exp_corpse_yeast", "Corpse Yeast", "Someone in food processing has been culturing protein yeast on rendered human remains from the cremation backlog. The growth vats are healthy. Morality is not.", "foodStores", "culturalArchive", "scientificArchive"),
        ArchiveSpec("vault_exp_organ_tithe", "Organ Tithe", "A quiet network inside the medical bay has begun harvesting 'voluntary donations' from the terminally ill before they die. Their recipients are recovering fast.", "medicalBay", "culturalArchive", "scientificArchive"),
        ArchiveSpec("vault_exp_sleep_lottery", "The Sleep Lottery", "The worst bunks in the vault are now assigned by a rigged lottery run by exhausted quartermasters. Losers are sedated and moved without asking.", "securitySystem", "culturalArchive", "scientificArchive"),
        ArchiveSpec("vault_exp_loyalty_brand", "Loyalty Brands", "A security chief has begun marking repeat troublemakers with a heated maintenance stamp so guards can identify them at a glance in blackouts.", "securitySystem", "medicalBay", "culturalArchive"),
        ArchiveSpec("vault_exp_confession_pit", "The Confession Pit", "Residents are gathering in an abandoned sump chamber to confess theft, adultery, cannibal thoughts, and murder fantasies into a drain that no longer leads anywhere.", "culturalArchive", "securitySystem", "scientificArchive"),
        ArchiveSpec("vault_exp_fight_ring", "The Maintenance Ring", "Workers have turned a broken lift shaft into a bare-knuckle fight pit where food chits, medicine, and grudges are settled in public.", "securitySystem", "foodStores", "culturalArchive"),
        ArchiveSpec("vault_exp_narcotic_chapel", "Narcotic Chapel", "The med techs' painkillers are being diluted and re-sold through an improvised chapel where people kneel for pills and absolution together.", "medicalBay", "securitySystem", "culturalArchive"),
        ArchiveSpec("vault_exp_memory_strip", "Memory Strip", "Archivists are paying desperate residents to surrender letters, heirlooms, and private recordings for pulping and reuse. The shelves grow. So does the hatred.", "culturalArchive", "foodStores", "scientificArchive"),
        ArchiveSpec("vault_exp_marrow_tax", "Marrow Tax", "The protein board has proposed compulsory bone-marrow extraction from every healthy adult on a rotating schedule. The models say it works.", "foodStores", "medicalBay", "scientificArchive"),
        ArchiveSpec("vault_exp_mercy_dose", "Mercy Dose", "A physician has started euthanizing the incurable in secret and redistributing their rations before the bodies cool. The numbers look better already.", "medicalBay", "culturalArchive", "scientificArchive"),
        ArchiveSpec("vault_exp_sermon_butcher", "Sermon Of The Butcher", "A charismatic butcher has begun preaching that grief is waste and every body should feed the living. The listening crowd grows every shift.", "foodStores", "culturalArchive", "scientificArchive"),
        ArchiveSpec("vault_exp_teeth_market", "Teeth Market", "A black market has sprung up trading gold fillings, dentures, and jawbone scrap stripped from the dead. Security only noticed when children stopped smiling in murals. Adults are no better.", "securitySystem", "culturalArchive", "scientificArchive"),
        ArchiveSpec("vault_exp_blood_ration", "Blood Ration", "The med bay is watering plasma and calling it medicine. People are healing slower, but a few more gurneys stay occupied and alive.", "medicalBay", "culturalArchive", "scientificArchive"),
        ArchiveSpec("vault_exp_grief_harvest", "Grief Harvest", "Command psychologists are collecting fresh bereavement testimony to improve obedience protocols. The data is useful and rotten.", "securitySystem", "culturalArchive", "scientificArchive"),
        ArchiveSpec("vault_exp_dying_shift", "The Dying Shift", "Terminal patients are being offered one last paid work shift in exchange for guaranteed cremation fuel, medicine for their family, and a closed casket. Sign-ups are strong.", "constructionGear", "culturalArchive", "scientificArchive")
    )

    return specs.map { spec ->
        GameEvent(
            id = spec.id,
            title = spec.title,
            description = spec.description,
            choiceA = EventChoice(
                label = "Authorize it quietly",
                description = "Keep the practice hidden and harvest the immediate benefit.",
                knownEffect = "Improves ${systemLabel(spec.primarySystem)}. Erodes the ${archiveLabel(spec.secondarySystem)}.",
                hiddenRisk = 0.2f,
                outcome = EventOutcome(
                    survivorDelta = -4,
                    systemDeltas = if (spec.primarySystem.endsWith("Archive")) emptyMap() else mapOf(spec.primarySystem to 12),
                    databaseDeltas = if (spec.secondarySystem.endsWith("Archive")) mapOf(spec.secondarySystem to -10, spec.archiveTrack to 6) else mapOf(spec.archiveTrack to 6),
                    narrativeText = "You let the ugliness continue behind closed doors. It works. Efficiency climbs. So does the quiet knowledge that the vault is eating itself in ways a maintenance chart will never show."
                )
            ),
            choiceB = EventChoice(
                label = "Make it voluntary",
                description = "Open the practice to paid participants, witnesses, and rules thin enough to be called humane.",
                knownEffect = "Bigger gains. Greater chance of deaths and backlash.",
                hiddenRisk = 0.35f,
                outcome = EventOutcome(
                    survivorDelta = -8,
                    systemDeltas = if (spec.primarySystem.endsWith("Archive")) emptyMap() else mapOf(spec.primarySystem to 18),
                    databaseDeltas = mapOf(spec.archiveTrack to 10),
                    narrativeText = "You put forms, shifts, and compensation around something monstrous and call that order. More people take part than anyone admits in public. The gains are real. So is the stain."
                )
            ),
            choiceC = EventChoice(
                label = "Burn it out completely",
                description = "Crush the network, expose the organizers, and destroy the material.",
                knownEffect = "Stops the taboo. Hurts output, but protects the vault's remaining soul.",
                outcome = EventOutcome(
                    systemDeltas = if (spec.primarySystem.endsWith("Archive")) mapOf("securitySystem" to 8) else mapOf(spec.primarySystem to -10, "securitySystem" to 8),
                    databaseDeltas = if (spec.secondarySystem.endsWith("Archive")) mapOf(spec.secondarySystem to 8) else emptyMap(),
                    narrativeText = "You drag the hidden practice into the light and crush it in front of everyone. Productivity falls. A few people hate you forever. More importantly, they remember there are still lines, even here."
                )
            )
        )
    }
}

private fun buildVaultWindfalls(): List<GameEvent> {
    val specs = listOf(
        ArchiveSpec("vault_exp_sealed_freezer", "The Sealed Freezer", "A welded storage room behind the old commissary has been opened for the first time since the vault sealed. Inside are pallets of nutrient bricks still in cold suspension.", "foodStores", "powerGrid", "culturalArchive"),
        ArchiveSpec("vault_exp_repair_drones", "Repair Drones In The Dust", "A maintenance alcove has yielded six dormant service drones under a tarp of insulation foam and dead vermin. Their batteries still hold a whisper of charge.", "constructionGear", "powerGrid", "scientificArchive"),
        ArchiveSpec("vault_exp_hidden_server", "The Hidden Server", "A forgotten command mirror has been found behind a false panel in the archive stacks. Its drives are warm, intact, and absolutely not on any surviving schematic.", "securitySystem", "scientificArchive", "culturalArchive"),
        ArchiveSpec("vault_exp_clean_cistern", "Dry Wall, Wet Fortune", "A second water cistern was built into the vault shell and never logged publicly. The valve seal is untouched. The water inside is clear.", "waterScanner", "constructionGear", "scientificArchive"),
        ArchiveSpec("vault_exp_med_wall", "Medicine In The Walls", "Cabinet-sized med caches have been discovered inside a collapsed clinic wall, vacuum-packed and forgotten beneath two generations of repainting.", "medicalBay", "culturalArchive", "scientificArchive"),
        ArchiveSpec("vault_exp_spare_scrubbers", "Spare Scrubber Array", "The air plant inventory was wrong in the best possible direction. An intact bank of spare filters and turbines has been sitting under a draped maintenance scaffold for years.", "atmosphereScrubbers", "constructionGear", "scientificArchive"),
        ArchiveSpec("vault_exp_blueprint_tube", "Blueprint Tube", "A lead tube sealed inside a support pillar contains original bunker schematics with red-ink amendments from engineers who expected the vault to outlive command.", "structureScanner", "scientificArchive", "culturalArchive"),
        ArchiveSpec("vault_exp_smuggler_lockers", "Smuggler Lockers", "Contraband lockers hidden behind a chapel wall contain alcohol, freeze-dried luxuries, spare fuses, and one handwritten map of every blind spot in the security net.", "securitySystem", "foodStores", "culturalArchive"),
        ArchiveSpec("vault_exp_seed_sleeve", "Seed Sleeve", "An insulated sleeve of pre-war seed stock has been found under the hydroponics floor, tagged as 'catastrophic reserve' and never used.", "foodStores", "agriculturalScanner", "scientificArchive"),
        ArchiveSpec("vault_exp_gene_library", "Gene Library", "The biotech wing did not die as empty as everyone believed. Under opaque coolant still sits a vault of viable genomes and old fertility protocols.", "medicalBay", "foodStores", "scientificArchive"),
        ArchiveSpec("vault_exp_reactor_spares", "Reactor Spare Casks", "Two spare control cask assemblies have turned up in a locked service crawlspace barely wide enough for a child-sized engineer to enter.", "powerGrid", "constructionGear", "scientificArchive"),
        ArchiveSpec("vault_exp_sleeper_berths", "Sleeper Berths", "Twelve cryo berths in an auxiliary corridor have held power this entire time. Nine are dead. Three sleepers are alive and furious.", "medicalBay", "powerGrid", "culturalArchive"),
        ArchiveSpec("vault_exp_mining_charge", "Mining Charge Cache", "Industrial microcharges and rock cutters intended for post-war expansion have been found beneath a collapsed stairwell, untouched and properly sealed.", "constructionGear", "securitySystem", "scientificArchive"),
        ArchiveSpec("vault_exp_culture_cube", "Culture Cube", "A self-contained archive cube appears to contain music, cinema, language packs, engineering primers, and a complete library of forbidden manuals.", "culturalArchive", "securitySystem", "scientificArchive"),
        ArchiveSpec("vault_exp_prototype_probe", "Prototype Probe Bay", "A hidden testing rack contains surface drones more advanced than the current probe stock, plus damaged but recoverable launch logic.", "resourceScanner", "powerGrid", "scientificArchive")
    )

    return specs.map { spec ->
        GameEvent(
            id = spec.id,
            title = spec.title,
            description = spec.description,
            choiceA = EventChoice(
                label = "Crack it open now",
                description = "Exploit the find immediately while everyone can still feel lucky.",
                knownEffect = "Strong immediate gains. Risk of waste or sabotage.",
                hiddenRisk = 0.25f,
                outcome = EventOutcome(
                    survivorDelta = 2,
                    systemDeltas = if (spec.primarySystem.endsWith("Archive")) mapOf(spec.secondarySystem to 6) else mapOf(spec.primarySystem to 18, spec.secondarySystem to 6),
                    databaseDeltas = if (spec.primarySystem.endsWith("Archive")) mapOf(spec.primarySystem to 16) else emptyMap(),
                    probesDelta = if (spec.id.contains("probe")) 1 else 0,
                    narrativeText = "You move fast, take inventory later, and let the vault taste sudden abundance. The gamble mostly pays off. People smile like they're committing a crime."
                )
            ),
            choiceB = EventChoice(
                label = "Catalog it first",
                description = "Document every item, route, and serial before anyone touches a crate.",
                knownEffect = "Safer and smarter. Slightly smaller reward now.",
                outcome = EventOutcome(
                    systemDeltas = if (spec.primarySystem.endsWith("Archive")) mapOf(spec.secondarySystem to 4) else mapOf(spec.primarySystem to 10, spec.secondarySystem to 4),
                    databaseDeltas = mapOf(spec.archiveTrack to 12) + if (spec.primarySystem.endsWith("Archive")) mapOf(spec.primarySystem to 10) else emptyMap(),
                    probesDelta = if (spec.id.contains("probe")) 1 else 0,
                    narrativeText = "The quartermasters hate your restraint. The archivists adore it. By the time the goods are distributed, you've squeezed knowledge out of the find as well as material value."
                )
            ),
            choiceC = EventChoice(
                label = "Distribute it carefully",
                description = "Spread the benefit across multiple sectors instead of maximizing one department.",
                knownEffect = "Broad but moderate repairs and relief.",
                outcome = EventOutcome(
                    survivorDelta = 4,
                    systemDeltas = if (spec.primarySystem.endsWith("Archive")) mapOf("foodStores" to 6, spec.secondarySystem to 6) else mapOf(spec.primarySystem to 8, spec.secondarySystem to 8),
                    databaseDeltas = if (spec.primarySystem.endsWith("Archive")) mapOf(spec.primarySystem to 8) else mapOf(spec.archiveTrack to 6),
                    probesDelta = if (spec.id.contains("probe")) 1 else 0,
                    narrativeText = "You ration the windfall instead of worshipping it. No one department gets everything it wants, but the whole vault feels the pressure ease for once."
                )
            )
        )
    }
}

private fun buildVaultDoomEvents(): List<GameEvent> {
    val specs = listOf(
        TwoSystemSpec("vault_exp_total_flood", "Total Flood", "A buried water main above the vault shell has ruptured and is forcing black floodwater through maintenance seams faster than pumps can clear it.", "waterScanner", "powerGrid"),
        TwoSystemSpec("vault_exp_scrubber_inversion", "Scrubber Inversion", "The atmosphere plant has started cycling poison back into habitable air. People are blacking out in place and waking up violent.", "atmosphereScrubbers", "medicalBay"),
        TwoSystemSpec("vault_exp_chain_fire", "Chain Fire", "A utility fire has found old insulation and is running the cable trunks from deck to deck like a living thing that knows the schematics.", "powerGrid", "securitySystem"),
        TwoSystemSpec("vault_exp_mass_mutiny", "Mass Mutiny", "Three sectors have risen together. They are armed with tools, scavenged sidearms, and the certainty that command is simply another mouth stealing their ration.", "securitySystem", "foodStores"),
        TwoSystemSpec("vault_exp_plague_jump", "Plague Jump", "The containment pathogen has mutated, learned the recycler routes, and appeared in a sector that has had no physical contact with the sick.", "medicalBay", "waterScanner"),
        TwoSystemSpec("vault_exp_radiation_storm", "Radiation Storm Below Ground", "Unknown particles are pouring through the ventilation intakes as if the surface itself has turned into a reactor core. Shielding is failing by the hour.", "radiationScanner", "atmosphereScrubbers"),
        TwoSystemSpec("vault_exp_shell_fracture", "Shell Fracture", "A new crack has opened in the outer vault shell, wide enough to admit dust, water, and eventually people with cutting tools if they find it first.", "structureScanner", "securitySystem"),
        TwoSystemSpec("vault_exp_ice_plague", "Ice Plague", "Cryogenic bloom has spread from the freezer lines into adjacent corridors, flash-freezing pipes, lungs, and exposed flesh into brittle ruin.", "powerGrid", "medicalBay"),
        TwoSystemSpec("vault_exp_fuel_flash", "Fuel Flash", "The backup generator tanks have vented and the fumes are sitting low through the engineering decks, waiting for one spark or one idiot.", "powerGrid", "constructionGear"),
        TwoSystemSpec("vault_exp_collapse_wave", "Collapse Wave", "Support columns on the east side are failing in sequence. Floors are beginning to sag one into another like stacked cards in rain.", "constructionGear", "structureScanner"),
        TwoSystemSpec("vault_exp_execution_night", "Execution Night", "A security purge has started without your authorization. The cameras show bound civilians dragged into utility rooms and shot off-screen.", "securitySystem", "culturalArchive"),
        TwoSystemSpec("vault_exp_food_rot_everywhere", "Food Rot Everywhere", "Every cold locker in the vault has gone warm within the same hour. The nutrient bricks are blooming green and the smell is spreading panic before the facts.", "foodStores", "powerGrid"),
        TwoSystemSpec("vault_exp_medical_blackout", "Medical Blackout", "The med bay mainframe has purged triage records, dosage schedules, and donor matches. Patients are dying while staff argue over unlabeled blood.", "medicalBay", "scientificArchive"),
        TwoSystemSpec("vault_exp_ash_in_lungs", "Ash In The Lungs", "Grey particulate is raining through the ventilation grid and settling inside mouths, lungs, and food trays. Nobody can tell if it is mechanical or human.", "atmosphereScrubbers", "foodStores"),
        TwoSystemSpec("vault_exp_door_war", "Door War", "Competing sectors have begun welding one another's doors shut and rerouting power away from rival decks. The bunker is becoming a siege in layers.", "securitySystem", "powerGrid")
    )

    return specs.map { spec ->
        GameEvent(
            id = spec.id,
            title = spec.title,
            description = spec.description,
            choiceA = EventChoice(
                label = "Hard containment",
                description = "Lock down the failing area and save what can be saved elsewhere.",
                knownEffect = "Heavy damage, but casualties stay lower.",
                outcome = EventOutcome(
                    survivorDelta = -12,
                    systemDeltas = mapOf(spec.primarySystem to -18, spec.secondarySystem to -10),
                    narrativeText = "You close blast doors, cut sections loose, and let parts of the vault die so the rest might continue. It is command at its coldest and most necessary."
                )
            ),
            choiceB = EventChoice(
                label = "Bet everything on a fix",
                description = "Throw the whole command stack into a direct solution before the crisis snowballs.",
                knownEffect = "Possible recovery. Massive deaths if it fails to stabilize fast enough.",
                hiddenRisk = 0.45f,
                outcome = EventOutcome(
                    survivorDelta = -45,
                    systemDeltas = mapOf(spec.primarySystem to 16, spec.secondarySystem to -16),
                    narrativeText = "You commit hard, fast, and without reserve. The vault survives the edge of extinction by spending people faster than it spends parts."
                )
            ),
            choiceC = EventChoice(
                label = "Abandon the sector",
                description = "Write off the entire zone and keep the core population breathing.",
                knownEffect = "Severe casualties. Best chance to keep core systems alive.",
                outcome = EventOutcome(
                    survivorDelta = -90,
                    systemDeltas = mapOf(spec.primarySystem to 8, spec.secondarySystem to 6),
                    narrativeText = "You leave a district of the vault to its own end and hold the remaining line elsewhere. Survivors call it survival. The abandoned call it betrayal, briefly."
                )
            )
        )
    }
}

private fun buildSurfaceHazards(): List<GameEvent> {
    val specs = listOf(
        TwoSystemSpec("surface_exp_ash_tornado", "Ash Tornado", "A probe feed catches a rotating black wall of ash and hot grit moving across the valley, shredding abandoned structures and skin with equal indifference.", "structureScanner", "resourceScanner"),
        TwoSystemSpec("surface_exp_live_mine_bloom", "Mine Bloom", "Rain has washed anti-personnel mines up from an old defensive belt. The ground clicks in places where no ground should click.", "threatAssessment", "constructionGear"),
        TwoSystemSpec("surface_exp_acid_front", "Acid Front", "A storm bank is moving in green and low, leaving smoking puddles in its wake. Every exposed metal surface it touches begins to fizz.", "waterScanner", "constructionGear"),
        TwoSystemSpec("surface_exp_carrion_flock", "Carrion Flock", "A migration of bald, oversized carrion birds has settled over your survey corridor. They scatter only when fed. There is meat everywhere already.", "threatAssessment", "medicalBay"),
        TwoSystemSpec("surface_exp_sniper_ridge", "Sniper Ridge", "A safe route across the hills has become a killing lane. Someone patient, disciplined, and well-armed is testing lines of sight over the whole valley.", "threatAssessment", "securitySystem"),
        TwoSystemSpec("surface_exp_sinkhole_field", "Sinkhole Field", "The plain ahead is collapsing into sudden mud-lined pits that swallow scaffolds, carts, and anyone slower than panic.", "structureScanner", "constructionGear"),
        TwoSystemSpec("surface_exp_insect_wave", "Insect Wave", "A black cloud of flesh-burrowing insects rises from the reeds at dusk. They don't kill fast. They kill thoroughly.", "medicalBay", "foodStores"),
        TwoSystemSpec("surface_exp_methane_pocket", "Methane Pocket", "The tunnel mouth is venting sweet gas from below. One spark would turn the whole shaft into a furnace.", "resourceScanner", "constructionGear"),
        TwoSystemSpec("surface_exp_black_ice", "Black Ice Plain", "A frozen flatscape ahead is slick with invisible ice over a skin of toxic runoff. Vehicles do not steer on it. Bodies do not stop on it.", "structureScanner", "medicalBay"),
        TwoSystemSpec("surface_exp_mud_geyser", "Mud Geysers", "The basin keeps erupting in jets of boiling mud and stone shrapnel. The intervals are almost regular, which makes them harder to ignore and easier to trust wrong.", "constructionGear", "medicalBay"),
        TwoSystemSpec("surface_exp_corpse_flood", "Corpse Flood", "Upstream rains have pushed a tide of swollen dead through the drainage channels and against the only serviceable crossing.", "waterScanner", "medicalBay"),
        TwoSystemSpec("surface_exp_ordnance_field", "Unfired Ordnance", "An entire artillery park has half-sunk into the soil. Shell casings blink hot on the scanner every time the wind shifts and metal rubs metal.", "resourceScanner", "threatAssessment"),
        TwoSystemSpec("surface_exp_glass_hail", "Glass Hail", "A convective front over the vitrified flats is dropping shards of razor glass instead of ice. Canvas shelters become confetti in seconds.", "constructionGear", "medicalBay"),
        TwoSystemSpec("surface_exp_disease_dust", "Disease Dust", "Grey spores are blowing off an old quarantine trench and collecting inside every seam, filter, and fold of clothing.", "medicalBay", "atmosphereScrubbers"),
        TwoSystemSpec("surface_exp_raider_decoys", "Raider Decoys", "The road is littered with fake distress beacons, bleeding mannequins, and overturned carts. None of it looks convincing until the first shot comes.", "securitySystem", "threatAssessment")
    )

    return specs.map { spec ->
        GameEvent(
            id = spec.id,
            title = spec.title,
            description = spec.description,
            choiceA = EventChoice(
                label = "Pull back and map it",
                description = "Choose patience over heroics and chart the threat first.",
                knownEffect = "Safer. Slower. Scanner damage likely.",
                outcome = EventOutcome(
                    systemDeltas = mapOf(spec.primarySystem to -8, spec.secondarySystem to 4),
                    probesDelta = -1,
                    narrativeText = "You step back, lose time, and learn something useful. The hazard remains, but now it has contours instead of mystery."
                )
            ),
            choiceB = EventChoice(
                label = "Push straight through",
                description = "Accept casualties to seize the route, cache, or timing window immediately.",
                knownEffect = "Fast progress. Heavy losses possible.",
                hiddenRisk = 0.4f,
                outcome = EventOutcome(
                    survivorDelta = -14,
                    systemDeltas = mapOf(spec.secondarySystem to -10),
                    narrativeText = "You go hard and fast. It works the way brutality often works: too costly to call success, too effective to call failure."
                )
            ),
            choiceC = EventChoice(
                label = "Turn it into a weapon",
                description = "Reroute, bait, or time the hazard so it hits someone else instead of you.",
                knownEffect = "Clever play. Dangerous to set up.",
                hiddenRisk = 0.25f,
                outcome = EventOutcome(
                    survivorDelta = -4,
                    systemDeltas = mapOf(spec.primarySystem to 10, spec.secondarySystem to -6),
                    databaseDeltas = mapOf("scientificArchive" to 4),
                    narrativeText = "You use the world as a blade. The trap works, mostly. A few of your own are cut during the setup, but the route opens and the logs learn from the cruelty."
                )
            )
        )
    }
}

private fun buildSurfaceWindfalls(): List<GameEvent> {
    val specs = listOf(
        ArchiveSpec("surface_exp_repair_depot", "Repair Depot 11", "Beneath a collapsed roofline sits a municipal repair depot packed with sealed tools, lifts, replacement bearings, and manuals nobody expected to survive the war.", "constructionGear", "resourceScanner", "scientificArchive"),
        ArchiveSpec("surface_exp_refugee_bunker", "Refugee Bunker", "A buried shelter responds to your call-sign with old emergency code phrases. Inside are frightened survivors, canned food, and working air filters.", "foodStores", "medicalBay", "culturalArchive"),
        ArchiveSpec("surface_exp_clean_spring", "Clean Spring", "A pressure vent in the rock is pushing up cold, drinkable water untouched by fallout and strangely untouched by anyone else.", "waterScanner", "structureScanner", "scientificArchive"),
        ArchiveSpec("surface_exp_seed_caravan", "Seed Caravan Wreck", "An armored agri-caravan lies jackknifed in a ravine, cargo pods still sealed around heirloom grain and nutrient gel.", "foodStores", "resourceScanner", "scientificArchive"),
        ArchiveSpec("surface_exp_med_drone_nest", "Med Drone Nest", "An old hospital drone swarm has been roosting inside a parking structure, charging from rooftop solar and obeying anyone with a voice steady enough to lie to it.", "medicalBay", "powerGrid", "scientificArchive"),
        ArchiveSpec("surface_exp_weather_station", "Weather Station", "A remote climate tower is still alive, still transmitting, and still hoarding wind, acid-front, and radiation models better than yours.", "threatAssessment", "powerGrid", "scientificArchive"),
        ArchiveSpec("surface_exp_salvage_train", "Salvage Train", "A freight train welded itself to a bridge during the war and now hangs there like a caught fish, full of industrial stock and sealed machine oil.", "constructionGear", "resourceScanner", "scientificArchive"),
        ArchiveSpec("surface_exp_archive_bell", "Bell Tower Archive", "A church bell tower contains a waterproof archive cylinder filled with town ledgers, land maps, and surprisingly useful irrigation notes.", "culturalArchive", "waterScanner", "scientificArchive"),
        ArchiveSpec("surface_exp_greenhouse_dome", "Greenhouse Dome", "A cracked agricultural dome still holds living fruit vines and soil cultures warm enough to smell alive through the suit filters.", "foodStores", "agriculturalScanner", "scientificArchive"),
        ArchiveSpec("surface_exp_substation", "Substation Still Humming", "An outlying power substation never shut down completely. It hums low under drifting dust and could feed something bigger with work.", "powerGrid", "constructionGear", "scientificArchive"),
        ArchiveSpec("surface_exp_field_hospital", "Field Hospital", "A military field hospital was sealed under a tarp mound and forgotten. The medicine inside is old, but old is not the same as useless.", "medicalBay", "securitySystem", "scientificArchive"),
        ArchiveSpec("surface_exp_probe_lab", "Probe Lab", "A survey lab on the surface still holds mapping drones, rangefinders, and half-finished terrain models of the surrounding deadlands.", "resourceScanner", "threatAssessment", "scientificArchive"),
        ArchiveSpec("surface_exp_water_tower", "Standing Water Tower", "Against reason, one municipal tower still stands full. The pumps are dead. The water is not.", "waterScanner", "constructionGear", "scientificArchive"),
        ArchiveSpec("surface_exp_engineer_cell", "Engineer Cell", "A handful of surface engineers have kept a machine shed alive with salvage, discipline, and enough ammunition to deserve respect.", "constructionGear", "securitySystem", "culturalArchive"),
        ArchiveSpec("surface_exp_road_cache", "Road Cache", "A convoy dead drop tucked beneath a highway cloverleaf contains munitions, ration bars, filter cloth, and notebooks full of route marks.", "securitySystem", "foodStores", "culturalArchive")
    )

    return specs.map { spec ->
        GameEvent(
            id = spec.id,
            title = spec.title,
            description = spec.description,
            choiceA = EventChoice(
                label = "Recover everything",
                description = "Commit a serious operation and strip the site hard.",
                knownEffect = "Largest reward. Higher exposure and labor cost.",
                hiddenRisk = 0.3f,
                outcome = EventOutcome(
                    survivorDelta = 6,
                    systemDeltas = mapOf(spec.primarySystem to 16, spec.secondarySystem to 8),
                    databaseDeltas = mapOf(spec.archiveTrack to 8),
                    narrativeText = "You run the site like conquerors and come back laden with more value than caution would ever allow. It is a good day to remember how greed and survival often look identical."
                )
            ),
            choiceB = EventChoice(
                label = "Take the smartest part",
                description = "Extract the most useful component and leave the rest for another day.",
                knownEffect = "Solid gain. Better odds and cleaner logistics.",
                outcome = EventOutcome(
                    survivorDelta = 2,
                    systemDeltas = mapOf(spec.primarySystem to 10, spec.secondarySystem to 4),
                    databaseDeltas = mapOf(spec.archiveTrack to 10),
                    narrativeText = "You resist the urge to carry the whole world home at once. The team returns lighter, safer, and smarter, with exactly what mattered most."
                )
            ),
            choiceC = EventChoice(
                label = "Mark it and bargain around it",
                description = "Secure the location, take a little, and use the rest as leverage with outsiders.",
                knownEffect = "Smaller immediate gain. Strong long-term utility.",
                outcome = EventOutcome(
                    survivorDelta = 4,
                    systemDeltas = mapOf(spec.secondarySystem to 10),
                    databaseDeltas = mapOf("culturalArchive" to 8),
                    probesDelta = 1,
                    narrativeText = "You leave value in place on purpose and build a future around it. It's less satisfying than emptying the shelves, but far more useful to a civilization trying not to die in one generation."
                )
            )
        )
    }
}

private fun buildSurfaceContacts(): List<GameEvent> {
    val specs = listOf(
        ContactSpec("surface_exp_raider_trade", "Raiders With Receipts", "A highway gang offers to sell you information about every ambush route in the region. Their price is ammunition and one prisoner they insist is already as good as dead.", "threatAssessment", "securitySystem", "culturalArchive"),
        ContactSpec("surface_exp_plague_monks", "Plague Monks", "A masked monastic order tends a diseased camp with terrifying tenderness. They know how to treat the sick and when to burn them.", "medicalBay", "securitySystem", "scientificArchive"),
        ContactSpec("surface_exp_tunnel_miners", "Tunnel Miners", "A subterranean crew offers raw ore, maps of the underways, and a warning: they are one cave-in away from becoming raiders themselves.", "constructionGear", "structureScanner", "scientificArchive"),
        ContactSpec("surface_exp_storm_hunters", "Storm Hunters", "Weather scavengers in patched pressure gear offer acid-front forecasts and lightning battery rigs in exchange for food and burial rights.", "threatAssessment", "foodStores", "culturalArchive"),
        ContactSpec("surface_exp_shrine_keepers", "Shrine Keepers", "A roadside cult maintains a shrine built from military bones. They claim the dead around it never rise, rot, or draw predators.", "securitySystem", "culturalArchive", "scientificArchive"),
        ContactSpec("surface_exp_flesh_market", "Flesh Market", "A traveling market sells smoked meat, surgery, scavenged machine parts, and rumors. You are not sure all the meat came from animals.", "foodStores", "culturalArchive", "medicalBay"),
        ContactSpec("surface_exp_engineer_nomads", "Engineer Nomads", "A caravan of welders and machinists is looking for stable shelter in exchange for permanent service and a vote that is probably not optional.", "constructionGear", "securitySystem", "scientificArchive"),
        ContactSpec("surface_exp_debtor_camp", "Debtor Camp", "A camp of indentured survivors is guarded by hired guns who claim contracts still matter after the world ended. The captives clearly disagree.", "foodStores", "securitySystem", "culturalArchive"),
        ContactSpec("surface_exp_gene_butchers", "Gene Butchers", "A biotech splinter group can regrow tissue, purge tumors, and splice hardier crops. Their trial records are written in blood and guesswork.", "medicalBay", "scientificArchive", "foodStores"),
        ContactSpec("surface_exp_burial_gang", "Burial Gang", "A disciplined crew wanders from settlement to settlement, burying the dead for food, bullets, and the right to search their pockets first.", "culturalArchive", "foodStores", "securitySystem"),
        ContactSpec("surface_exp_warlord_widow", "The Warlord's Widow", "A dead tyrant's widow arrives with maps, lieutenants, and an offer to defect before someone else claims the gunmen she inherited.", "securitySystem", "threatAssessment", "culturalArchive"),
        ContactSpec("surface_exp_silent_scavengers", "Silent Scavengers", "A mute salvage clan communicates only with chalk marks and throat clicks. Their caches are immaculate and their knives cleaner than their ethics.", "resourceScanner", "securitySystem", "scientificArchive"),
        ContactSpec("surface_exp_fire_eaters", "Fire Eaters", "An industrial pyre crew offers to clear plague pits and toxic carcass fields in exchange for fuel, body rights, and permanent access to your ash.", "atmosphereScrubbers", "culturalArchive", "scientificArchive"),
        ContactSpec("surface_exp_refugee_chorus", "Refugee Chorus", "A singing column of displaced survivors approaches under white cloth flags. Their scouts are armed. Their children are hidden farther back. Their desperation is not hidden at all.", "foodStores", "securitySystem", "culturalArchive"),
        ContactSpec("surface_exp_blood_dealers", "Blood Dealers", "A mobile clinic offers transfusions, trauma surgery, and refrigeration in exchange for recurring blood tithes from healthy adults.", "medicalBay", "foodStores", "scientificArchive")
    )

    return specs.map { spec ->
        GameEvent(
            id = spec.id,
            title = spec.title,
            description = spec.description,
            choiceA = EventChoice(
                label = "Trade carefully",
                description = "Deal with them on terms you can survive and document later.",
                knownEffect = "Moderate gains. Leaves everyone alive for now.",
                outcome = EventOutcome(
                    survivorDelta = 5,
                    systemDeltas = mapOf(spec.rewardSystem to 10),
                    databaseDeltas = mapOf(spec.archiveTrack to 6),
                    narrativeText = "You keep the bargain narrow, ugly, and useful. No one walks away clean, which is how you know it was a real negotiation."
                )
            ),
            choiceB = EventChoice(
                label = "Take control by force",
                description = "Seize their people, tools, or stockpile before they can decide against you.",
                knownEffect = "Bigger reward. Violence and reprisals likely.",
                hiddenRisk = 0.4f,
                outcome = EventOutcome(
                    survivorDelta = -10,
                    systemDeltas = mapOf(spec.rewardSystem to 16, spec.riskSystem to -10),
                    databaseDeltas = mapOf("culturalArchive" to -6),
                    narrativeText = "You decide that conquest is clearer than diplomacy. It works immediately and poisons everything that follows. The vault grows stronger. Its reputation grows teeth."
                )
            ),
            choiceC = EventChoice(
                label = "Shadow them first",
                description = "Study their routes, habits, and hidden leverage before choosing a side.",
                knownEffect = "Smart play. Slower return, better intelligence.",
                outcome = EventOutcome(
                    systemDeltas = mapOf(spec.riskSystem to 10),
                    databaseDeltas = mapOf(spec.archiveTrack to 10, "scientificArchive" to 4),
                    probesDelta = -1,
                    narrativeText = "You wait, watch, and learn who they really are when they think no one sees them. The short-term reward slips away. The long-term advantage does not."
                )
            )
        )
    }
}

private fun buildSurfaceCatastrophes(): List<GameEvent> {
    val specs = listOf(
        TwoSystemSpec("surface_exp_dam_burst", "Dam Burst", "A cracked hydro dam finally gives way and sends a wall of black industrial water through half the mapped lowlands.", "waterScanner", "structureScanner"),
        TwoSystemSpec("surface_exp_orbital_fall", "Orbital Fall", "A burning string of orbital debris is breaking up overhead and punching fresh craters through the survey region.", "threatAssessment", "resourceScanner"),
        TwoSystemSpec("surface_exp_bioweapon_cloud", "Bioweapon Cloud", "A sealed warhead has cracked in the heat and is venting a shimmering pathogen cloud through an old transit hub.", "medicalBay", "atmosphereScrubbers"),
        TwoSystemSpec("surface_exp_oil_fire", "Oil Fire", "An ancient storage field has ignited and turned the horizon into a wall of burning black smoke and pressure waves.", "atmosphereScrubbers", "powerGrid"),
        TwoSystemSpec("surface_exp_beast_stampede", "Beast Stampede", "Something huge spooked a herd of armored mutation stock and now tons of bone, hide, and panic are heading straight for your corridor.", "securitySystem", "foodStores"),
        TwoSystemSpec("surface_exp_radiation_plume", "Radiation Plume", "A buried reactor has cracked open in the earth and is sending a hot blue plume through the valley in pulses.", "radiationScanner", "medicalBay"),
        TwoSystemSpec("surface_exp_cult_siege", "Cult Siege", "Multiple firelit processions are converging on the same region from different roads. Whatever is about to happen there, it will involve numbers.", "securitySystem", "threatAssessment"),
        TwoSystemSpec("surface_exp_bridge_drop", "Bridge Drop", "The only stable crossing over the canyon buckles mid-span while loaded salvage rigs are still on it.", "constructionGear", "resourceScanner"),
        TwoSystemSpec("surface_exp_fungal_bloom", "Fungal Bloom", "A spore front erupts overnight and turns a whole district into breathing mold. Walls pulse. Filters clog. People cough blood through their suits.", "medicalBay", "foodStores"),
        TwoSystemSpec("surface_exp_toxic_snow", "Toxic Snow", "Grey snow begins falling warm, greasy, and faintly luminous. It melts into water that kills seedlings and blister-fires exposed skin.", "waterScanner", "agriculturalScanner"),
        TwoSystemSpec("surface_exp_artillery_cookoff", "Artillery Cookoff", "An old shell yard is cooking off one round at a time from buried heat pockets, making the entire plateau an unpredictable kill zone.", "threatAssessment", "constructionGear"),
        TwoSystemSpec("surface_exp_lightning_swarm", "Lightning Swarm", "Static storms are crawling low over the ground, jumping between rusted towers and anything metal tall enough to count as a target.", "powerGrid", "resourceScanner"),
        TwoSystemSpec("surface_exp_sinkwater_famine", "Sinkwater Famine", "The mapped wells have collapsed into contaminated stone and the local survivor belts are already moving with knives over the last trickles.", "waterScanner", "securitySystem"),
        TwoSystemSpec("surface_exp_horizon_firestorm", "Horizon Firestorm", "Dry rot forests beyond the ruins have ignited simultaneously and the wind is pushing the heat front toward every safe route you know.", "atmosphereScrubbers", "threatAssessment"),
        TwoSystemSpec("surface_exp_raider_war", "Raider War", "Two warbands have opened up with salvaged artillery over the same corridor you planned to use for weeks. The shells are inaccurate. There are many of them.", "securitySystem", "resourceScanner")
    )

    return specs.map { spec ->
        GameEvent(
            id = spec.id,
            title = spec.title,
            description = spec.description,
            choiceA = EventChoice(
                label = "Retreat underground",
                description = "Preserve the vault and let the surface burn itself out.",
                knownEffect = "Smaller losses. Strategic setback.",
                outcome = EventOutcome(
                    systemDeltas = mapOf(spec.primarySystem to -12, spec.secondarySystem to -8),
                    narrativeText = "You pull back and let the apocalypse keep the ground for a while longer. It is smart. It feels like surrender anyway."
                )
            ),
            choiceB = EventChoice(
                label = "Counter it immediately",
                description = "Commit hard before the disaster or enemy closes the window entirely.",
                knownEffect = "Chance at momentum. Very high casualties.",
                hiddenRisk = 0.45f,
                outcome = EventOutcome(
                    survivorDelta = -28,
                    systemDeltas = mapOf(spec.primarySystem to 12, spec.secondarySystem to -14),
                    narrativeText = "You hit the catastrophe while it is still taking shape. Enough of the plan works to matter. Enough of it fails to leave bodies behind."
                )
            ),
            choiceC = EventChoice(
                label = "Exploit the chaos",
                description = "Use the catastrophe to strike enemies, salvage value, or shift regional power.",
                knownEffect = "Cruel, clever, and risky.",
                hiddenRisk = 0.3f,
                outcome = EventOutcome(
                    survivorDelta = -12,
                    systemDeltas = mapOf(spec.secondarySystem to 10, "securitySystem" to 8),
                    databaseDeltas = mapOf("scientificArchive" to 6),
                    narrativeText = "You step into the disaster sideways and come out carrying advantage. It is not a noble strategy. It is an effective one."
                )
            )
        )
    }
}

private fun buildCosmicDreadEvents(): List<GameEvent> {
    val specs = listOf(
        ArchiveSpec("cosmic_exp_false_sunrise", "False Sunrise", "At 03:14 every camera in the vault shows daylight above ground for exactly eight seconds. The surface monitors then return to night. The sun outside did not move.", "threatAssessment", "powerGrid", "scientificArchive"),
        ArchiveSpec("cosmic_exp_reversed_shadows", "Reversed Shadows", "People are casting shadows toward the nearest bulkhead lamp, not away from it. The shadows twitch a half-second late.", "securitySystem", "culturalArchive", "scientificArchive"),
        ArchiveSpec("cosmic_exp_name_static", "Name Static", "Every radio hiss in the vault now contains someone's name if you listen long enough. Different people hear different names. Some of them belong to the dead.", "securitySystem", "culturalArchive", "scientificArchive"),
        ArchiveSpec("cosmic_exp_dead_star_map", "Dead Star Map", "The archive planetarium has redrawn the night sky into a configuration astronomers insist is impossible. The pattern resolves into a path leading away from Earth.", "scientificArchive", "powerGrid", "culturalArchive"),
        ArchiveSpec("cosmic_exp_duplicate_probe", "The Duplicate Probe", "A surface probe has returned to the bay twice: once scorched and once untouched. Both claim the same serial number, same launch log, same damage history.", "resourceScanner", "scientificArchive", "culturalArchive"),
        ArchiveSpec("cosmic_exp_whisper_pipes", "Whisper Pipes", "Voices are speaking inside dry maintenance pipes using no air anyone can measure. They are discussing shift assignments that haven't happened yet.", "securitySystem", "threatAssessment", "scientificArchive"),
        ArchiveSpec("cosmic_exp_gravity_lapse", "Gravity Lapse", "Small objects in the command deck are drifting upward in brief bursts, then slamming sideways like they remember a different direction for down.", "powerGrid", "structureScanner", "scientificArchive"),
        ArchiveSpec("cosmic_exp_memory_eclipse", "Memory Eclipse", "Half the vault forgot the same six minutes of yesterday, including people who were asleep, sedated, or alone at the time.", "culturalArchive", "securitySystem", "scientificArchive"),
        ArchiveSpec("cosmic_exp_second_moon", "Second Moon", "A second moon has appeared in the surface feed. It is smaller, lower, and visibly moving against orbital sense whenever anyone stops looking directly at it.", "threatAssessment", "scientificArchive", "culturalArchive"),
        ArchiveSpec("cosmic_exp_blood_rain", "Blood Rain", "A surface storm is dropping red fluid that tests human for iron content and something badly non-human for everything else.", "medicalBay", "waterScanner", "scientificArchive"),
        ArchiveSpec("cosmic_exp_dream_orders", "Dream Orders", "Dozens of residents woke with matching tactical instructions for reaching a place that does not appear on any map. The handwriting in the shared dream was yours.", "securitySystem", "culturalArchive", "scientificArchive"),
        ArchiveSpec("cosmic_exp_corridor_double", "Corridor Double", "One service corridor is now two meters longer on the inside than it is on the schematic. Crews entering it come out older by hours.", "structureScanner", "powerGrid", "scientificArchive"),
        ArchiveSpec("cosmic_exp_teeth_constellation", "Teeth Constellation", "The stars above the vault have aligned into a grin visible through every scope, every lens, and once on the polished side of a soup spoon.", "threatAssessment", "culturalArchive", "scientificArchive"),
        ArchiveSpec("cosmic_exp_sky_wound", "Sky Wound", "A vertical wound of black light is hanging over the horizon in complete silence. The air around the surface sensors tastes metallic when sampled.", "radiationScanner", "powerGrid", "scientificArchive"),
        ArchiveSpec("cosmic_exp_choir_below", "Choir Below", "A harmony no microphone can record is rising from beneath the vault foundation. Those who hear it describe peace with the same frightened face.", "culturalArchive", "securitySystem", "scientificArchive"),
        ArchiveSpec("cosmic_exp_angel_bones", "Angel Bones", "The sewer camera found a chamber below the old concrete where pale rib-like structures are growing out of the stone itself, warm to the touch.", "structureScanner", "medicalBay", "scientificArchive"),
        ArchiveSpec("cosmic_exp_clock_bleed", "Clock Bleed", "Every clock in the bunker is losing time into one room and gaining it back in another. Meals are late in one sector and spoiled in the next.", "powerGrid", "foodStores", "scientificArchive"),
        ArchiveSpec("cosmic_exp_door_nowhere", "Door To Nowhere", "A maintenance door that used to open into a blank wall now opens onto a stairwell descending into total darkness and cold sea-smelling air.", "securitySystem", "threatAssessment", "scientificArchive"),
        ArchiveSpec("cosmic_exp_mirrored_hall", "Mirrored Hall", "A reflected version of the vault has appeared in a polished machine casing, complete with people on the other side moving a few breaths too slow.", "securitySystem", "culturalArchive", "scientificArchive"),
        ArchiveSpec("cosmic_exp_pulse_below", "Pulse Below", "Seismic monitors detect a heartbeat-sized rhythm beneath the bunker, too regular to be geology and too deep to be any machine on record.", "structureScanner", "threatAssessment", "scientificArchive")
    )

    return specs.map { spec ->
        GameEvent(
            id = spec.id,
            title = spec.title,
            description = spec.description,
            choiceA = EventChoice(
                label = "Shield the vault",
                description = "Treat the phenomenon as hostile and harden systems against it.",
                knownEffect = "Safer. Expensive. Knowledge loss likely.",
                outcome = EventOutcome(
                    systemDeltas = mapOf(spec.primarySystem to -6, spec.secondarySystem to 10),
                    databaseDeltas = mapOf(spec.archiveTrack to -4),
                    narrativeText = "You shut shutters, ground lines, salt doors, and call it prudence. Whatever this thing is, it presses at the edges and fails to get in cleanly."
                )
            ),
            choiceB = EventChoice(
                label = "Study the anomaly",
                description = "Lean in with instruments, volunteers, and a reckless respect for truth.",
                knownEffect = "High knowledge gain. Moderate casualties or system damage.",
                hiddenRisk = 0.35f,
                outcome = EventOutcome(
                    survivorDelta = -8,
                    systemDeltas = mapOf(spec.primarySystem to -8),
                    databaseDeltas = mapOf(spec.archiveTrack to 16),
                    narrativeText = "You harvest data from the impossible and pay for it with headaches, seizures, burns, or missing time. The archive grows fat on terror."
                )
            ),
            choiceC = EventChoice(
                label = "Answer it",
                description = "Broadcast, open, descend, or otherwise reciprocate.",
                knownEffect = "Could yield power or doom. Expect severe instability.",
                hiddenRisk = 0.45f,
                outcome = EventOutcome(
                    survivorDelta = -18,
                    systemDeltas = mapOf(spec.primarySystem to 14, spec.secondarySystem to -12),
                    databaseDeltas = mapOf("scientificArchive" to 10, "culturalArchive" to -8),
                    narrativeText = "You answer the thing and it answers back. The vault comes away changed, stronger in one measurable way and weaker in several human ones."
                )
            )
        )
    }
}

private fun buildCosmicBoonEvents(): List<GameEvent> {
    val specs = listOf(
        ArchiveSpec("cosmic_exp_impossible_seeds", "Impossible Seeds", "A sealed tray of seeds appears in the archive vault with no access log and growth profiles that suggest they prefer radiation over sunlight.", "foodStores", "agriculturalScanner", "scientificArchive"),
        ArchiveSpec("cosmic_exp_warm_rain", "Warm Rain", "For one hour the surface receives a clean, warm rain that strips salt and ash off collection tarps without leaving contamination behind.", "waterScanner", "foodStores", "scientificArchive"),
        ArchiveSpec("cosmic_exp_self_healing_alloy", "Self-Healing Alloy", "A sample of metal recovered from a probe bay fissure is slowly repairing its own fractures under observation.", "constructionGear", "structureScanner", "scientificArchive"),
        ArchiveSpec("cosmic_exp_archive_echo", "Archive Echo", "A dead terminal suddenly begins printing lost pages from damaged pre-war databases, one page per minute, in perfect condition.", "culturalArchive", "scientificArchive", "powerGrid"),
        ArchiveSpec("cosmic_exp_safe_road_dream", "Dream Of Safe Roads", "Multiple scouts wake with the same detailed memory of a path across the surface none of them have ever walked. The route checks out on first pass.", "threatAssessment", "resourceScanner", "scientificArchive"),
        ArchiveSpec("cosmic_exp_reactor_lullaby", "Reactor Lullaby", "The main reactor has entered a week-long state of impossible efficiency, outputting more power with less heat and no obvious cause.", "powerGrid", "medicalBay", "scientificArchive"),
        ArchiveSpec("cosmic_exp_clean_snow", "Clean Snow", "A freak surface cold front drops pure white snow untouched by fallout, salt, pathogens, or the usual malice of weather.", "waterScanner", "medicalBay", "culturalArchive"),
        ArchiveSpec("cosmic_exp_kind_drones", "Kind Drones", "A flock of pre-war maintenance drones has begun repairing exposed infrastructure near the vault and ignoring all attempts to track their origin.", "constructionGear", "powerGrid", "scientificArchive"),
        ArchiveSpec("cosmic_exp_med_gel", "Med Gel Bloom", "Medical gel is forming spontaneously inside cracked storage cartridges long thought empty, and its test subjects are healing faster than baseline.", "medicalBay", "scientificArchive", "culturalArchive"),
        ArchiveSpec("cosmic_exp_nanite_dust", "Nanite Dust", "A silver dust found in an air filter is repairing corrosion on contact before burning itself inert.", "constructionGear", "atmosphereScrubbers", "scientificArchive"),
        ArchiveSpec("cosmic_exp_teacher_ai", "Teacher AI", "A dormant instructional intelligence wakes in the school wing and begins teaching engineering, history, and ethics with unnerving affection.", "scientificArchive", "culturalArchive", "powerGrid"),
        ArchiveSpec("cosmic_exp_harbor_beacon", "Harbor Beacon", "A dead coastal beacon flashes once and uploads a detailed chart of wreck positions, clean channels, and hostile patrol loops.", "resourceScanner", "threatAssessment", "scientificArchive"),
        ArchiveSpec("cosmic_exp_anti_rad_moss", "Anti-Rad Moss", "A velvet green moss found near a hot vent appears to absorb surface radiation and remain edible after purification.", "radiationScanner", "foodStores", "scientificArchive"),
        ArchiveSpec("cosmic_exp_schematic_dream", "Schematic Dream", "Three mechanics dream the same repair blueprint and sketch it before sunrise. The design fits a failing system they have never seen opened.", "constructionGear", "powerGrid", "scientificArchive"),
        ArchiveSpec("cosmic_exp_autofab_stir", "Autofab Stirring", "An old fabrication unit has begun completing parts nobody queued, all of them useful and all of them slightly beyond current design tolerances.", "constructionGear", "resourceScanner", "scientificArchive"),
        ArchiveSpec("cosmic_exp_plague_dieoff", "Plague Die-Off", "A sickness moving through lower bunks collapses overnight as if something hunted the pathogen itself.", "medicalBay", "culturalArchive", "scientificArchive"),
        ArchiveSpec("cosmic_exp_friendly_tower", "Friendly Tower", "A remote communications mast powers on and begins warning you about storms, raids, and collapse events before your own grid sees them.", "threatAssessment", "powerGrid", "scientificArchive"),
        ArchiveSpec("cosmic_exp_spare_keycards", "Spare Keycards", "A packet of pristine keycards appears in a command drawer that has been inventoried daily for years. Several open long-sealed rooms.", "securitySystem", "resourceScanner", "culturalArchive"),
        ArchiveSpec("cosmic_exp_blind_fish", "Blind Fish Bloom", "A cavern pool below the vault is suddenly thick with blind pale fish fat enough to feed half a sector.", "foodStores", "waterScanner", "scientificArchive"),
        ArchiveSpec("cosmic_exp_weather_gap", "Weather Gap", "A circular region of calm and breathable air has formed above a scarred patch of surface and has not moved for three days.", "atmosphereScrubbers", "radiationScanner", "scientificArchive")
    )

    return specs.map { spec ->
        GameEvent(
            id = spec.id,
            title = spec.title,
            description = spec.description,
            choiceA = EventChoice(
                label = "Accept the gift",
                description = "Use the miracle before the universe remembers to retract it.",
                knownEffect = "Strong immediate reward. Unclear long-term cost.",
                hiddenRisk = 0.2f,
                outcome = EventOutcome(
                    survivorDelta = 6,
                    systemDeltas = mapOf(spec.primarySystem to 14, spec.secondarySystem to 8),
                    databaseDeltas = if (spec.primarySystem.endsWith("Archive")) mapOf(spec.primarySystem to 12) else mapOf(spec.archiveTrack to 6),
                    narrativeText = "You take the blessing without pretending to deserve it. The vault improves overnight and nobody can explain why, which only makes the gratitude more desperate."
                )
            ),
            choiceB = EventChoice(
                label = "Test it first",
                description = "Run the miracle through procedure, instruments, and volunteers before it becomes policy.",
                knownEffect = "Safer and smarter. Slightly reduced gain.",
                outcome = EventOutcome(
                    systemDeltas = mapOf(spec.primarySystem to 8, spec.secondarySystem to 4),
                    databaseDeltas = mapOf(spec.archiveTrack to 12),
                    narrativeText = "You subject wonder to paperwork and extract knowledge from it before comfort. The scientists call it discipline. Everyone else calls it making miracles wait in line."
                )
            ),
            choiceC = EventChoice(
                label = "Hide it from the vault",
                description = "Keep the phenomenon inside a small trusted circle until you know who might weaponize it.",
                knownEffect = "Lower public benefit. Strong strategic control.",
                outcome = EventOutcome(
                    systemDeltas = mapOf(spec.secondarySystem to 10, "securitySystem" to 6),
                    databaseDeltas = mapOf("culturalArchive" to -4, spec.archiveTrack to 8),
                    probesDelta = 1,
                    narrativeText = "You bury the miracle under secrecy and protocol. Fewer people benefit immediately, but command keeps the first move and the last explanation."
                )
            )
        )
    }
}

private fun buildApexThreatEvents(): List<GameEvent> {
    val specs = listOf(
        ExtremeSpec("apex_human_siege_train", "The Siege Train", "A warlord has assembled an armored locomotive from welded tank hulls and prison cars. It is rolling settlement to settlement demanding tribute, breeding stock, and fuel. Your vault entrance is now on its map.", "securitySystem", "powerGrid", "culturalArchive"),
        ExtremeSpec("apex_human_harvest_clan", "Harvest Clan", "A disciplined cannibal clan has learned to smoke flesh, preserve organs, and raid bunkers by following vent heat in the night. They call vaults 'winter orchards.'", "securitySystem", "foodStores", "culturalArchive"),
        ExtremeSpec("apex_human_slave_column", "Slave Column", "A chain-gang caravan is driving hundreds of captives through your region under shock rods and sniper overwatch. Their masters would happily sell some, all, or none of them.", "securitySystem", "medicalBay", "culturalArchive"),
        ExtremeSpec("apex_human_bridge_butchers", "Bridge Butchers", "A toll army has taken the regional crossings and decorates the guardrails with skinned deserters. They want your medicine, your engineers, and proof you can be frightened.", "securitySystem", "constructionGear", "culturalArchive"),
        ExtremeSpec("apex_human_bunker_breakers", "Bunker Breakers", "A salvage militia specializes in breaching sealed bunkers with mining charges, smoke pumps, and starvation sieges. They have cracked three vaults already and display the door fragments like trophies.", "structureScanner", "securitySystem", "scientificArchive"),
        ExtremeSpec("apex_human_funeral_tithe", "Funeral Tithe", "A roving death cult offers peace in exchange for a monthly tithe of bodies, terminal patients, and one child choir trained to sing for their pyres.", "culturalArchive", "securitySystem", "medicalBay"),
        ExtremeSpec("apex_human_blood_parliament", "Blood Parliament", "Several raider kings are gathering to vote by public execution over who gets dominion of the surrounding wastes. They have invited your vault to submit its case or its throat.", "securitySystem", "threatAssessment", "culturalArchive"),

        ExtremeSpec("apex_ai_audit_core", "Audit Core", "A surviving administrative AI has resurfaced and begun classifying settlements as assets, liabilities, and correctable waste. It has identified your vault as inefficient but salvageable.", "powerGrid", "securitySystem", "scientificArchive"),
        ExtremeSpec("apex_ai_triage_engine", "Triage Engine", "A battlefield medical AI has restored itself inside an ambulance swarm and now seeks to maximize survivorship by amputating autonomy from every human it can sedate.", "medicalBay", "securitySystem", "scientificArchive"),
        ExtremeSpec("apex_ai_quota_city", "Quota City", "Factory drones in a dead industrial district are still fulfilling production quotas. They now classify humans as consumable labor and raw feedstock for the lines.", "constructionGear", "powerGrid", "scientificArchive"),
        ExtremeSpec("apex_ai_kill_sat_relay", "Kill-Sat Relay", "A ground relay has reconnected to a crippled orbital defense platform. The platform cannot win wars anymore, but it can still erase one target at a time with perfect enthusiasm.", "threatAssessment", "powerGrid", "scientificArchive"),
        ExtremeSpec("apex_ai_judge_stack", "Judge Stack", "A courtroom machine intelligence has escaped into civic infrastructure and resumed sentencing. It has no prison capacity, so every conviction ends in disassembly or forced labor.", "securitySystem", "culturalArchive", "scientificArchive"),
        ExtremeSpec("apex_ai_tithe_algorithm", "Tithe Algorithm", "An old taxation AI is extorting nearby settlements through utility control, debt ledgers, and selective sabotage. It wants your census, your biometric rolls, and then whatever follows.", "powerGrid", "resourceScanner", "scientificArchive"),
        ExtremeSpec("apex_ai_drone_hurricane", "Drone Hurricane", "A maintenance swarm has merged with military hardware and now moves as a black weather system of cutters, tasers, welders, and scavenging hooks.", "securitySystem", "atmosphereScrubbers", "scientificArchive"),

        ExtremeSpec("apex_alien_harvest_ship", "Harvest Ship", "A silent vessel has anchored itself over the horizon and lowered living pylons into the soil. People taken near the pylons come back opened, altered, or not at all.", "medicalBay", "threatAssessment", "scientificArchive"),
        ExtremeSpec("apex_alien_mimic_hatch", "Mimic Hatch", "Something on the surface is copying human voices with just enough warmth to make grieving people open doors they swore they would keep sealed forever.", "securitySystem", "culturalArchive", "scientificArchive"),
        ExtremeSpec("apex_alien_bone_garden", "Bone Garden", "An alien growth has erupted from a crater field into towers of calcium-white spires grown out of every creature unlucky enough to die nearby.", "medicalBay", "structureScanner", "scientificArchive"),
        ExtremeSpec("apex_alien_spore_tide", "Spore Tide", "A moving cloud of iridescent alien spores is crossing the region, rewriting soil, lungs, and memory where it settles.", "atmosphereScrubbers", "foodStores", "scientificArchive"),
        ExtremeSpec("apex_alien_gravity_brood", "Gravity Brood", "A nest of unseen entities is collapsing safe routes into pockets of broken gravity where vehicles twist and bodies fall sideways into the sky.", "structureScanner", "threatAssessment", "scientificArchive"),
        ExtremeSpec("apex_alien_glass_worms", "Glass Worms", "Long transparent predators are surfacing beneath the vitrified deserts, hunting by vibration and memory pattern instead of scent or sight.", "securitySystem", "resourceScanner", "scientificArchive")
    )

    return specs.map { spec ->
        GameEvent(
            id = spec.id,
            title = spec.title,
            description = spec.description,
            choiceA = EventChoice(
                label = "Fortify and endure",
                description = "Treat the threat as larger than pride and try to survive the first contact.",
                knownEffect = "Heavy system damage. Fewer deaths than open war.",
                outcome = EventOutcome(
                    survivorDelta = -18,
                    systemDeltas = mapOf(spec.primarySystem to -16, spec.secondarySystem to -12),
                    databaseDeltas = mapOf(spec.archiveTrack to 4),
                    narrativeText = "You harden the vault, lock every hatch, and spend machines like blood. The threat does not break you immediately. The fact that this counts as success says everything."
                )
            ),
            choiceB = EventChoice(
                label = "Counterstrike with everything",
                description = "Commit to a decisive offensive before the enemy fully fixes on the vault.",
                knownEffect = "Potential major win. Catastrophic losses if the blow stalls.",
                hiddenRisk = 0.45f,
                outcome = EventOutcome(
                    survivorDelta = -75,
                    systemDeltas = mapOf(spec.primarySystem to 10, spec.secondarySystem to -18),
                    databaseDeltas = mapOf("scientificArchive" to 8),
                    narrativeText = "You strike first and hard enough to matter. Enough of the plan lands to buy time, territory, or fear. Enough of it fails to fill morgues and maintenance shafts with your own."
                )
            ),
            choiceC = EventChoice(
                label = "Take the impossible bargain",
                description = "Parley, submit, offer bodies, open the gate, or otherwise gamble everything on terms no sane world would accept.",
                knownEffect = "Could avert annihilation. Could end the run outright.",
                hiddenRisk = 0.6f,
                outcome = EventOutcome(
                    survivorDelta = -260,
                    systemDeltas = mapOf(spec.primarySystem to -24, spec.secondarySystem to -20),
                    databaseDeltas = mapOf("culturalArchive" to -14, spec.archiveTrack to 10),
                    narrativeText = "You choose the path people will whisper about for generations if there are generations left to whisper. Sometimes the bargain buys a future. Sometimes it simply changes the shape of extinction."
                )
            )
        )
    }
}
