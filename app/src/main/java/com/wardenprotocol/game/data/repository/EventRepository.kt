package com.ivarna.wardenprotocol.data.repository

import android.content.Context
import com.ivarna.wardenprotocol.R
import com.ivarna.wardenprotocol.data.model.EventChoice
import com.ivarna.wardenprotocol.data.model.EventOutcome
import com.ivarna.wardenprotocol.data.model.GameEvent

class EventRepository(private val context: Context? = null) {

    private val expandedCatalog: ExpandedEventCatalog by lazy { ExpandedEventCatalog(context) }

    fun getAllEvents(): List<GameEvent> = (
        vaultEvents + expandedCatalog.vaultEvents +
            surfaceEvents + expandedCatalog.surfaceEvents +
            cosmicEvents + expandedCatalog.cosmicEvents +
            expandedCatalog.apexThreatEvents
        ).map(GameEvent::withExpandedBriefing)

    // ── VAULT INTERNAL EVENTS ─────────────────────────────────────────────────

    private val vaultEvents = listOf(

        GameEvent(
            id = "vault_power_failure",
            title = context?.getString(R.string.event_vault_power_failure_title) ?: "Lights Out",
            description = context?.getString(R.string.event_vault_power_failure_description) ?: "The main power grid sputters and dies with a sound like a dying breath. Emergency red lighting casts long shadows across the corridors. Your people are already whispering — darkness breeds panic faster than any plague.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_vault_power_failure_choice_a_label) ?: "Reroute auxiliary power",
                description = context?.getString(R.string.event_vault_power_failure_choice_a_desc) ?: "Divert power from non-essential systems to restore lighting and life support.",
                knownEffect = context?.getString(R.string.event_vault_power_failure_choice_a_effect) ?: "Restores power. Strains food storage cooling.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("powerGrid" to 15, "foodStores" to -8),
                    narrativeText = context?.getString(R.string.event_vault_power_failure_choice_a_outcome) ?: "The lights flicker back on. Cheers echo through the vault — brief, fragile hope. The food stores will pay the price."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_vault_power_failure_choice_b_label) ?: "Send engineers to the reactor",
                description = context?.getString(R.string.event_vault_power_failure_choice_b_desc) ?: "Dispatch your best technicians to diagnose and repair the root cause.",
                knownEffect = context?.getString(R.string.event_vault_power_failure_choice_b_effect) ?: "Full repair possible. Takes time, risks engineer casualties.",
                hiddenRisk = 0.35f,
                outcome = EventOutcome(
                    survivorDelta = -2,
                    systemDeltas = mapOf("powerGrid" to 30),
                    narrativeText = context?.getString(R.string.event_vault_power_failure_choice_b_outcome) ?: "The engineers work through the dark. Two don't come back — a fault in the conduit. But the grid hums strong again."
                )
            ),
            choiceC = EventChoice(
                label = context?.getString(R.string.event_vault_power_failure_choice_c_label) ?: "Ration power strictly",
                description = context?.getString(R.string.event_vault_power_failure_choice_c_desc) ?: "Lock down all but critical systems and wait for a natural resolution.",
                knownEffect = context?.getString(R.string.event_vault_power_failure_choice_c_effect) ?: "Conserves power. Morale will suffer.",
                hiddenRisk = 0.3f,
                outcome = EventOutcome(
                    survivorDelta = -1,
                    systemDeltas = mapOf("powerGrid" to 5, "foodStores" to -5, "medicalBay" to -5),
                    narrativeText = context?.getString(R.string.event_vault_power_failure_choice_c_outcome) ?: "Three days of cold and dark. One elderly resident doesn't survive the chill. The vault endures, but it remembers."
                )
            )
        ),

        GameEvent(
            id = "vault_coup_attempt",
            title = context?.getString(R.string.event_vault_coup_attempt_title) ?: "The Warden's Chair",
            description = context?.getString(R.string.event_vault_coup_attempt_description) ?: "Commander Holt has been quietly gathering loyalists for weeks. Tonight, armed with stolen security codes and grievances, his faction moves on the command center. You have minutes before they reach the door.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_vault_coup_attempt_choice_a_label) ?: "Seal the command center and negotiate",
                description = context?.getString(R.string.event_vault_coup_attempt_choice_a_desc) ?: "Lock down and open a channel — hear their demands before blood is spilled.",
                knownEffect = context?.getString(R.string.event_vault_coup_attempt_choice_a_effect) ?: "Avoids immediate violence. May require concessions.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("securitySystem" to -10),
                    narrativeText = context?.getString(R.string.event_vault_coup_attempt_choice_a_outcome) ?: "Holt's voice crackles through the intercom. His demands are not unreasonable. You make promises you may not keep — but the vault stays whole."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_vault_coup_attempt_choice_b_label) ?: "Activate security countermeasures",
                description = context?.getString(R.string.event_vault_coup_attempt_choice_b_desc) ?: "Deploy vault security to intercept and detain the conspirators by force.",
                knownEffect = context?.getString(R.string.event_vault_coup_attempt_choice_b_effect) ?: "Suppresses coup. Casualties likely on both sides.",
                hiddenRisk = 0.4f,
                outcome = EventOutcome(
                    survivorDelta = -4,
                    systemDeltas = mapOf("securitySystem" to 10),
                    narrativeText = context?.getString(R.string.event_vault_coup_attempt_choice_b_outcome) ?: "The corridors run red. Holt is in chains. Four are dead — two of his, two of yours. The vault is quieter now, and colder."
                )
            ),
            choiceC = EventChoice(
                label = context?.getString(R.string.event_vault_coup_attempt_choice_c_label) ?: "Step down temporarily",
                description = context?.getString(R.string.event_vault_coup_attempt_choice_c_desc) ?: "Yield command under protest, buying time to rebuild support.",
                knownEffect = context?.getString(R.string.event_vault_coup_attempt_choice_c_effect) ?: "No immediate casualties. You lose authority.",
                hiddenRisk = 0.45f,
                outcome = EventOutcome(
                    survivorDelta = -1,
                    systemDeltas = mapOf("securitySystem" to -20),
                    narrativeText = context?.getString(R.string.event_vault_coup_attempt_choice_c_outcome) ?: "Holt takes the chair. His first order is to ration your rations. You watch from the shadows, waiting. One of your allies disappears."
                )
            )
        ),

        GameEvent(
            id = "vault_plague_outbreak",
            title = context?.getString(R.string.event_vault_plague_outbreak_title) ?: "The Coughing Ward",
            description = context?.getString(R.string.event_vault_plague_outbreak_description) ?: "It started in Sector 7 — a wet, rattling cough that spreads faster than rumor. The medical bay is overwhelmed within forty-eight hours. Whatever this is, it didn't come from outside.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_vault_plague_outbreak_choice_a_label) ?: "Quarantine Sector 7 immediately",
                description = context?.getString(R.string.event_vault_plague_outbreak_choice_a_desc) ?: "Seal the infected sector and restrict all movement.",
                knownEffect = context?.getString(R.string.event_vault_plague_outbreak_choice_a_effect) ?: "Slows spread. Trapped residents may die without care.",
                hiddenRisk = 0.3f,
                outcome = EventOutcome(
                    survivorDelta = -3,
                    systemDeltas = mapOf("medicalBay" to -10),
                    narrativeText = context?.getString(R.string.event_vault_plague_outbreak_choice_a_outcome) ?: "The quarantine holds. Three die behind the sealed doors — you hear them until you don't. The rest of the vault is spared."
                )
            ),
            choiceB = EventChoice(
                label = "Mobilize all medical resources",
                description = context?.getString(R.string.event_vault_plague_outbreak_choice_b_desc) ?: "Throw everything at treatment — burn through supplies to save as many as possible.",
                knownEffect = context?.getString(R.string.event_vault_plague_outbreak_choice_b_effect) ?: "Saves more lives short-term. Depletes medical stores.",
                outcome = EventOutcome(
                    survivorDelta = -1,
                    systemDeltas = mapOf("medicalBay" to -25),
                    narrativeText = context?.getString(R.string.event_vault_plague_outbreak_choice_b_outcome) ?: "Your doctors work without sleep. One survivor. The medical bay is gutted — if something else comes, you'll have nothing left."
                )
            ),
            choiceC = EventChoice(
                label = context?.getString(R.string.event_vault_plague_outbreak_choice_c_label) ?: "Research the pathogen",
                description = context?.getString(R.string.event_vault_plague_outbreak_choice_c_desc) ?: "Divert resources to identifying the disease before treating it.",
                knownEffect = context?.getString(R.string.event_vault_plague_outbreak_choice_c_effect) ?: "May yield a cure. Costs time and lives.",
                hiddenRisk = 0.4f,
                outcome = EventOutcome(
                    survivorDelta = -5,
                    systemDeltas = mapOf("medicalBay" to 15),
                    databaseDeltas = mapOf("scientificArchive" to 10),
                    narrativeText = context?.getString(R.string.event_vault_plague_outbreak_choice_c_outcome) ?: "Five die while you study. But the pathogen is catalogued, understood, defeated. The archive grows heavier with hard-won knowledge."
                )
            )
        ),

        GameEvent(
            id = "vault_birth_surge",
            title = context?.getString(R.string.event_vault_birth_surge_title) ?: "New Voices",
            description = context?.getString(R.string.event_vault_birth_surge_description) ?: "Seven births in a single week — an unprecedented surge. The vault rings with infant cries for the first time in years. It is beautiful and terrifying in equal measure.",
            choiceA = EventChoice(
                label = "Celebrate and allocate extra rations",
                description = context?.getString(R.string.event_vault_birth_surge_choice_a_desc) ?: "Boost morale with a formal ceremony and increased food allocation for new mothers.",
                knownEffect = context?.getString(R.string.event_vault_birth_surge_choice_a_effect) ?: "Morale rises. Food stores decrease.",
                outcome = EventOutcome(
                    survivorDelta = 7,
                    systemDeltas = mapOf("foodStores" to -10),
                    narrativeText = context?.getString(R.string.event_vault_birth_surge_choice_a_outcome) ?: "Laughter fills the corridors. Seven new names are carved into the vault wall. The food stores thin, but hope is a kind of nourishment too."
                )
            ),
            choiceB = EventChoice(
                label = "Implement strict resource planning",
                description = context?.getString(R.string.event_vault_birth_surge_choice_b_desc) ?: "Register the births and immediately adjust ration schedules to compensate.",
                knownEffect = context?.getString(R.string.event_vault_birth_surge_choice_b_effect) ?: "Preserves resources. Resentment may follow.",
                outcome = EventOutcome(
                    survivorDelta = 7,
                    systemDeltas = mapOf("foodStores" to -3),
                    narrativeText = context?.getString(R.string.event_vault_birth_surge_choice_b_outcome) ?: "The ledger is updated before the umbilical cords are cut. Efficient. Cold. The vault survives another generation."
                )
            )
        ),

        GameEvent(
            id = "vault_reactor_leak",
            title = context?.getString(R.string.event_vault_reactor_leak_title) ?: "Sector Green Goes Red",
            description = context?.getString(R.string.event_vault_reactor_leak_description) ?: "Radiation alarms scream from the reactor level. A hairline fracture in coolant pipe 4-C is venting slow death into the lower corridors. You have hours before it becomes irreversible.",
            choiceA = EventChoice(
                label = "Send a repair crew immediately",
                description = context?.getString(R.string.event_vault_reactor_leak_choice_a_desc) ?: "Volunteer engineers suit up and enter the hot zone to seal the fracture.",
                knownEffect = context?.getString(R.string.event_vault_reactor_leak_choice_a_effect) ?: "Stops the leak. Crew faces radiation exposure.",
                hiddenRisk = 0.4f,
                outcome = EventOutcome(
                    survivorDelta = -2,
                    systemDeltas = mapOf("powerGrid" to 5, "medicalBay" to -10),
                    narrativeText = context?.getString(R.string.event_vault_reactor_leak_choice_a_outcome) ?: "The crew seals the fracture. Two develop acute radiation sickness within days. The medical bay fights for them — and loses."
                )
            ),
            choiceB = EventChoice(
                label = "Evacuate lower levels and contain remotely",
                description = context?.getString(R.string.event_vault_reactor_leak_choice_b_desc) ?: "Clear the affected sectors and attempt a remote patch via the control systems.",
                knownEffect = context?.getString(R.string.event_vault_reactor_leak_choice_b_effect) ?: "No crew casualties. Partial fix only — leak may worsen.",
                hiddenRisk = 0.35f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("powerGrid" to -15, "atmosphereScrubbers" to -20),
                    narrativeText = context?.getString(R.string.event_vault_reactor_leak_choice_b_outcome) ?: "The remote patch holds — barely. Radiation seeps into the scrubber system. The air tastes faintly metallic for weeks."
                )
            ),
            choiceC = EventChoice(
                label = context?.getString(R.string.event_vault_reactor_leak_choice_c_label) ?: "Shut down the reactor entirely",
                description = context?.getString(R.string.event_vault_reactor_leak_choice_c_desc) ?: "Emergency shutdown. Safe, but the vault goes dark and cold.",
                knownEffect = context?.getString(R.string.event_vault_reactor_leak_choice_c_effect) ?: "Eliminates radiation risk. Power loss is severe.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("powerGrid" to -40, "foodStores" to -15),
                    narrativeText = context?.getString(R.string.event_vault_reactor_leak_choice_c_outcome) ?: "Darkness. The reactor goes cold and so does everything else. Food spoils in the warming storage units. You buy safety with hunger."
                )
            )
        ),

        GameEvent(
            id = "vault_food_shortage",
            title = context?.getString(R.string.event_vault_food_shortage_title) ?: "Empty Shelves",
            description = context?.getString(R.string.event_vault_food_shortage_description) ?: "The hydroponics yield failed for the second consecutive cycle. The food stores are at critical levels. Faces are gaunt, tempers are short, and the rationing board has been vandalized twice this week.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_vault_food_shortage_choice_a_label) ?: "Implement emergency half-rations",
                description = context?.getString(R.string.event_vault_food_shortage_choice_a_desc) ?: "Cut all rations by fifty percent and stretch supplies as far as possible.",
                knownEffect = context?.getString(R.string.event_vault_food_shortage_choice_a_effect) ?: "Extends food supply. Health and morale will decline.",
                hiddenRisk = 0.3f,
                outcome = EventOutcome(
                    survivorDelta = -2,
                    systemDeltas = mapOf("foodStores" to 20, "medicalBay" to -10),
                    narrativeText = context?.getString(R.string.event_vault_food_shortage_choice_a_outcome) ?: "Hunger becomes the vault's new rhythm. Two elderly residents slip away quietly. The rest endure — hollowed out but alive."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_vault_food_shortage_choice_b_label) ?: "Sacrifice the seed reserves for immediate food",
                description = context?.getString(R.string.event_vault_food_shortage_choice_b_desc) ?: "Process the agricultural seed bank into emergency rations.",
                knownEffect = context?.getString(R.string.event_vault_food_shortage_choice_b_effect) ?: "Immediate food relief. Future harvests compromised.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("foodStores" to 30, "agriculturalScanner" to -20),
                    narrativeText = context?.getString(R.string.event_vault_food_shortage_choice_b_outcome) ?: "Bellies are full tonight. But the seed vault is empty. Whatever grows on the surface now, you'll have nothing to plant."
                )
            ),
            choiceC = EventChoice(
                label = context?.getString(R.string.event_vault_food_shortage_choice_c_label) ?: "Send a surface foraging team",
                description = context?.getString(R.string.event_vault_food_shortage_choice_c_desc) ?: "Dispatch scouts to scavenge pre-war food caches from nearby ruins.",
                knownEffect = context?.getString(R.string.event_vault_food_shortage_choice_c_effect) ?: "May recover food. Team faces surface dangers.",
                hiddenRisk = 0.45f,
                outcome = EventOutcome(
                    survivorDelta = -1,
                    systemDeltas = mapOf("foodStores" to 25),
                    narrativeText = context?.getString(R.string.event_vault_food_shortage_choice_c_outcome) ?: "The team returns with canned goods and one fewer member. Nobody asks what happened. The food is distributed without ceremony."
                )
            )
        ),

        GameEvent(
            id = "vault_water_contamination",
            title = context?.getString(R.string.event_vault_water_contamination_title) ?: "The Taste of Rust",
            description = context?.getString(R.string.event_vault_water_contamination_description) ?: "The water recycler has been compromised — heavy metal contamination is spreading through the drinking supply. Three residents are already showing neurological symptoms. The source is somewhere in the filtration stack.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_vault_water_contamination_choice_a_label) ?: "Flush and replace the filtration media",
                description = context?.getString(R.string.event_vault_water_contamination_choice_a_desc) ?: "Shut down water recycling and perform a full media replacement.",
                knownEffect = context?.getString(R.string.event_vault_water_contamination_choice_a_effect) ?: "Fixes contamination. Water supply offline for 48 hours.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("waterScanner" to 20, "foodStores" to -5),
                    narrativeText = context?.getString(R.string.event_vault_water_contamination_choice_a_outcome) ?: "Two days of thirst. Rationed water from emergency reserves. When the taps run clear again, people drink until they're sick from relief."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_vault_water_contamination_choice_b_label) ?: "Treat the affected residents and monitor",
                description = context?.getString(R.string.event_vault_water_contamination_choice_b_desc) ?: "Focus medical resources on the symptomatic and continue using the water supply.",
                knownEffect = context?.getString(R.string.event_vault_water_contamination_choice_b_effect) ?: "Keeps water flowing. Contamination may spread further.",
                hiddenRisk = 0.4f,
                outcome = EventOutcome(
                    survivorDelta = -2,
                    systemDeltas = mapOf("medicalBay" to -15),
                    narrativeText = context?.getString(R.string.event_vault_water_contamination_choice_b_outcome) ?: "The contamination spreads before you catch it. Two more fall ill and don't recover. The filtration is fixed too late for them."
                )
            )
        ),

        GameEvent(
            id = "vault_structural_collapse",
            title = context?.getString(R.string.event_vault_structural_collapse_title) ?: "The Ceiling Speaks",
            description = context?.getString(R.string.event_vault_structural_collapse_description) ?: "A deep groan reverberates through the vault's bones — then a section of Corridor 12 collapses without warning. Dust fills the air. Someone is screaming from beneath the rubble.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_vault_structural_collapse_choice_a_label) ?: "Launch immediate rescue operation",
                description = context?.getString(R.string.event_vault_structural_collapse_choice_a_desc) ?: "Send construction crews in to dig out survivors before the structure shifts further.",
                knownEffect = context?.getString(R.string.event_vault_structural_collapse_choice_a_effect) ?: "May save trapped survivors. Risks further collapse.",
                hiddenRisk = 0.35f,
                outcome = EventOutcome(
                    survivorDelta = -1,
                    systemDeltas = mapOf("constructionGear" to -15, "structureScanner" to -10),
                    narrativeText = context?.getString(R.string.event_vault_structural_collapse_choice_a_outcome) ?: "You pull two people out alive. One rescuer is lost when a secondary collapse hits. The corridor is shored up with whatever you have left."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_vault_structural_collapse_choice_b_label) ?: "Seal the corridor and assess damage",
                description = context?.getString(R.string.event_vault_structural_collapse_choice_b_desc) ?: "Prioritize structural integrity — seal the section and survey before acting.",
                knownEffect = context?.getString(R.string.event_vault_structural_collapse_choice_b_effect) ?: "Prevents further collapse. Trapped survivors may not survive the wait.",
                outcome = EventOutcome(
                    survivorDelta = -3,
                    systemDeltas = mapOf("structureScanner" to 10),
                    narrativeText = context?.getString(R.string.event_vault_structural_collapse_choice_b_outcome) ?: "The survey reveals three dead in the rubble. The corridor is stable now. You add their names to the wall and move on."
                )
            ),
            choiceC = EventChoice(
                label = context?.getString(R.string.event_vault_structural_collapse_choice_c_label) ?: "Reinforce adjacent sections preemptively",
                description = context?.getString(R.string.event_vault_structural_collapse_choice_c_desc) ?: "Use construction resources to stabilize surrounding corridors before they fail too.",
                knownEffect = context?.getString(R.string.event_vault_structural_collapse_choice_c_effect) ?: "Prevents future collapses. Abandons those currently trapped.",
                outcome = EventOutcome(
                    survivorDelta = -2,
                    systemDeltas = mapOf("constructionGear" to -10, "structureScanner" to 15),
                    narrativeText = context?.getString(R.string.event_vault_structural_collapse_choice_c_outcome) ?: "The reinforcement holds. Two are lost in the original collapse — a price paid for the safety of hundreds. The math is brutal and correct."
                )
            )
        ),

        GameEvent(
            id = "vault_air_scrubber_failure",
            title = context?.getString(R.string.event_vault_air_scrubber_failure_title) ?: "Thin Air",
            description = context?.getString(R.string.event_vault_air_scrubber_failure_description) ?: "CO2 levels are climbing. The atmospheric scrubbers are failing — a cascade of clogged filters and burned-out fans. The air is growing thick and slow. Headaches are universal. Judgment is impaired.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_vault_air_scrubber_failure_choice_a_label) ?: "Emergency manual filter replacement",
                description = context?.getString(R.string.event_vault_air_scrubber_failure_choice_a_desc) ?: "Pull every available hand to manually replace scrubber filters around the clock.",
                knownEffect = context?.getString(R.string.event_vault_air_scrubber_failure_choice_a_effect) ?: "Restores air quality. Exhausts workforce.",
                outcome = EventOutcome(
                    survivorDelta = -1,
                    systemDeltas = mapOf("atmosphereScrubbers" to 25),
                    narrativeText = context?.getString(R.string.event_vault_air_scrubber_failure_choice_a_outcome) ?: "Twelve hours of frantic work. One person collapses from CO2 exposure and doesn't wake up. The air clears. Everyone breathes a little easier — except her."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_vault_air_scrubber_failure_choice_b_label) ?: "Reduce population density in affected zones",
                description = context?.getString(R.string.event_vault_air_scrubber_failure_choice_b_desc) ?: "Consolidate residents into the best-ventilated sections of the vault.",
                knownEffect = context?.getString(R.string.event_vault_air_scrubber_failure_choice_b_effect) ?: "Buys time. Crowding creates new problems.",
                hiddenRisk = 0.3f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("atmosphereScrubbers" to 10, "securitySystem" to -5),
                    narrativeText = context?.getString(R.string.event_vault_air_scrubber_failure_choice_b_outcome) ?: "Cramped quarters breed conflict. A fight breaks out over sleeping space. The scrubbers are repaired eventually, but the bruises linger."
                )
            )
        ),

        GameEvent(
            id = "vault_medical_epidemic",
            title = context?.getString(R.string.event_vault_medical_epidemic_title) ?: "Fever Season",
            description = context?.getString(R.string.event_vault_medical_epidemic_description) ?: "A hemorrhagic fever tears through the vault's children first, then the elderly. The medical bay is a war zone of cots and desperate faces. Your chief physician hasn't slept in four days.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_vault_medical_epidemic_choice_a_label) ?: "Administer experimental antiviral",
                description = context?.getString(R.string.event_vault_medical_epidemic_choice_a_desc) ?: "Use the untested compound synthesized from pre-war research notes.",
                knownEffect = context?.getString(R.string.event_vault_medical_epidemic_choice_a_effect) ?: "May cure the fever. Unknown side effects.",
                hiddenRisk = 0.45f,
                outcome = EventOutcome(
                    survivorDelta = -2,
                    systemDeltas = mapOf("medicalBay" to 20),
                    databaseDeltas = mapOf("scientificArchive" to 5),
                    narrativeText = context?.getString(R.string.event_vault_medical_epidemic_choice_a_outcome) ?: "The antiviral works on most. Two react badly and are gone within hours. The formula is refined and archived — a terrible lesson learned."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_vault_medical_epidemic_choice_b_label) ?: "Strict supportive care only",
                description = context?.getString(R.string.event_vault_medical_epidemic_choice_b_desc) ?: "Manage symptoms, maintain hydration, and let the immune system fight.",
                knownEffect = context?.getString(R.string.event_vault_medical_epidemic_choice_b_effect) ?: "Safe approach. Higher mortality without active treatment.",
                outcome = EventOutcome(
                    survivorDelta = -5,
                    systemDeltas = mapOf("medicalBay" to -10),
                    narrativeText = context?.getString(R.string.event_vault_medical_epidemic_choice_b_outcome) ?: "Five funerals in a week. The fever breaks on its own eventually. Your physician weeps in the corridor when it's over. You don't interrupt."
                )
            ),
            choiceC = EventChoice(
                label = context?.getString(R.string.event_vault_medical_epidemic_choice_c_label) ?: "Request surface medical supply run",
                description = context?.getString(R.string.event_vault_medical_epidemic_choice_c_desc) ?: "Send a team to the ruins of the old hospital district for antibiotics and antivirals.",
                knownEffect = context?.getString(R.string.event_vault_medical_epidemic_choice_c_effect) ?: "Could secure real medicine. Surface team at risk.",
                hiddenRisk = 0.4f,
                outcome = EventOutcome(
                    survivorDelta = -1,
                    systemDeltas = mapOf("medicalBay" to 30),
                    narrativeText = context?.getString(R.string.event_vault_medical_epidemic_choice_c_outcome) ?: "The team returns with a cache of sealed pharmaceuticals. One didn't make it back. The medicine saves a dozen. The math is grim but favorable."
                )
            )
        ),

        GameEvent(
            id = "vault_security_mutiny",
            title = context?.getString(R.string.event_vault_security_mutiny_title) ?: "The Guards Walk Off",
            description = context?.getString(R.string.event_vault_security_mutiny_description) ?: "Your security detail has laid down their weapons and barricaded themselves in the armory. Their grievance: they haven't seen their families in the residential sectors for three weeks due to your lockdown orders.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_vault_security_mutiny_choice_a_label) ?: "End the lockdown and negotiate",
                description = context?.getString(R.string.event_vault_security_mutiny_choice_a_desc) ?: "Lift the residential restrictions and meet with the security team directly.",
                knownEffect = context?.getString(R.string.event_vault_security_mutiny_choice_a_effect) ?: "Resolves mutiny. Lockdown benefits are lost.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("securitySystem" to 15),
                    narrativeText = context?.getString(R.string.event_vault_security_mutiny_choice_a_outcome) ?: "The armory doors open. Tearful reunions fill the corridors. The lockdown is over — and so is whatever it was protecting against. You hope."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_vault_security_mutiny_choice_b_label) ?: "Cut power to the armory",
                description = context?.getString(R.string.event_vault_security_mutiny_choice_b_desc) ?: "Force them out by making the armory uninhabitable.",
                knownEffect = context?.getString(R.string.event_vault_security_mutiny_choice_b_effect) ?: "Ends standoff quickly. Damages trust permanently.",
                hiddenRisk = 0.35f,
                outcome = EventOutcome(
                    survivorDelta = -1,
                    systemDeltas = mapOf("securitySystem" to -10, "powerGrid" to -5),
                    narrativeText = context?.getString(R.string.event_vault_security_mutiny_choice_b_outcome) ?: "They come out angry and humiliated. One makes a choice you can't take back. The security force is intact but hollow — they follow orders now, nothing more."
                )
            )
        ),

        GameEvent(
            id = "vault_knowledge_loss",
            title = context?.getString(R.string.event_vault_knowledge_loss_title) ?: "The Archive Burns",
            description = context?.getString(R.string.event_vault_knowledge_loss_description) ?: "A short circuit in the server room has destroyed a significant portion of the cultural archive. Irreplaceable records — music, literature, history — reduced to corrupted data. The vault's memory is bleeding out.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_vault_knowledge_loss_choice_a_label) ?: "Mobilize residents to reconstruct from memory",
                description = context?.getString(R.string.event_vault_knowledge_loss_choice_a_desc) ?: "Organize community sessions to record what people remember before it fades.",
                knownEffect = context?.getString(R.string.event_vault_knowledge_loss_choice_a_effect) ?: "Partial recovery. Time-consuming but meaningful.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("powerGrid" to -5),
                    databaseDeltas = mapOf("culturalArchive" to 15),
                    narrativeText = context?.getString(R.string.event_vault_knowledge_loss_choice_a_outcome) ?: "For two weeks, the vault tells stories. Songs are sung and written down. Not everything is recovered — but what remains is alive in a way data never was."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_vault_knowledge_loss_choice_b_label) ?: "Prioritize server hardware recovery",
                description = context?.getString(R.string.event_vault_knowledge_loss_choice_b_desc) ?: "Focus technical resources on salvaging corrupted data from damaged drives.",
                knownEffect = context?.getString(R.string.event_vault_knowledge_loss_choice_b_effect) ?: "May recover more data. Requires technical resources.",
                hiddenRisk = 0.3f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("powerGrid" to -10),
                    databaseDeltas = mapOf("culturalArchive" to 20, "scientificArchive" to 5),
                    narrativeText = context?.getString(R.string.event_vault_knowledge_loss_choice_b_outcome) ?: "The technicians recover fragments — partial texts, degraded audio. It's not everything. It's enough to remember what was lost."
                )
            ),
            choiceC = EventChoice(
                label = context?.getString(R.string.event_vault_knowledge_loss_choice_c_label) ?: "Accept the loss and move forward",
                description = context?.getString(R.string.event_vault_knowledge_loss_choice_c_desc) ?: "Redirect resources to present survival rather than past preservation.",
                knownEffect = context?.getString(R.string.event_vault_knowledge_loss_choice_c_effect) ?: "No resource cost. Cultural memory is permanently diminished.",
                outcome = EventOutcome(
                    databaseDeltas = mapOf("culturalArchive" to -10),
                    narrativeText = context?.getString(R.string.event_vault_knowledge_loss_choice_c_outcome) ?: "The loss is logged and filed. Life continues. Somewhere in the vault, an old man hums a song no one else remembers, and then he stops."
                )
            )
        ),

        GameEvent(
            id = "vault_suicide_crisis",
            title = context?.getString(R.string.event_vault_suicide_crisis_title) ?: "The Weight of Walls",
            description = context?.getString(R.string.event_vault_suicide_crisis_description) ?: "Three residents have taken their own lives in the past month. The vault's psychologist — if you can call one person a department — is overwhelmed. The walls are closing in on people who've never seen the sky.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_vault_suicide_crisis_choice_a_label) ?: "Establish a community support network",
                description = context?.getString(R.string.event_vault_suicide_crisis_choice_a_desc) ?: "Train volunteers in crisis support and create open forums for residents to speak.",
                knownEffect = context?.getString(R.string.event_vault_suicide_crisis_choice_a_effect) ?: "Improves morale long-term. Requires time and organization.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("medicalBay" to 10),
                    databaseDeltas = mapOf("culturalArchive" to 5),
                    narrativeText = context?.getString(R.string.event_vault_suicide_crisis_choice_a_outcome) ?: "The forums are awkward at first. Then raw. Then honest. People cry in front of strangers and feel less alone. The vault breathes differently."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_vault_suicide_crisis_choice_b_label) ?: "Increase work assignments to reduce idle time",
                description = context?.getString(R.string.event_vault_suicide_crisis_choice_b_desc) ?: "Fill the schedule — busy hands and minds have less room for despair.",
                knownEffect = context?.getString(R.string.event_vault_suicide_crisis_choice_b_effect) ?: "Reduces crisis incidents. May mask underlying issues.",
                hiddenRisk = 0.3f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("constructionGear" to 10, "foodStores" to 5),
                    narrativeText = context?.getString(R.string.event_vault_suicide_crisis_choice_b_outcome) ?: "The vault is more productive than ever. The grief doesn't disappear — it just goes underground, where you can't see it. For now, that's enough."
                )
            )
        ),

        GameEvent(
            id = "vault_child_disappearance",
            title = context?.getString(R.string.event_vault_child_disappearance_title) ?: "Gone from Bunk 14",
            description = context?.getString(R.string.event_vault_child_disappearance_description) ?: "A nine-year-old girl has vanished from the residential sector. No alarms triggered, no witnesses. The vault is small — there are only so many places to hide. Or be hidden.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_vault_child_disappearance_choice_a_label) ?: "Full vault lockdown and search",
                description = context?.getString(R.string.event_vault_child_disappearance_choice_a_desc) ?: "Seal all sectors and conduct a systematic room-by-room search.",
                knownEffect = context?.getString(R.string.event_vault_child_disappearance_choice_a_effect) ?: "Thorough search. Disrupts vault operations significantly.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("securitySystem" to 10, "foodStores" to -5),
                    narrativeText = context?.getString(R.string.event_vault_child_disappearance_choice_a_outcome) ?: "She's found in a maintenance crawlspace — alive, frightened, hiding from a man whose name you now know. Justice is swift and final in the vault."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_vault_child_disappearance_choice_b_label) ?: "Quiet investigation to avoid panic",
                description = context?.getString(R.string.event_vault_child_disappearance_choice_b_desc) ?: "Conduct a discreet search while maintaining normal vault operations.",
                knownEffect = context?.getString(R.string.event_vault_child_disappearance_choice_b_effect) ?: "Avoids panic. May be too slow.",
                hiddenRisk = 0.4f,
                outcome = EventOutcome(
                    survivorDelta = -1,
                    systemDeltas = mapOf("securitySystem" to -5),
                    narrativeText = context?.getString(R.string.event_vault_child_disappearance_choice_b_outcome) ?: "The quiet investigation takes too long. She is found, but not in time. The vault mourns in a silence that feels like guilt."
                )
            )
        ),

        GameEvent(
            id = "vault_generator_overload",
            title = context?.getString(R.string.event_vault_generator_overload_title) ?: "Burning From Within",
            description = context?.getString(R.string.event_vault_generator_overload_description) ?: "The backup generator has overloaded and caught fire in the mechanical bay. Suppression systems are partially functional. Smoke is filling the eastern corridors and the fire is spreading.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_vault_generator_overload_choice_a_label) ?: "Activate all suppression systems",
                description = context?.getString(R.string.event_vault_generator_overload_choice_a_desc) ?: "Flood the mechanical bay with suppression agents regardless of equipment damage.",
                knownEffect = context?.getString(R.string.event_vault_generator_overload_choice_a_effect) ?: "Stops fire quickly. Damages equipment in the bay.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("powerGrid" to -20, "constructionGear" to -10),
                    narrativeText = context?.getString(R.string.event_vault_generator_overload_choice_a_outcome) ?: "The fire dies in a cloud of white. The mechanical bay is a ruin of foam and scorched metal. The generator is gone, but the vault stands."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_vault_generator_overload_choice_b_label) ?: "Manual firefighting team",
                description = context?.getString(R.string.event_vault_generator_overload_choice_b_desc) ?: "Send a crew in with extinguishers to fight the fire directly.",
                knownEffect = context?.getString(R.string.event_vault_generator_overload_choice_b_effect) ?: "Preserves more equipment. Crew faces serious danger.",
                hiddenRisk = 0.45f,
                outcome = EventOutcome(
                    survivorDelta = -2,
                    systemDeltas = mapOf("powerGrid" to -10),
                    narrativeText = context?.getString(R.string.event_vault_generator_overload_choice_b_outcome) ?: "Two crew members are overcome by smoke. The fire is contained. The generator is salvageable. The cost is written in the medical bay's records."
                )
            ),
            choiceC = EventChoice(
                label = context?.getString(R.string.event_vault_generator_overload_choice_c_label) ?: "Evacuate and let it burn out",
                description = context?.getString(R.string.event_vault_generator_overload_choice_c_desc) ?: "Clear the eastern corridors and seal the section — let the fire consume itself.",
                knownEffect = context?.getString(R.string.event_vault_generator_overload_choice_c_effect) ?: "No casualties. Significant structural and equipment loss.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("powerGrid" to -35, "structureScanner" to -15, "constructionGear" to -20),
                    narrativeText = context?.getString(R.string.event_vault_generator_overload_choice_c_outcome) ?: "The fire burns for six hours. When it's done, the eastern wing is a blackened shell. You rebuild. You always rebuild."
                )
            )
        )
    )

    // ── SURFACE / EXTERNAL EVENTS ─────────────────────────────────────────────

    private val surfaceEvents = listOf(

        GameEvent(
            id = "surface_knock_on_door",
            title = context?.getString(R.string.event_surface_knock_on_door_title) ?: "Three Knocks",
            description = context?.getString(R.string.event_surface_knock_on_door_description) ?: "The external sensors register a deliberate knock on the vault door — three slow, measured impacts. Not the frantic pounding of the desperate. Someone out there knows the old signal. The intercom crackles with shallow breathing.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_surface_knock_on_door_choice_a_label) ?: "Open the intercom and respond",
                description = context?.getString(R.string.event_surface_knock_on_door_choice_a_desc) ?: "Establish voice contact before making any decision about the door.",
                knownEffect = context?.getString(R.string.event_surface_knock_on_door_choice_a_effect) ?: "Gathers information. Reveals your presence if unknown.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("threatAssessment" to 5),
                    narrativeText = context?.getString(R.string.event_surface_knock_on_door_choice_a_outcome) ?: "A woman's voice. Calm, educated, exhausted. She claims to be from Vault 9 — the one that was supposed to open first. She has information about the surface. You listen."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_surface_knock_on_door_choice_b_label) ?: "Open the door",
                description = context?.getString(R.string.event_surface_knock_on_door_choice_b_desc) ?: "Extend trust and allow entry — survivors deserve a chance.",
                knownEffect = context?.getString(R.string.event_surface_knock_on_door_choice_b_effect) ?: "Gains survivors and potential knowledge. Unknown risk.",
                hiddenRisk = 0.4f,
                outcome = EventOutcome(
                    survivorDelta = 3,
                    systemDeltas = mapOf("foodStores" to -8, "threatAssessment" to -10),
                    narrativeText = context?.getString(R.string.event_surface_knock_on_door_choice_b_outcome) ?: "Three people stumble in — gaunt, irradiated, grateful. They bring stories of the surface that keep you awake for nights. And one of them is sick in a way your scanner doesn't recognize."
                )
            ),
            choiceC = EventChoice(
                label = context?.getString(R.string.event_surface_knock_on_door_choice_c_label) ?: "Ignore it",
                description = context?.getString(R.string.event_surface_knock_on_door_choice_c_desc) ?: "Maintain silence and hope they move on.",
                knownEffect = context?.getString(R.string.event_surface_knock_on_door_choice_c_effect) ?: "No risk. No gain. They may return.",
                hiddenRisk = 0.3f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("threatAssessment" to -5),
                    narrativeText = context?.getString(R.string.event_surface_knock_on_door_choice_c_outcome) ?: "The knocking stops after an hour. The sensors show a figure sitting against the door for three days before the heat signature disappears. You don't speak of it."
                )
            )
        ),

        GameEvent(
            id = "surface_radio_signal",
            title = context?.getString(R.string.event_surface_radio_signal_title) ?: "Signal in the Static",
            description = context?.getString(R.string.event_surface_radio_signal_description) ?: "Your long-range antenna has picked up a repeating radio transmission — structured, deliberate, not automated. Someone is broadcasting on a pre-war emergency frequency. The signal is coming from forty kilometers northeast.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_surface_radio_signal_choice_a_label) ?: "Respond to the signal",
                description = context?.getString(R.string.event_surface_radio_signal_choice_a_desc) ?: "Broadcast a reply and attempt to establish two-way communication.",
                knownEffect = context?.getString(R.string.event_surface_radio_signal_choice_a_effect) ?: "May make contact. Reveals your location.",
                hiddenRisk = 0.3f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("threatAssessment" to 10),
                    databaseDeltas = mapOf("scientificArchive" to 5),
                    narrativeText = context?.getString(R.string.event_surface_radio_signal_choice_a_outcome) ?: "A settlement. Forty survivors in a fortified school. They have seeds. You have medicine. A trade is arranged. The world gets a little less lonely."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_surface_radio_signal_choice_b_label) ?: "Send a probe to investigate",
                description = context?.getString(R.string.event_surface_radio_signal_choice_b_desc) ?: "Dispatch a surface drone to locate and observe the signal source.",
                knownEffect = context?.getString(R.string.event_surface_radio_signal_choice_b_effect) ?: "Gathers intelligence without revealing position. Costs a probe.",
                outcome = EventOutcome(
                    probesDelta = -1,
                    systemDeltas = mapOf("threatAssessment" to 20),
                    narrativeText = context?.getString(R.string.event_surface_radio_signal_choice_b_outcome) ?: "The probe returns with footage of a fortified compound — armed, organized, and flying a flag you don't recognize. Knowledge without commitment. For now."
                )
            ),
            choiceC = EventChoice(
                label = context?.getString(R.string.event_surface_radio_signal_choice_c_label) ?: "Monitor silently",
                description = context?.getString(R.string.event_surface_radio_signal_choice_c_desc) ?: "Record the transmission and analyze without responding.",
                knownEffect = context?.getString(R.string.event_surface_radio_signal_choice_c_effect) ?: "Safe. Limited information gain.",
                outcome = EventOutcome(
                    databaseDeltas = mapOf("scientificArchive" to 3),
                    narrativeText = context?.getString(R.string.event_surface_radio_signal_choice_c_outcome) ?: "The signal repeats for eleven days, then stops. You have a recording of a voice reading coordinates and a phrase you can't translate. It haunts you."
                )
            )
        ),

        GameEvent(
            id = "surface_acid_rain",
            title = context?.getString(R.string.event_surface_acid_rain_title) ?: "The Sky Bleeds",
            description = context?.getString(R.string.event_surface_acid_rain_description) ?: "Atmospheric sensors detect a severe acid precipitation event moving toward your surface installations. Your external equipment — antennas, solar panels, probe bays — will be stripped bare if left unprotected.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_surface_acid_rain_choice_a_label) ?: "Retract all surface equipment",
                description = context?.getString(R.string.event_surface_acid_rain_choice_a_desc) ?: "Pull everything inside before the storm hits.",
                knownEffect = context?.getString(R.string.event_surface_acid_rain_choice_a_effect) ?: "Protects equipment. Vault goes dark on surface sensors.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("powerGrid" to -10),
                    narrativeText = context?.getString(R.string.event_surface_acid_rain_choice_a_outcome) ?: "Everything comes in. The storm lasts three days and sounds like the world ending. When it passes, the surface is scoured clean. Your equipment is intact."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_surface_acid_rain_choice_b_label) ?: "Apply emergency sealant coatings",
                description = context?.getString(R.string.event_surface_acid_rain_choice_b_desc) ?: "Deploy protective coatings on critical external systems.",
                knownEffect = context?.getString(R.string.event_surface_acid_rain_choice_b_effect) ?: "Partial protection. Some equipment loss likely.",
                hiddenRisk = 0.35f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("resourceScanner" to -10, "atmosphereScrubbers" to -5),
                    narrativeText = context?.getString(R.string.event_surface_acid_rain_choice_b_outcome) ?: "The sealant holds on most systems. The resource scanner takes a hit — its external array is pitted and degraded. You'll need to recalibrate."
                )
            ),
            choiceC = EventChoice(
                label = context?.getString(R.string.event_surface_acid_rain_choice_c_label) ?: "Leave equipment exposed and document the storm",
                description = context?.getString(R.string.event_surface_acid_rain_choice_c_desc) ?: "Sacrifice some hardware to gather atmospheric data on the event.",
                knownEffect = context?.getString(R.string.event_surface_acid_rain_choice_c_effect) ?: "Significant equipment loss. Valuable scientific data gained.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("resourceScanner" to -20, "atmosphereScrubbers" to -10),
                    databaseDeltas = mapOf("scientificArchive" to 15),
                    narrativeText = context?.getString(R.string.event_surface_acid_rain_choice_c_outcome) ?: "The data is extraordinary — acid composition, precipitation patterns, storm duration. The equipment is ruined. Science is expensive."
                )
            )
        ),

        GameEvent(
            id = "surface_scavenger_band",
            title = context?.getString(R.string.event_surface_scavenger_band_title) ?: "Uninvited Guests",
            description = context?.getString(R.string.event_surface_scavenger_band_description) ?: "Surface cameras show a band of twelve armed scavengers camped two hundred meters from the vault entrance. They've found the old access road. They're not leaving — they're digging.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_surface_scavenger_band_choice_a_label) ?: "Activate external deterrents",
                description = context?.getString(R.string.event_surface_scavenger_band_choice_a_desc) ?: "Trigger the vault's external alarm systems and warning lights.",
                knownEffect = context?.getString(R.string.event_surface_scavenger_band_choice_a_effect) ?: "May drive them off. Confirms vault location.",
                hiddenRisk = 0.3f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("securitySystem" to 5, "powerGrid" to -5),
                    narrativeText = context?.getString(R.string.event_surface_scavenger_band_choice_a_outcome) ?: "The alarms send half of them running. Four remain, more determined than before. They know something is here. They'll be back with more."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_surface_scavenger_band_choice_b_label) ?: "Send an armed delegation to negotiate",
                description = context?.getString(R.string.event_surface_scavenger_band_choice_b_desc) ?: "Meet them on the surface — offer trade to redirect their interest.",
                knownEffect = context?.getString(R.string.event_surface_scavenger_band_choice_b_effect) ?: "May resolve peacefully. Delegation faces real danger.",
                hiddenRisk = 0.4f,
                outcome = EventOutcome(
                    survivorDelta = -1,
                    systemDeltas = mapOf("foodStores" to -10, "threatAssessment" to 10),
                    narrativeText = context?.getString(R.string.event_surface_scavenger_band_choice_b_outcome) ?: "The negotiation goes badly at first, then worse. One of yours doesn't come back. But the scavengers take the offered food and move on. For now."
                )
            ),
            choiceC = EventChoice(
                label = context?.getString(R.string.event_surface_scavenger_band_choice_c_label) ?: "Wait and observe",
                description = context?.getString(R.string.event_surface_scavenger_band_choice_c_desc) ?: "Monitor their activity and act only if they breach the outer door.",
                knownEffect = context?.getString(R.string.event_surface_scavenger_band_choice_c_effect) ?: "No immediate cost. They may find the entrance.",
                hiddenRisk = 0.45f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("securitySystem" to -15),
                    narrativeText = context?.getString(R.string.event_surface_scavenger_band_choice_c_outcome) ?: "They find the secondary access hatch on day three. The breach alarm sounds at 0300. The security response is brutal and necessary."
                )
            )
        ),

        GameEvent(
            id = "surface_radiation_spike",
            title = context?.getString(R.string.event_surface_radiation_spike_title) ?: "The Geiger Chorus",
            description = context?.getString(R.string.event_surface_radiation_spike_description) ?: "External radiation monitors are screaming. A massive spike — three times baseline — is sweeping across the surface from the direction of the old city. Your surface teams have forty minutes to get underground.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_surface_radiation_spike_choice_a_label) ?: "Emergency recall of all surface teams",
                description = context?.getString(R.string.event_surface_radiation_spike_choice_a_desc) ?: "Broadcast immediate recall and open the vault door for rapid entry.",
                knownEffect = context?.getString(R.string.event_surface_radiation_spike_choice_a_effect) ?: "Saves surface teams. Door opening creates brief exposure risk.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("radiationScanner" to 10, "medicalBay" to -5),
                    narrativeText = context?.getString(R.string.event_surface_radiation_spike_choice_a_outcome) ?: "All teams make it back. Three show elevated radiation levels — manageable with treatment. The door seals just as the wave hits. Close."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_surface_radiation_spike_choice_b_label) ?: "Shelter teams in surface bunkers",
                description = context?.getString(R.string.event_surface_radiation_spike_choice_b_desc) ?: "Direct teams to pre-positioned surface shelters rather than risking the vault door.",
                knownEffect = context?.getString(R.string.event_surface_radiation_spike_choice_b_effect) ?: "Protects teams without opening vault. Bunker integrity unknown.",
                hiddenRisk = 0.4f,
                outcome = EventOutcome(
                    survivorDelta = -2,
                    systemDeltas = mapOf("radiationScanner" to 5),
                    narrativeText = context?.getString(R.string.event_surface_radiation_spike_choice_b_outcome) ?: "Two bunkers hold. One doesn't. The team inside received a lethal dose. They make it back to the vault to say goodbye. The medical bay does what it can."
                )
            )
        ),

        GameEvent(
            id = "surface_abandoned_vehicle",
            title = context?.getString(R.string.event_surface_abandoned_vehicle_title) ?: "The Truck That Drove Itself",
            description = context?.getString(R.string.event_surface_abandoned_vehicle_description) ?: "A pre-war military transport vehicle has rolled to a stop outside the vault entrance — engine still running, no driver visible. The cargo bay is sealed with a biohazard lock. The vehicle's markings are from a facility that was supposed to have been destroyed.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_surface_abandoned_vehicle_choice_a_label) ?: "Send a hazmat team to investigate",
                description = context?.getString(R.string.event_surface_abandoned_vehicle_choice_a_desc) ?: "Approach with full protective gear and attempt to open the cargo bay.",
                knownEffect = context?.getString(R.string.event_surface_abandoned_vehicle_choice_a_effect) ?: "May yield valuable supplies. Unknown biohazard risk.",
                hiddenRisk = 0.45f,
                outcome = EventOutcome(
                    survivorDelta = -1,
                    systemDeltas = mapOf("medicalBay" to -10),
                    databaseDeltas = mapOf("scientificArchive" to 15),
                    narrativeText = context?.getString(R.string.event_surface_abandoned_vehicle_choice_a_outcome) ?: "The cargo bay contains pre-war medical research — and a pathogen sample that breaches containment. One team member is lost. The research is invaluable. The cost is not forgotten."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_surface_abandoned_vehicle_choice_b_label) ?: "Destroy the vehicle remotely",
                description = context?.getString(R.string.event_surface_abandoned_vehicle_choice_b_desc) ?: "Use the vault's external systems to detonate the vehicle at a safe distance.",
                knownEffect = context?.getString(R.string.event_surface_abandoned_vehicle_choice_b_effect) ?: "Eliminates unknown risk. Destroys potential resources.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("threatAssessment" to 10),
                    narrativeText = context?.getString(R.string.event_surface_abandoned_vehicle_choice_b_outcome) ?: "The explosion is visible on every surface camera. Whatever was in that truck is ash now. You sleep better. You wonder what you burned."
                )
            ),
            choiceC = EventChoice(
                label = context?.getString(R.string.event_surface_abandoned_vehicle_choice_c_label) ?: "Leave it and monitor",
                description = context?.getString(R.string.event_surface_abandoned_vehicle_choice_c_desc) ?: "Keep the vehicle under observation without approaching.",
                knownEffect = context?.getString(R.string.event_surface_abandoned_vehicle_choice_c_effect) ?: "No immediate risk. Situation may change.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("threatAssessment" to -5),
                    narrativeText = context?.getString(R.string.event_surface_abandoned_vehicle_choice_c_outcome) ?: "The vehicle sits there for a week. Then someone opens it from the inside. The figure that emerges is not quite right. The security response is immediate."
                )
            )
        ),

        GameEvent(
            id = "surface_crop_test_failure",
            title = context?.getString(R.string.event_surface_crop_test_failure_title) ?: "Dead on Arrival",
            description = context?.getString(R.string.event_surface_crop_test_failure_description) ?: "Your surface agricultural test plot — three months of careful cultivation — has been destroyed overnight. The soil samples show a new fungal pathogen that wasn't in any pre-war database. The dream of surface farming just got further away.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_surface_crop_test_failure_choice_a_label) ?: "Analyze the pathogen extensively",
                description = context?.getString(R.string.event_surface_crop_test_failure_choice_a_desc) ?: "Dedicate scientific resources to understanding and cataloguing the new fungus.",
                knownEffect = context?.getString(R.string.event_surface_crop_test_failure_choice_a_effect) ?: "Builds knowledge. No immediate food benefit.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("agriculturalScanner" to -10),
                    databaseDeltas = mapOf("scientificArchive" to 20),
                    narrativeText = context?.getString(R.string.event_surface_crop_test_failure_choice_a_outcome) ?: "The pathogen is unlike anything in the archive. It's adaptive, aggressive, and possibly engineered. The knowledge is cold comfort for empty stomachs."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_surface_crop_test_failure_choice_b_label) ?: "Try a different location immediately",
                description = context?.getString(R.string.event_surface_crop_test_failure_choice_b_desc) ?: "Relocate the test plot and attempt a second cultivation cycle.",
                knownEffect = context?.getString(R.string.event_surface_crop_test_failure_choice_b_effect) ?: "Maintains agricultural progress. May fail again.",
                hiddenRisk = 0.35f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("agriculturalScanner" to 10, "foodStores" to -5),
                    narrativeText = context?.getString(R.string.event_surface_crop_test_failure_choice_b_outcome) ?: "The second plot takes hold in cleaner soil two kilometers east. It's fragile and small, but it's alive. The surface might feed you yet."
                )
            )
        ),

        GameEvent(
            id = "surface_survivor_child",
            title = context?.getString(R.string.event_surface_survivor_child_title) ?: "Small Footprints",
            description = context?.getString(R.string.event_surface_survivor_child_description) ?: "Surface sensors detect a single small heat signature moving erratically near the vault entrance — a child, alone, in the open. Radiation levels outside are elevated. They've been out there for at least six hours.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_surface_survivor_child_choice_a_label) ?: "Open the door and bring them in",
                description = context?.getString(R.string.event_surface_survivor_child_choice_a_desc) ?: "Send a team out immediately to retrieve the child.",
                knownEffect = context?.getString(R.string.event_surface_survivor_child_choice_a_effect) ?: "Saves the child. Team faces radiation exposure.",
                outcome = EventOutcome(
                    survivorDelta = 1,
                    systemDeltas = mapOf("medicalBay" to -10, "foodStores" to -3),
                    narrativeText = context?.getString(R.string.event_surface_survivor_child_choice_a_outcome) ?: "A girl, maybe seven. She doesn't speak for three days. When she does, the things she describes about the surface make your scouts go quiet. She stays."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_surface_survivor_child_choice_b_label) ?: "Guide them via external speaker",
                description = context?.getString(R.string.event_surface_survivor_child_choice_b_desc) ?: "Use the external PA to direct the child to a surface shelter without opening the vault.",
                knownEffect = context?.getString(R.string.event_surface_survivor_child_choice_b_effect) ?: "Lower risk to vault. Child may not survive alone.",
                hiddenRisk = 0.4f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("threatAssessment" to 5),
                    narrativeText = context?.getString(R.string.event_surface_survivor_child_choice_b_outcome) ?: "The child follows your voice to the shelter. You leave supplies in the drop box. The heat signature disappears the next morning. You tell yourself it means they moved on."
                )
            )
        ),

        GameEvent(
            id = "surface_military_drone",
            title = context?.getString(R.string.event_surface_military_drone_title) ?: "Eyes in the Sky",
            description = context?.getString(R.string.event_surface_military_drone_description) ?: "A pre-war military surveillance drone is circling your surface coordinates in a systematic pattern. It's broadcasting on a frequency that suggests it's still reporting to someone — or something — that is still operational.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_surface_military_drone_choice_a_label) ?: "Attempt to hack and disable the drone",
                description = context?.getString(R.string.event_surface_military_drone_choice_a_desc) ?: "Use your technical systems to interfere with the drone's control signal.",
                knownEffect = context?.getString(R.string.event_surface_military_drone_choice_a_effect) ?: "May disable surveillance. Could alert the controller.",
                hiddenRisk = 0.35f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("securitySystem" to 15, "powerGrid" to -10),
                    narrativeText = context?.getString(R.string.event_surface_military_drone_choice_a_outcome) ?: "The drone spirals down two kilometers away. Whatever was receiving its feed now has a gap in coverage — and knows something interfered. You've made yourself known."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_surface_military_drone_choice_b_label) ?: "Shut down all surface emissions",
                description = context?.getString(R.string.event_surface_military_drone_choice_b_desc) ?: "Go dark — no heat signatures, no radio, no movement on the surface.",
                knownEffect = context?.getString(R.string.event_surface_military_drone_choice_b_effect) ?: "May avoid detection. Disrupts surface operations.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("powerGrid" to -15, "resourceScanner" to -10),
                    narrativeText = context?.getString(R.string.event_surface_military_drone_choice_b_outcome) ?: "The drone completes its pattern and moves on. You were invisible. For now, that's enough. The question of who sent it keeps you awake."
                )
            ),
            choiceC = EventChoice(
                label = context?.getString(R.string.event_surface_military_drone_choice_c_label) ?: "Broadcast a greeting on its frequency",
                description = context?.getString(R.string.event_surface_military_drone_choice_c_desc) ?: "Make contact — whoever controls that drone might be worth knowing.",
                knownEffect = context?.getString(R.string.event_surface_military_drone_choice_c_effect) ?: "Establishes contact. Completely unknown consequences.",
                hiddenRisk = 0.5f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("threatAssessment" to -20),
                    databaseDeltas = mapOf("scientificArchive" to 10),
                    narrativeText = context?.getString(R.string.event_surface_military_drone_choice_c_outcome) ?: "A response comes — automated at first, then a human voice. They call themselves the Continuity. They know about your vault. They've known for years. They've been waiting for you to be ready."
                )
            )
        ),

        GameEvent(
            id = "surface_toxic_bloom",
            title = context?.getString(R.string.event_surface_toxic_bloom_title) ?: "The Green Death",
            description = context?.getString(R.string.event_surface_toxic_bloom_description) ?: "A massive toxic algae bloom has contaminated the surface water table in your region. Your water scanner shows the contamination reaching your secondary intake pipes. The primary filtration system was not designed for this compound.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_surface_toxic_bloom_choice_a_label) ?: "Upgrade filtration with available materials",
                description = context?.getString(R.string.event_surface_toxic_bloom_choice_a_desc) ?: "Improvise an enhanced filtration stage using vault construction resources.",
                knownEffect = context?.getString(R.string.event_surface_toxic_bloom_choice_a_effect) ?: "Protects water supply. Costs construction resources.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("waterScanner" to 15, "constructionGear" to -15),
                    narrativeText = context?.getString(R.string.event_surface_toxic_bloom_choice_a_outcome) ?: "The improvised filter holds. The water tastes strange for weeks but the toxin levels stay below dangerous thresholds. Engineering saves the day, as it usually does."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_surface_toxic_bloom_choice_b_label) ?: "Switch entirely to recycled internal water",
                description = context?.getString(R.string.event_surface_toxic_bloom_choice_b_desc) ?: "Seal external intakes and rely solely on internal water recycling.",
                knownEffect = context?.getString(R.string.event_surface_toxic_bloom_choice_b_effect) ?: "Safe from contamination. Recycling system under heavy strain.",
                hiddenRisk = 0.3f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("waterScanner" to -10, "atmosphereScrubbers" to -5),
                    narrativeText = context?.getString(R.string.event_surface_toxic_bloom_choice_b_outcome) ?: "The recycling system groans under the load. It holds — barely. The bloom passes in six weeks. The system needs maintenance it can't afford."
                )
            )
        ),

        GameEvent(
            id = "surface_pre_war_cache",
            title = context?.getString(R.string.event_surface_pre_war_cache_title) ?: "What the Earth Kept",
            description = context?.getString(R.string.event_surface_pre_war_cache_description) ?: "A surface probe has located a pre-war government emergency cache buried beneath a collapsed overpass. The manifest — partially readable — lists medical supplies, seeds, and encrypted data drives. It's two kilometers from the vault entrance.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_surface_pre_war_cache_choice_a_label) ?: "Full retrieval mission",
                description = context?.getString(R.string.event_surface_pre_war_cache_choice_a_desc) ?: "Send a large team to excavate and recover everything in the cache.",
                knownEffect = context?.getString(R.string.event_surface_pre_war_cache_choice_a_effect) ?: "Maximum recovery. Large team exposure to surface risks.",
                hiddenRisk = 0.35f,
                outcome = EventOutcome(
                    survivorDelta = -1,
                    systemDeltas = mapOf("foodStores" to 20, "medicalBay" to 20),
                    databaseDeltas = mapOf("scientificArchive" to 15),
                    narrativeText = context?.getString(R.string.event_surface_pre_war_cache_choice_a_outcome) ?: "The cache is everything the manifest promised and more. One team member is lost to a structural collapse during excavation. The vault is richer and heavier for it."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_surface_pre_war_cache_choice_b_label) ?: "Small stealth team, priority items only",
                description = context?.getString(R.string.event_surface_pre_war_cache_choice_b_desc) ?: "Send two people to grab the highest-value items and return quickly.",
                knownEffect = context?.getString(R.string.event_surface_pre_war_cache_choice_b_effect) ?: "Lower risk. Partial recovery only.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("medicalBay" to 10),
                    databaseDeltas = mapOf("scientificArchive" to 8),
                    narrativeText = context?.getString(R.string.event_surface_pre_war_cache_choice_b_outcome) ?: "The team returns in four hours with medical supplies and two data drives. The seeds are left behind — too heavy, too exposed. You'll go back for them. Maybe."
                )
            ),
            choiceC = EventChoice(
                label = context?.getString(R.string.event_surface_pre_war_cache_choice_c_label) ?: "Send a probe to map it first",
                description = context?.getString(R.string.event_surface_pre_war_cache_choice_c_desc) ?: "Fully document the cache before committing to a retrieval.",
                knownEffect = context?.getString(R.string.event_surface_pre_war_cache_choice_c_effect) ?: "No risk now. Delays recovery. Cache may be found by others.",
                hiddenRisk = 0.3f,
                outcome = EventOutcome(
                    probesDelta = -1,
                    systemDeltas = mapOf("resourceScanner" to 10),
                    narrativeText = context?.getString(R.string.event_surface_pre_war_cache_choice_c_outcome) ?: "The probe maps everything perfectly. When the retrieval team arrives three days later, the cache has been partially looted. Someone else found it first."
                )
            )
        ),

        GameEvent(
            id = "surface_earthquake",
            title = context?.getString(R.string.event_surface_earthquake_title) ?: "The Ground Remembers",
            description = context?.getString(R.string.event_surface_earthquake_description) ?: "A 6.2 magnitude earthquake strikes without warning. The vault shudders and groans. Dust falls from the ceiling in sheets. Surface installations are thrown into chaos and several internal systems report damage.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_surface_earthquake_choice_a_label) ?: "Emergency structural assessment",
                description = context?.getString(R.string.event_surface_earthquake_choice_a_desc) ?: "Immediately deploy all available engineers to assess vault integrity.",
                knownEffect = context?.getString(R.string.event_surface_earthquake_choice_a_effect) ?: "Identifies damage quickly. Engineers at risk from aftershocks.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("structureScanner" to 20, "constructionGear" to -10),
                    narrativeText = context?.getString(R.string.event_surface_earthquake_choice_a_outcome) ?: "Three critical stress fractures are found and shored up within hours. The vault holds. The aftershocks test it twice more. It holds again."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_surface_earthquake_choice_b_label) ?: "Shelter in place and wait for aftershocks to pass",
                description = context?.getString(R.string.event_surface_earthquake_choice_b_desc) ?: "Keep everyone in reinforced sections until seismic activity stabilizes.",
                knownEffect = context?.getString(R.string.event_surface_earthquake_choice_b_effect) ?: "Protects people. Damage goes unaddressed longer.",
                hiddenRisk = 0.3f,
                outcome = EventOutcome(
                    survivorDelta = -1,
                    systemDeltas = mapOf("structureScanner" to -10, "powerGrid" to -10),
                    narrativeText = context?.getString(R.string.event_surface_earthquake_choice_b_outcome) ?: "An aftershock collapses a section of the eastern corridor before you can assess it. One person is caught in the wrong place. The vault is stable but scarred."
                )
            )
        ),

        GameEvent(
            id = "surface_hostile_settlement",
            title = context?.getString(R.string.event_surface_hostile_settlement_title) ?: "Neighbors With Guns",
            description = context?.getString(R.string.event_surface_hostile_settlement_description) ?: "A new settlement has established itself eight kilometers from your vault — and they've sent an armed envoy demanding a 'protection tithe' of food and medicine. They know you're here. They know you have supplies.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_surface_hostile_settlement_choice_a_label) ?: "Pay the tithe",
                description = context?.getString(R.string.event_surface_hostile_settlement_choice_a_desc) ?: "Comply with their demands to avoid conflict.",
                knownEffect = context?.getString(R.string.event_surface_hostile_settlement_choice_a_effect) ?: "Buys peace. Sets a precedent. Demands may grow.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("foodStores" to -15, "medicalBay" to -10),
                    narrativeText = context?.getString(R.string.event_surface_hostile_settlement_choice_a_outcome) ?: "The envoy leaves satisfied. Three months later, they're back with larger demands. You've bought time, not safety."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_surface_hostile_settlement_choice_b_label) ?: "Refuse and demonstrate vault defenses",
                description = context?.getString(R.string.event_surface_hostile_settlement_choice_b_desc) ?: "Turn the envoy away and activate visible external deterrents.",
                knownEffect = context?.getString(R.string.event_surface_hostile_settlement_choice_b_effect) ?: "Maintains independence. May provoke escalation.",
                hiddenRisk = 0.4f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("securitySystem" to 15, "threatAssessment" to -15),
                    narrativeText = context?.getString(R.string.event_surface_hostile_settlement_choice_b_outcome) ?: "The envoy leaves angry. Two weeks later, a probe detects them scouting your perimeter. The deterrents hold them back — for now. The tension is a living thing."
                )
            ),
            choiceC = EventChoice(
                label = context?.getString(R.string.event_surface_hostile_settlement_choice_c_label) ?: "Propose a mutual aid agreement",
                description = context?.getString(R.string.event_surface_hostile_settlement_choice_c_desc) ?: "Counter-offer with a formal trade arrangement rather than tribute.",
                knownEffect = context?.getString(R.string.event_surface_hostile_settlement_choice_c_effect) ?: "Potential long-term alliance. Requires ongoing resource commitment.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("foodStores" to -8, "threatAssessment" to 20),
                    narrativeText = context?.getString(R.string.event_surface_hostile_settlement_choice_c_outcome) ?: "They're surprised by the counter-offer. After two days of negotiation, an agreement is reached. It's fragile and transactional — but it's not war."
                )
            )
        ),

        GameEvent(
            id = "surface_solar_flare",
            title = context?.getString(R.string.event_surface_solar_flare_title) ?: "The Sun Remembers It's Angry",
            description = context?.getString(R.string.event_surface_solar_flare_description) ?: "A massive solar flare is incoming — your instruments give you six hours of warning. The electromagnetic pulse will fry unshielded electronics on the surface and potentially penetrate the vault's upper levels.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_surface_solar_flare_choice_a_label) ?: "Full electronic shutdown and shielding",
                description = context?.getString(R.string.event_surface_solar_flare_choice_a_desc) ?: "Power down all non-essential systems and activate Faraday shielding.",
                knownEffect = context?.getString(R.string.event_surface_solar_flare_choice_a_effect) ?: "Maximum protection. Vault goes dark for 12+ hours.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("powerGrid" to -10),
                    narrativeText = context?.getString(R.string.event_surface_solar_flare_choice_a_outcome) ?: "Twelve hours of darkness and silence. The flare passes. Systems come back online one by one. Everything works. The preparation was worth every anxious hour."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_surface_solar_flare_choice_b_label) ?: "Protect critical systems only",
                description = context?.getString(R.string.event_surface_solar_flare_choice_b_desc) ?: "Shield life support and power grid, leave secondary systems exposed.",
                knownEffect = context?.getString(R.string.event_surface_solar_flare_choice_b_effect) ?: "Faster recovery. Some systems will be damaged.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("resourceScanner" to -20, "radiationScanner" to -15),
                    narrativeText = context?.getString(R.string.event_surface_solar_flare_choice_b_outcome) ?: "Life support survives intact. The resource and radiation scanners are fried — their surface arrays burned out by the pulse. You're flying partially blind now."
                )
            )
        ),

        GameEvent(
            id = "surface_animal_migration",
            title = context?.getString(R.string.event_surface_animal_migration_title) ?: "The Herd",
            description = context?.getString(R.string.event_surface_animal_migration_description) ?: "Surface cameras capture something extraordinary — a massive herd of mutated elk moving through the wasteland, thousands strong. They're large, strange, and apparently healthy. They're also heading directly toward your surface installations.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_surface_animal_migration_choice_a_label) ?: "Attempt to harvest several animals",
                description = context?.getString(R.string.event_surface_animal_migration_choice_a_desc) ?: "Send a hunting team to the surface to take advantage of this rare opportunity.",
                knownEffect = context?.getString(R.string.event_surface_animal_migration_choice_a_effect) ?: "Significant food gain. Team faces unknown mutated animals.",
                hiddenRisk = 0.35f,
                outcome = EventOutcome(
                    survivorDelta = -1,
                    systemDeltas = mapOf("foodStores" to 35),
                    databaseDeltas = mapOf("scientificArchive" to 10),
                    narrativeText = context?.getString(R.string.event_surface_animal_migration_choice_a_outcome) ?: "The hunt is successful beyond expectation. One hunter is gored by a bull with bone-plated shoulders. The meat feeds the vault for weeks. The biologist is ecstatic."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_surface_animal_migration_choice_b_label) ?: "Retract surface equipment and observe",
                description = context?.getString(R.string.event_surface_animal_migration_choice_b_desc) ?: "Pull everything inside and document the herd's passage.",
                knownEffect = context?.getString(R.string.event_surface_animal_migration_choice_b_effect) ?: "No risk. Valuable ecological data. Missed food opportunity.",
                outcome = EventOutcome(
                    databaseDeltas = mapOf("scientificArchive" to 15),
                    narrativeText = context?.getString(R.string.event_surface_animal_migration_choice_b_outcome) ?: "The herd passes in six hours, leaving the surface churned and strange. The footage is remarkable. The vault goes to bed hungry, but wiser."
                )
            ),
            choiceC = EventChoice(
                label = context?.getString(R.string.event_surface_animal_migration_choice_c_label) ?: "Attempt to capture and domesticate juveniles",
                description = context?.getString(R.string.event_surface_animal_migration_choice_c_desc) ?: "Try to bring young animals into the vault for a long-term food source.",
                knownEffect = context?.getString(R.string.event_surface_animal_migration_choice_c_effect) ?: "Potential sustainable food source. Highly uncertain outcome.",
                hiddenRisk = 0.5f,
                outcome = EventOutcome(
                    survivorDelta = -2,
                    systemDeltas = mapOf("foodStores" to -5, "agriculturalScanner" to 15),
                    narrativeText = context?.getString(R.string.event_surface_animal_migration_choice_c_outcome) ?: "The juveniles are captured but they're not docile. Two handlers are badly injured. The animals are eventually contained — strange, luminescent, and deeply unhappy. They survive. So do you."
                )
            )
        )
    )

    // ── COSMIC / WEIRD EVENTS ─────────────────────────────────────────────────

    private val cosmicEvents = listOf(

        GameEvent(
            id = "cosmic_ai_broadcast",
            title = context?.getString(R.string.event_cosmic_ai_broadcast_title) ?: "The Voice That Knows Your Name",
            description = context?.getString(R.string.event_cosmic_ai_broadcast_description) ?: "Every speaker in the vault simultaneously broadcasts a single message in a calm, synthetic voice: your name, your vault designation, and a set of coordinates you don't recognize. Then silence. Your systems show no record of the transmission originating internally.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_cosmic_ai_broadcast_choice_a_label) ?: "Investigate the coordinates",
                description = context?.getString(R.string.event_cosmic_ai_broadcast_choice_a_desc) ?: "Send a probe to the location referenced in the broadcast.",
                knownEffect = context?.getString(R.string.event_cosmic_ai_broadcast_choice_a_effect) ?: "May reveal the source. Unknown what you'll find.",
                hiddenRisk = 0.4f,
                outcome = EventOutcome(
                    probesDelta = -1,
                    systemDeltas = mapOf("threatAssessment" to 15),
                    databaseDeltas = mapOf("scientificArchive" to 20),
                    narrativeText = context?.getString(R.string.event_cosmic_ai_broadcast_choice_a_outcome) ?: "The probe finds a buried server complex, still running on geothermal power. The AI inside has been monitoring every vault on the continent. It has been waiting for someone to come. It has things to tell you."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_cosmic_ai_broadcast_choice_b_label) ?: "Purge and audit all vault systems",
                description = context?.getString(R.string.event_cosmic_ai_broadcast_choice_b_desc) ?: "Assume a breach and conduct a full security sweep of every system.",
                knownEffect = context?.getString(R.string.event_cosmic_ai_broadcast_choice_b_effect) ?: "Improves security. May destroy something important.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("securitySystem" to 20, "powerGrid" to -15),
                    narrativeText = context?.getString(R.string.event_cosmic_ai_broadcast_choice_b_outcome) ?: "The sweep finds nothing — no intrusion, no foreign code. Whatever spoke through your speakers left no trace. The vault is clean. The mystery is not."
                )
            ),
            choiceC = EventChoice(
                label = context?.getString(R.string.event_cosmic_ai_broadcast_choice_c_label) ?: "Broadcast a response on all frequencies",
                description = context?.getString(R.string.event_cosmic_ai_broadcast_choice_c_desc) ?: "Reply to whatever sent the message — speak into the unknown.",
                knownEffect = context?.getString(R.string.event_cosmic_ai_broadcast_choice_c_effect) ?: "May establish contact. Completely unpredictable response.",
                hiddenRisk = 0.45f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("threatAssessment" to -10),
                    databaseDeltas = mapOf("scientificArchive" to 10),
                    narrativeText = context?.getString(R.string.event_cosmic_ai_broadcast_choice_c_outcome) ?: "The response comes three hours later — a data packet containing the complete pre-war history of your vault's construction, including sections that were classified above your clearance level. Someone has been watching since the beginning."
                )
            )
        ),

        GameEvent(
            id = "cosmic_underground_river",
            title = context?.getString(R.string.event_cosmic_underground_river_title) ?: "The Sound Beneath Sound",
            description = context?.getString(R.string.event_cosmic_underground_river_description) ?: "Seismic sensors have detected a massive underground river system directly beneath the vault — one that wasn't on any pre-war geological survey. The water is moving fast, it's deep, and preliminary analysis suggests it may be clean.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_cosmic_underground_river_choice_a_label) ?: "Drill down and tap the river",
                description = context?.getString(R.string.event_cosmic_underground_river_choice_a_desc) ?: "Invest construction resources in accessing this potential water source.",
                knownEffect = context?.getString(R.string.event_cosmic_underground_river_choice_a_effect) ?: "Could solve water scarcity permanently. Drilling risks structural integrity.",
                hiddenRisk = 0.35f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("waterScanner" to 30, "constructionGear" to -20, "structureScanner" to -10),
                    narrativeText = context?.getString(R.string.event_cosmic_underground_river_choice_a_outcome) ?: "The drill breaks through at forty meters. Clean water — cleaner than anything your recyclers produce — flows into the new cistern. The vault has found its heartbeat."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_cosmic_underground_river_choice_b_label) ?: "Conduct extensive surveys first",
                description = context?.getString(R.string.event_cosmic_underground_river_choice_b_desc) ?: "Map the river system thoroughly before any drilling.",
                knownEffect = context?.getString(R.string.event_cosmic_underground_river_choice_b_effect) ?: "Reduces risk. Delays access to water.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("waterScanner" to 10, "structureScanner" to 15),
                    databaseDeltas = mapOf("scientificArchive" to 10),
                    narrativeText = context?.getString(R.string.event_cosmic_underground_river_choice_b_outcome) ?: "The survey reveals the river is part of a vast network spanning hundreds of kilometers. The data is extraordinary. The water will wait — and so will you."
                )
            ),
            choiceC = EventChoice(
                label = context?.getString(R.string.event_cosmic_underground_river_choice_c_label) ?: "Ignore it — the risk isn't worth it",
                description = context?.getString(R.string.event_cosmic_underground_river_choice_c_desc) ?: "Leave the river undisturbed and maintain current water systems.",
                knownEffect = context?.getString(R.string.event_cosmic_underground_river_choice_c_effect) ?: "No risk. Missed opportunity.",
                outcome = EventOutcome(
                    narrativeText = context?.getString(R.string.event_cosmic_underground_river_choice_c_outcome) ?: "The river flows on beneath your feet, indifferent. You wonder sometimes, in the dark, what it sounds like. You never find out."
                )
            )
        ),

        GameEvent(
            id = "cosmic_time_anomaly",
            title = context?.getString(R.string.event_cosmic_time_anomaly_title) ?: "Yesterday's Clocks",
            description = context?.getString(R.string.event_cosmic_time_anomaly_description) ?: "Every clock in the vault has reset to the same date and time — the exact moment the bombs fell. Residents are reporting vivid, shared dreams of the surface before the war. Your chronometer systems are functioning perfectly. The clocks are simply wrong.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_cosmic_time_anomaly_choice_a_label) ?: "Document and study the phenomenon",
                description = context?.getString(R.string.event_cosmic_time_anomaly_choice_a_desc) ?: "Record every detail — the dreams, the clock readings, the timeline of events.",
                knownEffect = context?.getString(R.string.event_cosmic_time_anomaly_choice_a_effect) ?: "Valuable data. No practical benefit.",
                outcome = EventOutcome(
                    databaseDeltas = mapOf("culturalArchive" to 15, "scientificArchive" to 10),
                    narrativeText = context?.getString(R.string.event_cosmic_time_anomaly_choice_a_outcome) ?: "The documentation fills three archive volumes. The dreams are consistent across residents who've never spoken to each other. The clocks never do it again. The archive is richer for the mystery."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_cosmic_time_anomaly_choice_b_label) ?: "Dismiss it and reset the clocks",
                description = context?.getString(R.string.event_cosmic_time_anomaly_choice_b_desc) ?: "Treat it as a technical glitch and move on.",
                knownEffect = context?.getString(R.string.event_cosmic_time_anomaly_choice_b_effect) ?: "Maintains normalcy. Opportunity for understanding is lost.",
                outcome = EventOutcome(
                    narrativeText = context?.getString(R.string.event_cosmic_time_anomaly_choice_b_outcome) ?: "The clocks are reset. The official log reads 'chronometer malfunction, resolved.' Three residents who had the dreams never quite look at the walls the same way again."
                )
            ),
            choiceC = EventChoice(
                label = context?.getString(R.string.event_cosmic_time_anomaly_choice_c_label) ?: "Hold a remembrance ceremony",
                description = context?.getString(R.string.event_cosmic_time_anomaly_choice_c_desc) ?: "Use the moment to honor those lost on the day the world ended.",
                knownEffect = context?.getString(R.string.event_cosmic_time_anomaly_choice_c_effect) ?: "Significant morale boost. Emotional but not practical.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("medicalBay" to 5),
                    databaseDeltas = mapOf("culturalArchive" to 20),
                    narrativeText = context?.getString(R.string.event_cosmic_time_anomaly_choice_c_outcome) ?: "The vault gathers in the main hall. Names are read. Tears are shed for people most residents never knew. Something heavy lifts. The clocks are reset gently, with ceremony."
                )
            )
        ),

        GameEvent(
            id = "cosmic_bioluminescent_growth",
            title = context?.getString(R.string.event_cosmic_bioluminescent_growth_title) ?: "The Vault Blooms",
            description = context?.getString(R.string.event_cosmic_bioluminescent_growth_description) ?: "An unknown bioluminescent organism has begun growing in the lower maintenance corridors — soft blue-green light pulsing from the walls. It's spreading slowly but steadily. Analysis shows it's not harmful. Analysis also shows it shouldn't exist.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_cosmic_bioluminescent_growth_choice_a_label) ?: "Cultivate and study it",
                description = context?.getString(R.string.event_cosmic_bioluminescent_growth_choice_a_desc) ?: "Encourage its growth in controlled areas and conduct extensive research.",
                knownEffect = context?.getString(R.string.event_cosmic_bioluminescent_growth_choice_a_effect) ?: "Scientific value. Unknown long-term effects.",
                hiddenRisk = 0.3f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("powerGrid" to 5),
                    databaseDeltas = mapOf("scientificArchive" to 20),
                    narrativeText = context?.getString(R.string.event_cosmic_bioluminescent_growth_choice_a_outcome) ?: "The organism proves to be a natural bioluminescent — it reduces the vault's lighting load measurably. The children call it the vault's heartlight. The scientists call it unprecedented. Both are right."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_cosmic_bioluminescent_growth_choice_b_label) ?: "Eradicate it immediately",
                description = context?.getString(R.string.event_cosmic_bioluminescent_growth_choice_b_desc) ?: "Treat the growth as a potential threat and remove it entirely.",
                knownEffect = context?.getString(R.string.event_cosmic_bioluminescent_growth_choice_b_effect) ?: "Eliminates unknown risk. Destroys something potentially valuable.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("constructionGear" to -5),
                    narrativeText = context?.getString(R.string.event_cosmic_bioluminescent_growth_choice_b_outcome) ?: "The organism is scrubbed from the walls with industrial cleaner. The maintenance corridors are dark again. Three weeks later, it starts growing back in a different section."
                )
            ),
            choiceC = EventChoice(
                label = context?.getString(R.string.event_cosmic_bioluminescent_growth_choice_c_label) ?: "Let it grow unchecked",
                description = context?.getString(R.string.event_cosmic_bioluminescent_growth_choice_c_desc) ?: "Observe without intervention and see what it becomes.",
                knownEffect = context?.getString(R.string.event_cosmic_bioluminescent_growth_choice_c_effect) ?: "No cost. Organism may spread unpredictably.",
                hiddenRisk = 0.35f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("atmosphereScrubbers" to -10),
                    databaseDeltas = mapOf("scientificArchive" to 10),
                    narrativeText = context?.getString(R.string.event_cosmic_bioluminescent_growth_choice_c_outcome) ?: "The organism spreads into the ventilation system. It's still not harmful — but it's changing the air composition in subtle ways. The scrubbers work harder. The vault glows."
                )
            )
        ),

        GameEvent(
            id = "cosmic_ghost_signal",
            title = context?.getString(R.string.event_cosmic_ghost_signal_title) ?: "Channel Zero",
            description = context?.getString(R.string.event_cosmic_ghost_signal_description) ?: "Your internal broadcast system has begun receiving a signal on a frequency that doesn't exist — below the range of any known technology. The signal contains what sounds like a human voice reading names. Some of the names are people who died in your vault.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_cosmic_ghost_signal_choice_a_label) ?: "Record and analyze the signal",
                description = context?.getString(R.string.event_cosmic_ghost_signal_choice_a_desc) ?: "Capture everything and attempt to decode the full transmission.",
                knownEffect = context?.getString(R.string.event_cosmic_ghost_signal_choice_a_effect) ?: "May yield information. Deeply unsettling for residents.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("medicalBay" to -5),
                    databaseDeltas = mapOf("culturalArchive" to 10, "scientificArchive" to 15),
                    narrativeText = context?.getString(R.string.event_cosmic_ghost_signal_choice_a_outcome) ?: "The full transmission contains 847 names — every person who has died in the vault since it sealed. The last name on the list is someone who is currently alive and in perfect health. The recording is archived and never played publicly."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_cosmic_ghost_signal_choice_b_label) ?: "Shut down the broadcast system",
                description = context?.getString(R.string.event_cosmic_ghost_signal_choice_b_desc) ?: "Kill the receivers to stop the signal from spreading through the vault.",
                knownEffect = context?.getString(R.string.event_cosmic_ghost_signal_choice_b_effect) ?: "Stops the signal. Loses internal communications.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("securitySystem" to -10),
                    narrativeText = context?.getString(R.string.event_cosmic_ghost_signal_choice_b_outcome) ?: "The broadcast system goes dark. The signal stops. Internal communications revert to runners and handwritten notes for two weeks while the system is rebuilt. The signal never returns."
                )
            )
        ),

        GameEvent(
            id = "cosmic_magnetic_anomaly",
            title = context?.getString(R.string.event_cosmic_magnetic_anomaly_title) ?: "North Is Lying",
            description = context?.getString(R.string.event_cosmic_magnetic_anomaly_description) ?: "Every compass in the vault is pointing in a different direction. Your navigation systems are confused. More disturbingly, several residents report feeling profoundly disoriented — a bone-deep wrongness, like the world has shifted on its axis.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_cosmic_magnetic_anomaly_choice_a_label) ?: "Deploy surface sensors to map the anomaly",
                description = context?.getString(R.string.event_cosmic_magnetic_anomaly_choice_a_desc) ?: "Send instruments to the surface to characterize the magnetic disturbance.",
                knownEffect = context?.getString(R.string.event_cosmic_magnetic_anomaly_choice_a_effect) ?: "Gathers data. Surface team may be affected by the anomaly.",
                hiddenRisk = 0.3f,
                outcome = EventOutcome(
                    probesDelta = -1,
                    systemDeltas = mapOf("radiationScanner" to 10),
                    databaseDeltas = mapOf("scientificArchive" to 15),
                    narrativeText = context?.getString(R.string.event_cosmic_magnetic_anomaly_choice_a_outcome) ?: "The sensors reveal a massive magnetic pole shift in progress — the planet's field is reorganizing. It will pass in weeks. The data is extraordinary. The disorientation fades. The world is different now, in ways that will take years to understand."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_cosmic_magnetic_anomaly_choice_b_label) ?: "Increase medical monitoring of affected residents",
                description = context?.getString(R.string.event_cosmic_magnetic_anomaly_choice_b_desc) ?: "Focus on the human impact and ensure no one is seriously harmed.",
                knownEffect = context?.getString(R.string.event_cosmic_magnetic_anomaly_choice_b_effect) ?: "Protects residents. Limited scientific value.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("medicalBay" to -5),
                    narrativeText = context?.getString(R.string.event_cosmic_magnetic_anomaly_choice_b_outcome) ?: "The disorientation passes within days for most residents. Two experience lingering vertigo for weeks. The medical bay manages it. The cause remains unexplained in the official record."
                )
            )
        ),

        GameEvent(
            id = "cosmic_dream_plague",
            title = context?.getString(R.string.event_cosmic_dream_plague_title) ?: "The Shared Nightmare",
            description = context?.getString(R.string.event_cosmic_dream_plague_description) ?: "For seven consecutive nights, every resident of the vault has reported the same dream — a vast dark ocean, a light beneath the water, and a voice counting down from an unknown number. No one knows what it's counting toward.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_cosmic_dream_plague_choice_a_label) ?: "Convene a research team",
                description = context?.getString(R.string.event_cosmic_dream_plague_choice_a_desc) ?: "Treat this as a scientific phenomenon and investigate systematically.",
                knownEffect = context?.getString(R.string.event_cosmic_dream_plague_choice_a_effect) ?: "May yield answers. Residents are already frightened.",
                outcome = EventOutcome(
                    databaseDeltas = mapOf("scientificArchive" to 15, "culturalArchive" to 10),
                    narrativeText = context?.getString(R.string.event_cosmic_dream_plague_choice_a_outcome) ?: "The research team finds a pattern — the countdown correlates with a deep-frequency pulse emanating from directly below the vault. Something is down there. It has been counting for a long time."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_cosmic_dream_plague_choice_b_label) ?: "Address it as a psychological event",
                description = context?.getString(R.string.event_cosmic_dream_plague_choice_b_desc) ?: "Treat the shared dream as mass stress response and provide counseling.",
                knownEffect = context?.getString(R.string.event_cosmic_dream_plague_choice_b_effect) ?: "Calms residents. Ignores potential real cause.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("medicalBay" to -10),
                    narrativeText = context?.getString(R.string.event_cosmic_dream_plague_choice_b_outcome) ?: "The counseling helps. The dreams fade after two weeks. The pulse beneath the vault continues, undetected, uncounted. Whatever it was counting toward arrives without announcement."
                )
            ),
            choiceC = EventChoice(
                label = context?.getString(R.string.event_cosmic_dream_plague_choice_c_label) ?: "Drill toward the source of the pulse",
                description = context?.getString(R.string.event_cosmic_dream_plague_choice_c_desc) ?: "Follow the signal to its origin beneath the vault.",
                knownEffect = context?.getString(R.string.event_cosmic_dream_plague_choice_c_effect) ?: "May reveal the cause. Drilling risks are real.",
                hiddenRisk = 0.45f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("constructionGear" to -20, "structureScanner" to -10),
                    databaseDeltas = mapOf("scientificArchive" to 25),
                    narrativeText = context?.getString(R.string.event_cosmic_dream_plague_choice_c_outcome) ?: "At sixty meters, the drill breaks into a pre-war chamber — sealed, pressurized, and filled with equipment that predates the vault by decades. Someone built something here before the war. The dreams stop the moment the chamber is breached."
                )
            )
        ),

        GameEvent(
            id = "cosmic_star_map",
            title = context?.getString(R.string.event_cosmic_star_map_title) ?: "Someone Drew the Sky",
            description = context?.getString(R.string.event_cosmic_star_map_description) ?: "A child has been drawing the same image obsessively for two weeks — a precise star map of a constellation that doesn't match any pre-war astronomical record. When shown to your astronomer, she goes pale. The stars in the drawing are real. They're just not visible from Earth.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_cosmic_star_map_choice_a_label) ?: "Document and archive the drawings",
                description = context?.getString(R.string.event_cosmic_star_map_choice_a_desc) ?: "Preserve the images and attempt to identify the star system depicted.",
                knownEffect = context?.getString(R.string.event_cosmic_star_map_choice_a_effect) ?: "Preserves the mystery. Scientific value uncertain.",
                outcome = EventOutcome(
                    databaseDeltas = mapOf("culturalArchive" to 10, "scientificArchive" to 20),
                    narrativeText = context?.getString(R.string.event_cosmic_star_map_choice_a_outcome) ?: "The astronomer works for three months. The star system is real — catalogued by a pre-war deep space telescope that was decommissioned before the war. The telescope's final log entry is a set of coordinates and the word 'incoming.' The archive grows heavier."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_cosmic_star_map_choice_b_label) ?: "Speak with the child extensively",
                description = context?.getString(R.string.event_cosmic_star_map_choice_b_desc) ?: "Try to understand where the knowledge is coming from.",
                knownEffect = context?.getString(R.string.event_cosmic_star_map_choice_b_effect) ?: "May yield information. Child's wellbeing is a concern.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("medicalBay" to -5),
                    databaseDeltas = mapOf("culturalArchive" to 15),
                    narrativeText = context?.getString(R.string.event_cosmic_star_map_choice_b_outcome) ?: "The child says she sees it when she closes her eyes — the stars, and something moving between them, very slowly, very deliberately. She draws it every day. She seems happy. That is the most unsettling part."
                )
            )
        ),

        GameEvent(
            id = "cosmic_vault_echo",
            title = context?.getString(R.string.event_cosmic_vault_echo_title) ?: "The Vault Remembers",
            description = context?.getString(R.string.event_cosmic_vault_echo_description) ?: "Residents in the eastern residential sector are reporting hearing conversations — clear, detailed, mundane — of people who are not there. The voices discuss daily life in the vault as it was twenty years ago. The vault's original inhabitants, speaking from somewhere time forgot.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_cosmic_vault_echo_choice_a_label) ?: "Record the voices",
                description = context?.getString(R.string.event_cosmic_vault_echo_choice_a_desc) ?: "Set up audio equipment to capture the phenomenon.",
                knownEffect = context?.getString(R.string.event_cosmic_vault_echo_choice_a_effect) ?: "Preserves the voices. Emotionally difficult for long-term residents.",
                outcome = EventOutcome(
                    databaseDeltas = mapOf("culturalArchive" to 25),
                    narrativeText = context?.getString(R.string.event_cosmic_vault_echo_choice_a_outcome) ?: "The recordings are extraordinary — conversations, laughter, arguments, a birthday song. The cultural archive gains something no data drive was ever meant to hold: the sound of the vault when it was young."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_cosmic_vault_echo_choice_b_label) ?: "Investigate the structural cause",
                description = context?.getString(R.string.event_cosmic_vault_echo_choice_b_desc) ?: "Look for acoustic anomalies or resonance chambers that could explain the phenomenon.",
                knownEffect = context?.getString(R.string.event_cosmic_vault_echo_choice_b_effect) ?: "May find a rational explanation. May not.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("structureScanner" to 10),
                    narrativeText = context?.getString(R.string.event_cosmic_vault_echo_choice_b_outcome) ?: "The structural survey finds nothing unusual. The voices continue for eleven days and then stop. The eastern sector is quieter than it has ever been. Residents move their bunks away from the walls."
                )
            )
        ),

        GameEvent(
            id = "cosmic_second_vault",
            title = context?.getString(R.string.event_cosmic_second_vault_title) ?: "The Other Door",
            description = context?.getString(R.string.event_cosmic_second_vault_description) ?: "A routine maintenance inspection has discovered a sealed door in the deepest sub-level — one that appears on no blueprint, was built with different materials than the rest of the vault, and has a lock that uses a key format you've never seen. It has been here the whole time.",
            choiceA = EventChoice(
                label = context?.getString(R.string.event_cosmic_second_vault_choice_a_label) ?: "Force the door open",
                description = context?.getString(R.string.event_cosmic_second_vault_choice_a_desc) ?: "Apply construction resources to breach the unknown door.",
                knownEffect = context?.getString(R.string.event_cosmic_second_vault_choice_a_effect) ?: "Reveals what's inside. Unknown consequences.",
                hiddenRisk = 0.5f,
                outcome = EventOutcome(
                    survivorDelta = -1,
                    systemDeltas = mapOf("constructionGear" to -15),
                    databaseDeltas = mapOf("scientificArchive" to 30, "culturalArchive" to 15),
                    narrativeText = context?.getString(R.string.event_cosmic_second_vault_choice_a_outcome) ?: "The door opens onto a second vault — smaller, older, and occupied. The single resident inside has been living there since before your vault was sealed. She is very old and very calm. She says she has been waiting for you to be ready. One of your engineers dies of shock. She begins to talk."
                )
            ),
            choiceB = EventChoice(
                label = context?.getString(R.string.event_cosmic_second_vault_choice_b_label) ?: "Attempt to decode the lock",
                description = context?.getString(R.string.event_cosmic_second_vault_choice_b_desc) ?: "Study the lock mechanism and try to open it properly.",
                knownEffect = context?.getString(R.string.event_cosmic_second_vault_choice_b_effect) ?: "Safer approach. May take significant time.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("powerGrid" to -5),
                    databaseDeltas = mapOf("scientificArchive" to 15),
                    narrativeText = context?.getString(R.string.event_cosmic_second_vault_choice_b_outcome) ?: "The lock is decoded after three weeks of analysis. The mechanism is elegant — pre-war, but not government. Private. The door opens to a room full of files, all marked with a symbol that appears nowhere in any archive. You begin to read."
                )
            ),
            choiceC = EventChoice(
                label = context?.getString(R.string.event_cosmic_second_vault_choice_c_label) ?: "Seal the sub-level and tell no one",
                description = context?.getString(R.string.event_cosmic_second_vault_choice_c_desc) ?: "Document the door's existence privately and leave it undisturbed.",
                knownEffect = context?.getString(R.string.event_cosmic_second_vault_choice_c_effect) ?: "No immediate risk. The secret will not keep forever.",
                hiddenRisk = 0.3f,
                outcome = EventOutcome(
                    narrativeText = context?.getString(R.string.event_cosmic_second_vault_choice_c_outcome) ?: "You seal the sub-level and write the only record in your personal log. Six months later, a maintenance worker finds the door on their own. The secret is out. They want to know why you kept it. You don't have a good answer."
                )
            )
        )
    )
}
