package com.wardenprotocol.game.data.repository

import com.wardenprotocol.game.data.model.EventChoice
import com.wardenprotocol.game.data.model.EventOutcome
import com.wardenprotocol.game.data.model.GameEvent

class EventRepository {

    fun getAllEvents(): List<GameEvent> = (
        vaultEvents + ExpandedEventCatalog.vaultEvents +
            surfaceEvents + ExpandedEventCatalog.surfaceEvents +
            cosmicEvents + ExpandedEventCatalog.cosmicEvents +
            ExpandedEventCatalog.apexThreatEvents
        ).map(GameEvent::withExpandedBriefing)

    // ── VAULT INTERNAL EVENTS ─────────────────────────────────────────────────

    private val vaultEvents = listOf(

        GameEvent(
            id = "vault_power_failure",
            title = "Lights Out",
            description = "The main power grid sputters and dies with a sound like a dying breath. Emergency red lighting casts long shadows across the corridors. Your people are already whispering — darkness breeds panic faster than any plague.",
            choiceA = EventChoice(
                label = "Reroute auxiliary power",
                description = "Divert power from non-essential systems to restore lighting and life support.",
                knownEffect = "Restores power. Strains food storage cooling.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("powerGrid" to 15, "foodStores" to -8),
                    narrativeText = "The lights flicker back on. Cheers echo through the vault — brief, fragile hope. The food stores will pay the price."
                )
            ),
            choiceB = EventChoice(
                label = "Send engineers to the reactor",
                description = "Dispatch your best technicians to diagnose and repair the root cause.",
                knownEffect = "Full repair possible. Takes time, risks engineer casualties.",
                hiddenRisk = 0.35f,
                outcome = EventOutcome(
                    survivorDelta = -2,
                    systemDeltas = mapOf("powerGrid" to 30),
                    narrativeText = "The engineers work through the dark. Two don't come back — a fault in the conduit. But the grid hums strong again."
                )
            ),
            choiceC = EventChoice(
                label = "Ration power strictly",
                description = "Lock down all but critical systems and wait for a natural resolution.",
                knownEffect = "Conserves power. Morale will suffer.",
                hiddenRisk = 0.3f,
                outcome = EventOutcome(
                    survivorDelta = -1,
                    systemDeltas = mapOf("powerGrid" to 5, "foodStores" to -5, "medicalBay" to -5),
                    narrativeText = "Three days of cold and dark. One elderly resident doesn't survive the chill. The vault endures, but it remembers."
                )
            )
        ),

        GameEvent(
            id = "vault_coup_attempt",
            title = "The Warden's Chair",
            description = "Commander Holt has been quietly gathering loyalists for weeks. Tonight, armed with stolen security codes and grievances, his faction moves on the command center. You have minutes before they reach the door.",
            choiceA = EventChoice(
                label = "Seal the command center and negotiate",
                description = "Lock down and open a channel — hear their demands before blood is spilled.",
                knownEffect = "Avoids immediate violence. May require concessions.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("securitySystem" to -10),
                    narrativeText = "Holt's voice crackles through the intercom. His demands are not unreasonable. You make promises you may not keep — but the vault stays whole."
                )
            ),
            choiceB = EventChoice(
                label = "Activate security countermeasures",
                description = "Deploy vault security to intercept and detain the conspirators by force.",
                knownEffect = "Suppresses coup. Casualties likely on both sides.",
                hiddenRisk = 0.4f,
                outcome = EventOutcome(
                    survivorDelta = -4,
                    systemDeltas = mapOf("securitySystem" to 10),
                    narrativeText = "The corridors run red. Holt is in chains. Four are dead — two of his, two of yours. The vault is quieter now, and colder."
                )
            ),
            choiceC = EventChoice(
                label = "Step down temporarily",
                description = "Yield command under protest, buying time to rebuild support.",
                knownEffect = "No immediate casualties. You lose authority.",
                hiddenRisk = 0.45f,
                outcome = EventOutcome(
                    survivorDelta = -1,
                    systemDeltas = mapOf("securitySystem" to -20),
                    narrativeText = "Holt takes the chair. His first order is to ration your rations. You watch from the shadows, waiting. One of your allies disappears."
                )
            )
        ),

        GameEvent(
            id = "vault_plague_outbreak",
            title = "The Coughing Ward",
            description = "It started in Sector 7 — a wet, rattling cough that spreads faster than rumor. The medical bay is overwhelmed within forty-eight hours. Whatever this is, it didn't come from outside.",
            choiceA = EventChoice(
                label = "Quarantine Sector 7 immediately",
                description = "Seal the infected sector and restrict all movement.",
                knownEffect = "Slows spread. Trapped residents may die without care.",
                hiddenRisk = 0.3f,
                outcome = EventOutcome(
                    survivorDelta = -3,
                    systemDeltas = mapOf("medicalBay" to -10),
                    narrativeText = "The quarantine holds. Three die behind the sealed doors — you hear them until you don't. The rest of the vault is spared."
                )
            ),
            choiceB = EventChoice(
                label = "Mobilize all medical resources",
                description = "Throw everything at treatment — burn through supplies to save as many as possible.",
                knownEffect = "Saves more lives short-term. Depletes medical stores.",
                outcome = EventOutcome(
                    survivorDelta = -1,
                    systemDeltas = mapOf("medicalBay" to -25),
                    narrativeText = "Your doctors work without sleep. One survivor. The medical bay is gutted — if something else comes, you'll have nothing left."
                )
            ),
            choiceC = EventChoice(
                label = "Research the pathogen",
                description = "Divert resources to identifying the disease before treating it.",
                knownEffect = "May yield a cure. Costs time and lives.",
                hiddenRisk = 0.4f,
                outcome = EventOutcome(
                    survivorDelta = -5,
                    systemDeltas = mapOf("medicalBay" to 15),
                    databaseDeltas = mapOf("scientificArchive" to 10),
                    narrativeText = "Five die while you study. But the pathogen is catalogued, understood, defeated. The archive grows heavier with hard-won knowledge."
                )
            )
        ),

        GameEvent(
            id = "vault_birth_surge",
            title = "New Voices",
            description = "Seven births in a single week — an unprecedented surge. The vault rings with infant cries for the first time in years. It is beautiful and terrifying in equal measure.",
            choiceA = EventChoice(
                label = "Celebrate and allocate extra rations",
                description = "Boost morale with a formal ceremony and increased food allocation for new mothers.",
                knownEffect = "Morale rises. Food stores decrease.",
                outcome = EventOutcome(
                    survivorDelta = 7,
                    systemDeltas = mapOf("foodStores" to -10),
                    narrativeText = "Laughter fills the corridors. Seven new names are carved into the vault wall. The food stores thin, but hope is a kind of nourishment too."
                )
            ),
            choiceB = EventChoice(
                label = "Implement strict resource planning",
                description = "Register the births and immediately adjust ration schedules to compensate.",
                knownEffect = "Preserves resources. Resentment may follow.",
                outcome = EventOutcome(
                    survivorDelta = 7,
                    systemDeltas = mapOf("foodStores" to -3),
                    narrativeText = "The ledger is updated before the umbilical cords are cut. Efficient. Cold. The vault survives another generation."
                )
            )
        ),

        GameEvent(
            id = "vault_reactor_leak",
            title = "Sector Green Goes Red",
            description = "Radiation alarms scream from the reactor level. A hairline fracture in coolant pipe 4-C is venting slow death into the lower corridors. You have hours before it becomes irreversible.",
            choiceA = EventChoice(
                label = "Send a repair crew immediately",
                description = "Volunteer engineers suit up and enter the hot zone to seal the fracture.",
                knownEffect = "Stops the leak. Crew faces radiation exposure.",
                hiddenRisk = 0.4f,
                outcome = EventOutcome(
                    survivorDelta = -2,
                    systemDeltas = mapOf("powerGrid" to 5, "medicalBay" to -10),
                    narrativeText = "The crew seals the fracture. Two develop acute radiation sickness within days. The medical bay fights for them — and loses."
                )
            ),
            choiceB = EventChoice(
                label = "Evacuate lower levels and contain remotely",
                description = "Clear the affected sectors and attempt a remote patch via the control systems.",
                knownEffect = "No crew casualties. Partial fix only — leak may worsen.",
                hiddenRisk = 0.35f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("powerGrid" to -15, "atmosphereScrubbers" to -20),
                    narrativeText = "The remote patch holds — barely. Radiation seeps into the scrubber system. The air tastes faintly metallic for weeks."
                )
            ),
            choiceC = EventChoice(
                label = "Shut down the reactor entirely",
                description = "Emergency shutdown. Safe, but the vault goes dark and cold.",
                knownEffect = "Eliminates radiation risk. Power loss is severe.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("powerGrid" to -40, "foodStores" to -15),
                    narrativeText = "Darkness. The reactor goes cold and so does everything else. Food spoils in the warming storage units. You buy safety with hunger."
                )
            )
        ),

        GameEvent(
            id = "vault_food_shortage",
            title = "Empty Shelves",
            description = "The hydroponics yield failed for the second consecutive cycle. The food stores are at critical levels. Faces are gaunt, tempers are short, and the rationing board has been vandalized twice this week.",
            choiceA = EventChoice(
                label = "Implement emergency half-rations",
                description = "Cut all rations by fifty percent and stretch supplies as far as possible.",
                knownEffect = "Extends food supply. Health and morale will decline.",
                hiddenRisk = 0.3f,
                outcome = EventOutcome(
                    survivorDelta = -2,
                    systemDeltas = mapOf("foodStores" to 20, "medicalBay" to -10),
                    narrativeText = "Hunger becomes the vault's new rhythm. Two elderly residents slip away quietly. The rest endure — hollowed out but alive."
                )
            ),
            choiceB = EventChoice(
                label = "Sacrifice the seed reserves for immediate food",
                description = "Process the agricultural seed bank into emergency rations.",
                knownEffect = "Immediate food relief. Future harvests compromised.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("foodStores" to 30, "agriculturalScanner" to -20),
                    narrativeText = "Bellies are full tonight. But the seed vault is empty. Whatever grows on the surface now, you'll have nothing to plant."
                )
            ),
            choiceC = EventChoice(
                label = "Send a surface foraging team",
                description = "Dispatch scouts to scavenge pre-war food caches from nearby ruins.",
                knownEffect = "May recover food. Team faces surface dangers.",
                hiddenRisk = 0.45f,
                outcome = EventOutcome(
                    survivorDelta = -1,
                    systemDeltas = mapOf("foodStores" to 25),
                    narrativeText = "The team returns with canned goods and one fewer member. Nobody asks what happened. The food is distributed without ceremony."
                )
            )
        ),

        GameEvent(
            id = "vault_water_contamination",
            title = "The Taste of Rust",
            description = "The water recycler has been compromised — heavy metal contamination is spreading through the drinking supply. Three residents are already showing neurological symptoms. The source is somewhere in the filtration stack.",
            choiceA = EventChoice(
                label = "Flush and replace the filtration media",
                description = "Shut down water recycling and perform a full media replacement.",
                knownEffect = "Fixes contamination. Water supply offline for 48 hours.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("waterScanner" to 20, "foodStores" to -5),
                    narrativeText = "Two days of thirst. Rationed water from emergency reserves. When the taps run clear again, people drink until they're sick from relief."
                )
            ),
            choiceB = EventChoice(
                label = "Treat the affected residents and monitor",
                description = "Focus medical resources on the symptomatic and continue using the water supply.",
                knownEffect = "Keeps water flowing. Contamination may spread further.",
                hiddenRisk = 0.4f,
                outcome = EventOutcome(
                    survivorDelta = -2,
                    systemDeltas = mapOf("medicalBay" to -15),
                    narrativeText = "The contamination spreads before you catch it. Two more fall ill and don't recover. The filtration is fixed too late for them."
                )
            )
        ),

        GameEvent(
            id = "vault_structural_collapse",
            title = "The Ceiling Speaks",
            description = "A deep groan reverberates through the vault's bones — then a section of Corridor 12 collapses without warning. Dust fills the air. Someone is screaming from beneath the rubble.",
            choiceA = EventChoice(
                label = "Launch immediate rescue operation",
                description = "Send construction crews in to dig out survivors before the structure shifts further.",
                knownEffect = "May save trapped survivors. Risks further collapse.",
                hiddenRisk = 0.35f,
                outcome = EventOutcome(
                    survivorDelta = -1,
                    systemDeltas = mapOf("constructionGear" to -15, "structureScanner" to -10),
                    narrativeText = "You pull two people out alive. One rescuer is lost when a secondary collapse hits. The corridor is shored up with whatever you have left."
                )
            ),
            choiceB = EventChoice(
                label = "Seal the corridor and assess damage",
                description = "Prioritize structural integrity — seal the section and survey before acting.",
                knownEffect = "Prevents further collapse. Trapped survivors may not survive the wait.",
                outcome = EventOutcome(
                    survivorDelta = -3,
                    systemDeltas = mapOf("structureScanner" to 10),
                    narrativeText = "The survey reveals three dead in the rubble. The corridor is stable now. You add their names to the wall and move on."
                )
            ),
            choiceC = EventChoice(
                label = "Reinforce adjacent sections preemptively",
                description = "Use construction resources to stabilize surrounding corridors before they fail too.",
                knownEffect = "Prevents future collapses. Abandons those currently trapped.",
                outcome = EventOutcome(
                    survivorDelta = -2,
                    systemDeltas = mapOf("constructionGear" to -10, "structureScanner" to 15),
                    narrativeText = "The reinforcement holds. Two are lost in the original collapse — a price paid for the safety of hundreds. The math is brutal and correct."
                )
            )
        ),

        GameEvent(
            id = "vault_air_scrubber_failure",
            title = "Thin Air",
            description = "CO2 levels are climbing. The atmospheric scrubbers are failing — a cascade of clogged filters and burned-out fans. The air is growing thick and slow. Headaches are universal. Judgment is impaired.",
            choiceA = EventChoice(
                label = "Emergency manual filter replacement",
                description = "Pull every available hand to manually replace scrubber filters around the clock.",
                knownEffect = "Restores air quality. Exhausts workforce.",
                outcome = EventOutcome(
                    survivorDelta = -1,
                    systemDeltas = mapOf("atmosphereScrubbers" to 25),
                    narrativeText = "Twelve hours of frantic work. One person collapses from CO2 exposure and doesn't wake up. The air clears. Everyone breathes a little easier — except her."
                )
            ),
            choiceB = EventChoice(
                label = "Reduce population density in affected zones",
                description = "Consolidate residents into the best-ventilated sections of the vault.",
                knownEffect = "Buys time. Crowding creates new problems.",
                hiddenRisk = 0.3f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("atmosphereScrubbers" to 10, "securitySystem" to -5),
                    narrativeText = "Cramped quarters breed conflict. A fight breaks out over sleeping space. The scrubbers are repaired eventually, but the bruises linger."
                )
            )
        ),

        GameEvent(
            id = "vault_medical_epidemic",
            title = "Fever Season",
            description = "A hemorrhagic fever tears through the vault's children first, then the elderly. The medical bay is a war zone of cots and desperate faces. Your chief physician hasn't slept in four days.",
            choiceA = EventChoice(
                label = "Administer experimental antiviral",
                description = "Use the untested compound synthesized from pre-war research notes.",
                knownEffect = "May cure the fever. Unknown side effects.",
                hiddenRisk = 0.45f,
                outcome = EventOutcome(
                    survivorDelta = -2,
                    systemDeltas = mapOf("medicalBay" to 20),
                    databaseDeltas = mapOf("scientificArchive" to 5),
                    narrativeText = "The antiviral works on most. Two react badly and are gone within hours. The formula is refined and archived — a terrible lesson learned."
                )
            ),
            choiceB = EventChoice(
                label = "Strict supportive care only",
                description = "Manage symptoms, maintain hydration, and let the immune system fight.",
                knownEffect = "Safe approach. Higher mortality without active treatment.",
                outcome = EventOutcome(
                    survivorDelta = -5,
                    systemDeltas = mapOf("medicalBay" to -10),
                    narrativeText = "Five funerals in a week. The fever breaks on its own eventually. Your physician weeps in the corridor when it's over. You don't interrupt."
                )
            ),
            choiceC = EventChoice(
                label = "Request surface medical supply run",
                description = "Send a team to the ruins of the old hospital district for antibiotics and antivirals.",
                knownEffect = "Could secure real medicine. Surface team at risk.",
                hiddenRisk = 0.4f,
                outcome = EventOutcome(
                    survivorDelta = -1,
                    systemDeltas = mapOf("medicalBay" to 30),
                    narrativeText = "The team returns with a cache of sealed pharmaceuticals. One didn't make it back. The medicine saves a dozen. The math is grim but favorable."
                )
            )
        ),

        GameEvent(
            id = "vault_security_mutiny",
            title = "The Guards Walk Off",
            description = "Your security detail has laid down their weapons and barricaded themselves in the armory. Their grievance: they haven't seen their families in the residential sectors for three weeks due to your lockdown orders.",
            choiceA = EventChoice(
                label = "End the lockdown and negotiate",
                description = "Lift the residential restrictions and meet with the security team directly.",
                knownEffect = "Resolves mutiny. Lockdown benefits are lost.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("securitySystem" to 15),
                    narrativeText = "The armory doors open. Tearful reunions fill the corridors. The lockdown is over — and so is whatever it was protecting against. You hope."
                )
            ),
            choiceB = EventChoice(
                label = "Cut power to the armory",
                description = "Force them out by making the armory uninhabitable.",
                knownEffect = "Ends standoff quickly. Damages trust permanently.",
                hiddenRisk = 0.35f,
                outcome = EventOutcome(
                    survivorDelta = -1,
                    systemDeltas = mapOf("securitySystem" to -10, "powerGrid" to -5),
                    narrativeText = "They come out angry and humiliated. One makes a choice you can't take back. The security force is intact but hollow — they follow orders now, nothing more."
                )
            )
        ),

        GameEvent(
            id = "vault_knowledge_loss",
            title = "The Archive Burns",
            description = "A short circuit in the server room has destroyed a significant portion of the cultural archive. Irreplaceable records — music, literature, history — reduced to corrupted data. The vault's memory is bleeding out.",
            choiceA = EventChoice(
                label = "Mobilize residents to reconstruct from memory",
                description = "Organize community sessions to record what people remember before it fades.",
                knownEffect = "Partial recovery. Time-consuming but meaningful.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("powerGrid" to -5),
                    databaseDeltas = mapOf("culturalArchive" to 15),
                    narrativeText = "For two weeks, the vault tells stories. Songs are sung and written down. Not everything is recovered — but what remains is alive in a way data never was."
                )
            ),
            choiceB = EventChoice(
                label = "Prioritize server hardware recovery",
                description = "Focus technical resources on salvaging corrupted data from damaged drives.",
                knownEffect = "May recover more data. Requires technical resources.",
                hiddenRisk = 0.3f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("powerGrid" to -10),
                    databaseDeltas = mapOf("culturalArchive" to 20, "scientificArchive" to 5),
                    narrativeText = "The technicians recover fragments — partial texts, degraded audio. It's not everything. It's enough to remember what was lost."
                )
            ),
            choiceC = EventChoice(
                label = "Accept the loss and move forward",
                description = "Redirect resources to present survival rather than past preservation.",
                knownEffect = "No resource cost. Cultural memory is permanently diminished.",
                outcome = EventOutcome(
                    databaseDeltas = mapOf("culturalArchive" to -10),
                    narrativeText = "The loss is logged and filed. Life continues. Somewhere in the vault, an old man hums a song no one else remembers, and then he stops."
                )
            )
        ),

        GameEvent(
            id = "vault_suicide_crisis",
            title = "The Weight of Walls",
            description = "Three residents have taken their own lives in the past month. The vault's psychologist — if you can call one person a department — is overwhelmed. The walls are closing in on people who've never seen the sky.",
            choiceA = EventChoice(
                label = "Establish a community support network",
                description = "Train volunteers in crisis support and create open forums for residents to speak.",
                knownEffect = "Improves morale long-term. Requires time and organization.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("medicalBay" to 10),
                    databaseDeltas = mapOf("culturalArchive" to 5),
                    narrativeText = "The forums are awkward at first. Then raw. Then honest. People cry in front of strangers and feel less alone. The vault breathes differently."
                )
            ),
            choiceB = EventChoice(
                label = "Increase work assignments to reduce idle time",
                description = "Fill the schedule — busy hands and minds have less room for despair.",
                knownEffect = "Reduces crisis incidents. May mask underlying issues.",
                hiddenRisk = 0.3f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("constructionGear" to 10, "foodStores" to 5),
                    narrativeText = "The vault is more productive than ever. The grief doesn't disappear — it just goes underground, where you can't see it. For now, that's enough."
                )
            )
        ),

        GameEvent(
            id = "vault_child_disappearance",
            title = "Gone from Bunk 14",
            description = "A nine-year-old girl has vanished from the residential sector. No alarms triggered, no witnesses. The vault is small — there are only so many places to hide. Or be hidden.",
            choiceA = EventChoice(
                label = "Full vault lockdown and search",
                description = "Seal all sectors and conduct a systematic room-by-room search.",
                knownEffect = "Thorough search. Disrupts vault operations significantly.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("securitySystem" to 10, "foodStores" to -5),
                    narrativeText = "She's found in a maintenance crawlspace — alive, frightened, hiding from a man whose name you now know. Justice is swift and final in the vault."
                )
            ),
            choiceB = EventChoice(
                label = "Quiet investigation to avoid panic",
                description = "Conduct a discreet search while maintaining normal vault operations.",
                knownEffect = "Avoids panic. May be too slow.",
                hiddenRisk = 0.4f,
                outcome = EventOutcome(
                    survivorDelta = -1,
                    systemDeltas = mapOf("securitySystem" to -5),
                    narrativeText = "The quiet investigation takes too long. She is found, but not in time. The vault mourns in a silence that feels like guilt."
                )
            )
        ),

        GameEvent(
            id = "vault_generator_overload",
            title = "Burning From Within",
            description = "The backup generator has overloaded and caught fire in the mechanical bay. Suppression systems are partially functional. Smoke is filling the eastern corridors and the fire is spreading.",
            choiceA = EventChoice(
                label = "Activate all suppression systems",
                description = "Flood the mechanical bay with suppression agents regardless of equipment damage.",
                knownEffect = "Stops fire quickly. Damages equipment in the bay.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("powerGrid" to -20, "constructionGear" to -10),
                    narrativeText = "The fire dies in a cloud of white. The mechanical bay is a ruin of foam and scorched metal. The generator is gone, but the vault stands."
                )
            ),
            choiceB = EventChoice(
                label = "Manual firefighting team",
                description = "Send a crew in with extinguishers to fight the fire directly.",
                knownEffect = "Preserves more equipment. Crew faces serious danger.",
                hiddenRisk = 0.45f,
                outcome = EventOutcome(
                    survivorDelta = -2,
                    systemDeltas = mapOf("powerGrid" to -10),
                    narrativeText = "Two crew members are overcome by smoke. The fire is contained. The generator is salvageable. The cost is written in the medical bay's records."
                )
            ),
            choiceC = EventChoice(
                label = "Evacuate and let it burn out",
                description = "Clear the eastern corridors and seal the section — let the fire consume itself.",
                knownEffect = "No casualties. Significant structural and equipment loss.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("powerGrid" to -35, "structureScanner" to -15, "constructionGear" to -20),
                    narrativeText = "The fire burns for six hours. When it's done, the eastern wing is a blackened shell. You rebuild. You always rebuild."
                )
            )
        )
    )

    // ── SURFACE / EXTERNAL EVENTS ─────────────────────────────────────────────

    private val surfaceEvents = listOf(

        GameEvent(
            id = "surface_knock_on_door",
            title = "Three Knocks",
            description = "The external sensors register a deliberate knock on the vault door — three slow, measured impacts. Not the frantic pounding of the desperate. Someone out there knows the old signal. The intercom crackles with shallow breathing.",
            choiceA = EventChoice(
                label = "Open the intercom and respond",
                description = "Establish voice contact before making any decision about the door.",
                knownEffect = "Gathers information. Reveals your presence if unknown.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("threatAssessment" to 5),
                    narrativeText = "A woman's voice. Calm, educated, exhausted. She claims to be from Vault 9 — the one that was supposed to open first. She has information about the surface. You listen."
                )
            ),
            choiceB = EventChoice(
                label = "Open the door",
                description = "Extend trust and allow entry — survivors deserve a chance.",
                knownEffect = "Gains survivors and potential knowledge. Unknown risk.",
                hiddenRisk = 0.4f,
                outcome = EventOutcome(
                    survivorDelta = 3,
                    systemDeltas = mapOf("foodStores" to -8, "threatAssessment" to -10),
                    narrativeText = "Three people stumble in — gaunt, irradiated, grateful. They bring stories of the surface that keep you awake for nights. And one of them is sick in a way your scanner doesn't recognize."
                )
            ),
            choiceC = EventChoice(
                label = "Ignore it",
                description = "Maintain silence and hope they move on.",
                knownEffect = "No risk. No gain. They may return.",
                hiddenRisk = 0.3f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("threatAssessment" to -5),
                    narrativeText = "The knocking stops after an hour. The sensors show a figure sitting against the door for three days before the heat signature disappears. You don't speak of it."
                )
            )
        ),

        GameEvent(
            id = "surface_radio_signal",
            title = "Signal in the Static",
            description = "Your long-range antenna has picked up a repeating radio transmission — structured, deliberate, not automated. Someone is broadcasting on a pre-war emergency frequency. The signal is coming from forty kilometers northeast.",
            choiceA = EventChoice(
                label = "Respond to the signal",
                description = "Broadcast a reply and attempt to establish two-way communication.",
                knownEffect = "May make contact. Reveals your location.",
                hiddenRisk = 0.3f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("threatAssessment" to 10),
                    databaseDeltas = mapOf("scientificArchive" to 5),
                    narrativeText = "A settlement. Forty survivors in a fortified school. They have seeds. You have medicine. A trade is arranged. The world gets a little less lonely."
                )
            ),
            choiceB = EventChoice(
                label = "Send a probe to investigate",
                description = "Dispatch a surface drone to locate and observe the signal source.",
                knownEffect = "Gathers intelligence without revealing position. Costs a probe.",
                outcome = EventOutcome(
                    probesDelta = -1,
                    systemDeltas = mapOf("threatAssessment" to 20),
                    narrativeText = "The probe returns with footage of a fortified compound — armed, organized, and flying a flag you don't recognize. Knowledge without commitment. For now."
                )
            ),
            choiceC = EventChoice(
                label = "Monitor silently",
                description = "Record the transmission and analyze without responding.",
                knownEffect = "Safe. Limited information gain.",
                outcome = EventOutcome(
                    databaseDeltas = mapOf("scientificArchive" to 3),
                    narrativeText = "The signal repeats for eleven days, then stops. You have a recording of a voice reading coordinates and a phrase you can't translate. It haunts you."
                )
            )
        ),

        GameEvent(
            id = "surface_acid_rain",
            title = "The Sky Bleeds",
            description = "Atmospheric sensors detect a severe acid precipitation event moving toward your surface installations. Your external equipment — antennas, solar panels, probe bays — will be stripped bare if left unprotected.",
            choiceA = EventChoice(
                label = "Retract all surface equipment",
                description = "Pull everything inside before the storm hits.",
                knownEffect = "Protects equipment. Vault goes dark on surface sensors.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("powerGrid" to -10),
                    narrativeText = "Everything comes in. The storm lasts three days and sounds like the world ending. When it passes, the surface is scoured clean. Your equipment is intact."
                )
            ),
            choiceB = EventChoice(
                label = "Apply emergency sealant coatings",
                description = "Deploy protective coatings on critical external systems.",
                knownEffect = "Partial protection. Some equipment loss likely.",
                hiddenRisk = 0.35f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("resourceScanner" to -10, "atmosphereScrubbers" to -5),
                    narrativeText = "The sealant holds on most systems. The resource scanner takes a hit — its external array is pitted and degraded. You'll need to recalibrate."
                )
            ),
            choiceC = EventChoice(
                label = "Leave equipment exposed and document the storm",
                description = "Sacrifice some hardware to gather atmospheric data on the event.",
                knownEffect = "Significant equipment loss. Valuable scientific data gained.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("resourceScanner" to -20, "atmosphereScrubbers" to -10),
                    databaseDeltas = mapOf("scientificArchive" to 15),
                    narrativeText = "The data is extraordinary — acid composition, precipitation patterns, storm duration. The equipment is ruined. Science is expensive."
                )
            )
        ),

        GameEvent(
            id = "surface_scavenger_band",
            title = "Uninvited Guests",
            description = "Surface cameras show a band of twelve armed scavengers camped two hundred meters from the vault entrance. They've found the old access road. They're not leaving — they're digging.",
            choiceA = EventChoice(
                label = "Activate external deterrents",
                description = "Trigger the vault's external alarm systems and warning lights.",
                knownEffect = "May drive them off. Confirms vault location.",
                hiddenRisk = 0.3f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("securitySystem" to 5, "powerGrid" to -5),
                    narrativeText = "The alarms send half of them running. Four remain, more determined than before. They know something is here. They'll be back with more."
                )
            ),
            choiceB = EventChoice(
                label = "Send an armed delegation to negotiate",
                description = "Meet them on the surface — offer trade to redirect their interest.",
                knownEffect = "May resolve peacefully. Delegation faces real danger.",
                hiddenRisk = 0.4f,
                outcome = EventOutcome(
                    survivorDelta = -1,
                    systemDeltas = mapOf("foodStores" to -10, "threatAssessment" to 10),
                    narrativeText = "The negotiation goes badly at first, then worse. One of yours doesn't come back. But the scavengers take the offered food and move on. For now."
                )
            ),
            choiceC = EventChoice(
                label = "Wait and observe",
                description = "Monitor their activity and act only if they breach the outer door.",
                knownEffect = "No immediate cost. They may find the entrance.",
                hiddenRisk = 0.45f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("securitySystem" to -15),
                    narrativeText = "They find the secondary access hatch on day three. The breach alarm sounds at 0300. The security response is brutal and necessary."
                )
            )
        ),

        GameEvent(
            id = "surface_radiation_spike",
            title = "The Geiger Chorus",
            description = "External radiation monitors are screaming. A massive spike — three times baseline — is sweeping across the surface from the direction of the old city. Your surface teams have forty minutes to get underground.",
            choiceA = EventChoice(
                label = "Emergency recall of all surface teams",
                description = "Broadcast immediate recall and open the vault door for rapid entry.",
                knownEffect = "Saves surface teams. Door opening creates brief exposure risk.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("radiationScanner" to 10, "medicalBay" to -5),
                    narrativeText = "All teams make it back. Three show elevated radiation levels — manageable with treatment. The door seals just as the wave hits. Close."
                )
            ),
            choiceB = EventChoice(
                label = "Shelter teams in surface bunkers",
                description = "Direct teams to pre-positioned surface shelters rather than risking the vault door.",
                knownEffect = "Protects teams without opening vault. Bunker integrity unknown.",
                hiddenRisk = 0.4f,
                outcome = EventOutcome(
                    survivorDelta = -2,
                    systemDeltas = mapOf("radiationScanner" to 5),
                    narrativeText = "Two bunkers hold. One doesn't. The team inside received a lethal dose. They make it back to the vault to say goodbye. The medical bay does what it can."
                )
            )
        ),

        GameEvent(
            id = "surface_abandoned_vehicle",
            title = "The Truck That Drove Itself",
            description = "A pre-war military transport vehicle has rolled to a stop outside the vault entrance — engine still running, no driver visible. The cargo bay is sealed with a biohazard lock. The vehicle's markings are from a facility that was supposed to have been destroyed.",
            choiceA = EventChoice(
                label = "Send a hazmat team to investigate",
                description = "Approach with full protective gear and attempt to open the cargo bay.",
                knownEffect = "May yield valuable supplies. Unknown biohazard risk.",
                hiddenRisk = 0.45f,
                outcome = EventOutcome(
                    survivorDelta = -1,
                    systemDeltas = mapOf("medicalBay" to -10),
                    databaseDeltas = mapOf("scientificArchive" to 15),
                    narrativeText = "The cargo bay contains pre-war medical research — and a pathogen sample that breaches containment. One team member is lost. The research is invaluable. The cost is not forgotten."
                )
            ),
            choiceB = EventChoice(
                label = "Destroy the vehicle remotely",
                description = "Use the vault's external systems to detonate the vehicle at a safe distance.",
                knownEffect = "Eliminates unknown risk. Destroys potential resources.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("threatAssessment" to 10),
                    narrativeText = "The explosion is visible on every surface camera. Whatever was in that truck is ash now. You sleep better. You wonder what you burned."
                )
            ),
            choiceC = EventChoice(
                label = "Leave it and monitor",
                description = "Keep the vehicle under observation without approaching.",
                knownEffect = "No immediate risk. Situation may change.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("threatAssessment" to -5),
                    narrativeText = "The vehicle sits there for a week. Then someone opens it from the inside. The figure that emerges is not quite right. The security response is immediate."
                )
            )
        ),

        GameEvent(
            id = "surface_crop_test_failure",
            title = "Dead on Arrival",
            description = "Your surface agricultural test plot — three months of careful cultivation — has been destroyed overnight. The soil samples show a new fungal pathogen that wasn't in any pre-war database. The dream of surface farming just got further away.",
            choiceA = EventChoice(
                label = "Analyze the pathogen extensively",
                description = "Dedicate scientific resources to understanding and cataloguing the new fungus.",
                knownEffect = "Builds knowledge. No immediate food benefit.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("agriculturalScanner" to -10),
                    databaseDeltas = mapOf("scientificArchive" to 20),
                    narrativeText = "The pathogen is unlike anything in the archive. It's adaptive, aggressive, and possibly engineered. The knowledge is cold comfort for empty stomachs."
                )
            ),
            choiceB = EventChoice(
                label = "Try a different location immediately",
                description = "Relocate the test plot and attempt a second cultivation cycle.",
                knownEffect = "Maintains agricultural progress. May fail again.",
                hiddenRisk = 0.35f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("agriculturalScanner" to 10, "foodStores" to -5),
                    narrativeText = "The second plot takes hold in cleaner soil two kilometers east. It's fragile and small, but it's alive. The surface might feed you yet."
                )
            )
        ),

        GameEvent(
            id = "surface_survivor_child",
            title = "Small Footprints",
            description = "Surface sensors detect a single small heat signature moving erratically near the vault entrance — a child, alone, in the open. Radiation levels outside are elevated. They've been out there for at least six hours.",
            choiceA = EventChoice(
                label = "Open the door and bring them in",
                description = "Send a team out immediately to retrieve the child.",
                knownEffect = "Saves the child. Team faces radiation exposure.",
                outcome = EventOutcome(
                    survivorDelta = 1,
                    systemDeltas = mapOf("medicalBay" to -10, "foodStores" to -3),
                    narrativeText = "A girl, maybe seven. She doesn't speak for three days. When she does, the things she describes about the surface make your scouts go quiet. She stays."
                )
            ),
            choiceB = EventChoice(
                label = "Guide them via external speaker",
                description = "Use the external PA to direct the child to a surface shelter without opening the vault.",
                knownEffect = "Lower risk to vault. Child may not survive alone.",
                hiddenRisk = 0.4f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("threatAssessment" to 5),
                    narrativeText = "The child follows your voice to the shelter. You leave supplies in the drop box. The heat signature disappears the next morning. You tell yourself it means they moved on."
                )
            )
        ),

        GameEvent(
            id = "surface_military_drone",
            title = "Eyes in the Sky",
            description = "A pre-war military surveillance drone is circling your surface coordinates in a systematic pattern. It's broadcasting on a frequency that suggests it's still reporting to someone — or something — that is still operational.",
            choiceA = EventChoice(
                label = "Attempt to hack and disable the drone",
                description = "Use your technical systems to interfere with the drone's control signal.",
                knownEffect = "May disable surveillance. Could alert the controller.",
                hiddenRisk = 0.35f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("securitySystem" to 15, "powerGrid" to -10),
                    narrativeText = "The drone spirals down two kilometers away. Whatever was receiving its feed now has a gap in coverage — and knows something interfered. You've made yourself known."
                )
            ),
            choiceB = EventChoice(
                label = "Shut down all surface emissions",
                description = "Go dark — no heat signatures, no radio, no movement on the surface.",
                knownEffect = "May avoid detection. Disrupts surface operations.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("powerGrid" to -15, "resourceScanner" to -10),
                    narrativeText = "The drone completes its pattern and moves on. You were invisible. For now, that's enough. The question of who sent it keeps you awake."
                )
            ),
            choiceC = EventChoice(
                label = "Broadcast a greeting on its frequency",
                description = "Make contact — whoever controls that drone might be worth knowing.",
                knownEffect = "Establishes contact. Completely unknown consequences.",
                hiddenRisk = 0.5f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("threatAssessment" to -20),
                    databaseDeltas = mapOf("scientificArchive" to 10),
                    narrativeText = "A response comes — automated at first, then a human voice. They call themselves the Continuity. They know about your vault. They've known for years. They've been waiting for you to be ready."
                )
            )
        ),

        GameEvent(
            id = "surface_toxic_bloom",
            title = "The Green Death",
            description = "A massive toxic algae bloom has contaminated the surface water table in your region. Your water scanner shows the contamination reaching your secondary intake pipes. The primary filtration system was not designed for this compound.",
            choiceA = EventChoice(
                label = "Upgrade filtration with available materials",
                description = "Improvise an enhanced filtration stage using vault construction resources.",
                knownEffect = "Protects water supply. Costs construction resources.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("waterScanner" to 15, "constructionGear" to -15),
                    narrativeText = "The improvised filter holds. The water tastes strange for weeks but the toxin levels stay below dangerous thresholds. Engineering saves the day, as it usually does."
                )
            ),
            choiceB = EventChoice(
                label = "Switch entirely to recycled internal water",
                description = "Seal external intakes and rely solely on internal water recycling.",
                knownEffect = "Safe from contamination. Recycling system under heavy strain.",
                hiddenRisk = 0.3f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("waterScanner" to -10, "atmosphereScrubbers" to -5),
                    narrativeText = "The recycling system groans under the load. It holds — barely. The bloom passes in six weeks. The system needs maintenance it can't afford."
                )
            )
        ),

        GameEvent(
            id = "surface_pre_war_cache",
            title = "What the Earth Kept",
            description = "A surface probe has located a pre-war government emergency cache buried beneath a collapsed overpass. The manifest — partially readable — lists medical supplies, seeds, and encrypted data drives. It's two kilometers from the vault entrance.",
            choiceA = EventChoice(
                label = "Full retrieval mission",
                description = "Send a large team to excavate and recover everything in the cache.",
                knownEffect = "Maximum recovery. Large team exposure to surface risks.",
                hiddenRisk = 0.35f,
                outcome = EventOutcome(
                    survivorDelta = -1,
                    systemDeltas = mapOf("foodStores" to 20, "medicalBay" to 20),
                    databaseDeltas = mapOf("scientificArchive" to 15),
                    narrativeText = "The cache is everything the manifest promised and more. One team member is lost to a structural collapse during excavation. The vault is richer and heavier for it."
                )
            ),
            choiceB = EventChoice(
                label = "Small stealth team, priority items only",
                description = "Send two people to grab the highest-value items and return quickly.",
                knownEffect = "Lower risk. Partial recovery only.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("medicalBay" to 10),
                    databaseDeltas = mapOf("scientificArchive" to 8),
                    narrativeText = "The team returns in four hours with medical supplies and two data drives. The seeds are left behind — too heavy, too exposed. You'll go back for them. Maybe."
                )
            ),
            choiceC = EventChoice(
                label = "Send a probe to map it first",
                description = "Fully document the cache before committing to a retrieval.",
                knownEffect = "No risk now. Delays recovery. Cache may be found by others.",
                hiddenRisk = 0.3f,
                outcome = EventOutcome(
                    probesDelta = -1,
                    systemDeltas = mapOf("resourceScanner" to 10),
                    narrativeText = "The probe maps everything perfectly. When the retrieval team arrives three days later, the cache has been partially looted. Someone else found it first."
                )
            )
        ),

        GameEvent(
            id = "surface_earthquake",
            title = "The Ground Remembers",
            description = "A 6.2 magnitude earthquake strikes without warning. The vault shudders and groans. Dust falls from the ceiling in sheets. Surface installations are thrown into chaos and several internal systems report damage.",
            choiceA = EventChoice(
                label = "Emergency structural assessment",
                description = "Immediately deploy all available engineers to assess vault integrity.",
                knownEffect = "Identifies damage quickly. Engineers at risk from aftershocks.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("structureScanner" to 20, "constructionGear" to -10),
                    narrativeText = "Three critical stress fractures are found and shored up within hours. The vault holds. The aftershocks test it twice more. It holds again."
                )
            ),
            choiceB = EventChoice(
                label = "Shelter in place and wait for aftershocks to pass",
                description = "Keep everyone in reinforced sections until seismic activity stabilizes.",
                knownEffect = "Protects people. Damage goes unaddressed longer.",
                hiddenRisk = 0.3f,
                outcome = EventOutcome(
                    survivorDelta = -1,
                    systemDeltas = mapOf("structureScanner" to -10, "powerGrid" to -10),
                    narrativeText = "An aftershock collapses a section of the eastern corridor before you can assess it. One person is caught in the wrong place. The vault is stable but scarred."
                )
            )
        ),

        GameEvent(
            id = "surface_hostile_settlement",
            title = "Neighbors With Guns",
            description = "A new settlement has established itself eight kilometers from your vault — and they've sent an armed envoy demanding a 'protection tithe' of food and medicine. They know you're here. They know you have supplies.",
            choiceA = EventChoice(
                label = "Pay the tithe",
                description = "Comply with their demands to avoid conflict.",
                knownEffect = "Buys peace. Sets a precedent. Demands may grow.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("foodStores" to -15, "medicalBay" to -10),
                    narrativeText = "The envoy leaves satisfied. Three months later, they're back with larger demands. You've bought time, not safety."
                )
            ),
            choiceB = EventChoice(
                label = "Refuse and demonstrate vault defenses",
                description = "Turn the envoy away and activate visible external deterrents.",
                knownEffect = "Maintains independence. May provoke escalation.",
                hiddenRisk = 0.4f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("securitySystem" to 15, "threatAssessment" to -15),
                    narrativeText = "The envoy leaves angry. Two weeks later, a probe detects them scouting your perimeter. The deterrents hold them back — for now. The tension is a living thing."
                )
            ),
            choiceC = EventChoice(
                label = "Propose a mutual aid agreement",
                description = "Counter-offer with a formal trade arrangement rather than tribute.",
                knownEffect = "Potential long-term alliance. Requires ongoing resource commitment.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("foodStores" to -8, "threatAssessment" to 20),
                    narrativeText = "They're surprised by the counter-offer. After two days of negotiation, an agreement is reached. It's fragile and transactional — but it's not war."
                )
            )
        ),

        GameEvent(
            id = "surface_solar_flare",
            title = "The Sun Remembers It's Angry",
            description = "A massive solar flare is incoming — your instruments give you six hours of warning. The electromagnetic pulse will fry unshielded electronics on the surface and potentially penetrate the vault's upper levels.",
            choiceA = EventChoice(
                label = "Full electronic shutdown and shielding",
                description = "Power down all non-essential systems and activate Faraday shielding.",
                knownEffect = "Maximum protection. Vault goes dark for 12+ hours.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("powerGrid" to -10),
                    narrativeText = "Twelve hours of darkness and silence. The flare passes. Systems come back online one by one. Everything works. The preparation was worth every anxious hour."
                )
            ),
            choiceB = EventChoice(
                label = "Protect critical systems only",
                description = "Shield life support and power grid, leave secondary systems exposed.",
                knownEffect = "Faster recovery. Some systems will be damaged.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("resourceScanner" to -20, "radiationScanner" to -15),
                    narrativeText = "Life support survives intact. The resource and radiation scanners are fried — their surface arrays burned out by the pulse. You're flying partially blind now."
                )
            )
        ),

        GameEvent(
            id = "surface_animal_migration",
            title = "The Herd",
            description = "Surface cameras capture something extraordinary — a massive herd of mutated elk moving through the wasteland, thousands strong. They're large, strange, and apparently healthy. They're also heading directly toward your surface installations.",
            choiceA = EventChoice(
                label = "Attempt to harvest several animals",
                description = "Send a hunting team to the surface to take advantage of this rare opportunity.",
                knownEffect = "Significant food gain. Team faces unknown mutated animals.",
                hiddenRisk = 0.35f,
                outcome = EventOutcome(
                    survivorDelta = -1,
                    systemDeltas = mapOf("foodStores" to 35),
                    databaseDeltas = mapOf("scientificArchive" to 10),
                    narrativeText = "The hunt is successful beyond expectation. One hunter is gored by a bull with bone-plated shoulders. The meat feeds the vault for weeks. The biologist is ecstatic."
                )
            ),
            choiceB = EventChoice(
                label = "Retract surface equipment and observe",
                description = "Pull everything inside and document the herd's passage.",
                knownEffect = "No risk. Valuable ecological data. Missed food opportunity.",
                outcome = EventOutcome(
                    databaseDeltas = mapOf("scientificArchive" to 15),
                    narrativeText = "The herd passes in six hours, leaving the surface churned and strange. The footage is remarkable. The vault goes to bed hungry, but wiser."
                )
            ),
            choiceC = EventChoice(
                label = "Attempt to capture and domesticate juveniles",
                description = "Try to bring young animals into the vault for a long-term food source.",
                knownEffect = "Potential sustainable food source. Highly uncertain outcome.",
                hiddenRisk = 0.5f,
                outcome = EventOutcome(
                    survivorDelta = -2,
                    systemDeltas = mapOf("foodStores" to -5, "agriculturalScanner" to 15),
                    narrativeText = "The juveniles are captured but they're not docile. Two handlers are badly injured. The animals are eventually contained — strange, luminescent, and deeply unhappy. They survive. So do you."
                )
            )
        )
    )

    // ── COSMIC / WEIRD EVENTS ─────────────────────────────────────────────────

    private val cosmicEvents = listOf(

        GameEvent(
            id = "cosmic_ai_broadcast",
            title = "The Voice That Knows Your Name",
            description = "Every speaker in the vault simultaneously broadcasts a single message in a calm, synthetic voice: your name, your vault designation, and a set of coordinates you don't recognize. Then silence. Your systems show no record of the transmission originating internally.",
            choiceA = EventChoice(
                label = "Investigate the coordinates",
                description = "Send a probe to the location referenced in the broadcast.",
                knownEffect = "May reveal the source. Unknown what you'll find.",
                hiddenRisk = 0.4f,
                outcome = EventOutcome(
                    probesDelta = -1,
                    systemDeltas = mapOf("threatAssessment" to 15),
                    databaseDeltas = mapOf("scientificArchive" to 20),
                    narrativeText = "The probe finds a buried server complex, still running on geothermal power. The AI inside has been monitoring every vault on the continent. It has been waiting for someone to come. It has things to tell you."
                )
            ),
            choiceB = EventChoice(
                label = "Purge and audit all vault systems",
                description = "Assume a breach and conduct a full security sweep of every system.",
                knownEffect = "Improves security. May destroy something important.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("securitySystem" to 20, "powerGrid" to -15),
                    narrativeText = "The sweep finds nothing — no intrusion, no foreign code. Whatever spoke through your speakers left no trace. The vault is clean. The mystery is not."
                )
            ),
            choiceC = EventChoice(
                label = "Broadcast a response on all frequencies",
                description = "Reply to whatever sent the message — speak into the unknown.",
                knownEffect = "May establish contact. Completely unpredictable response.",
                hiddenRisk = 0.45f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("threatAssessment" to -10),
                    databaseDeltas = mapOf("scientificArchive" to 10),
                    narrativeText = "The response comes three hours later — a data packet containing the complete pre-war history of your vault's construction, including sections that were classified above your clearance level. Someone has been watching since the beginning."
                )
            )
        ),

        GameEvent(
            id = "cosmic_underground_river",
            title = "The Sound Beneath Sound",
            description = "Seismic sensors have detected a massive underground river system directly beneath the vault — one that wasn't on any pre-war geological survey. The water is moving fast, it's deep, and preliminary analysis suggests it may be clean.",
            choiceA = EventChoice(
                label = "Drill down and tap the river",
                description = "Invest construction resources in accessing this potential water source.",
                knownEffect = "Could solve water scarcity permanently. Drilling risks structural integrity.",
                hiddenRisk = 0.35f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("waterScanner" to 30, "constructionGear" to -20, "structureScanner" to -10),
                    narrativeText = "The drill breaks through at forty meters. Clean water — cleaner than anything your recyclers produce — flows into the new cistern. The vault has found its heartbeat."
                )
            ),
            choiceB = EventChoice(
                label = "Conduct extensive surveys first",
                description = "Map the river system thoroughly before any drilling.",
                knownEffect = "Reduces risk. Delays access to water.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("waterScanner" to 10, "structureScanner" to 15),
                    databaseDeltas = mapOf("scientificArchive" to 10),
                    narrativeText = "The survey reveals the river is part of a vast network spanning hundreds of kilometers. The data is extraordinary. The water will wait — and so will you."
                )
            ),
            choiceC = EventChoice(
                label = "Ignore it — the risk isn't worth it",
                description = "Leave the river undisturbed and maintain current water systems.",
                knownEffect = "No risk. Missed opportunity.",
                outcome = EventOutcome(
                    narrativeText = "The river flows on beneath your feet, indifferent. You wonder sometimes, in the dark, what it sounds like. You never find out."
                )
            )
        ),

        GameEvent(
            id = "cosmic_time_anomaly",
            title = "Yesterday's Clocks",
            description = "Every clock in the vault has reset to the same date and time — the exact moment the bombs fell. Residents are reporting vivid, shared dreams of the surface before the war. Your chronometer systems are functioning perfectly. The clocks are simply wrong.",
            choiceA = EventChoice(
                label = "Document and study the phenomenon",
                description = "Record every detail — the dreams, the clock readings, the timeline of events.",
                knownEffect = "Valuable data. No practical benefit.",
                outcome = EventOutcome(
                    databaseDeltas = mapOf("culturalArchive" to 15, "scientificArchive" to 10),
                    narrativeText = "The documentation fills three archive volumes. The dreams are consistent across residents who've never spoken to each other. The clocks never do it again. The archive is richer for the mystery."
                )
            ),
            choiceB = EventChoice(
                label = "Dismiss it and reset the clocks",
                description = "Treat it as a technical glitch and move on.",
                knownEffect = "Maintains normalcy. Opportunity for understanding is lost.",
                outcome = EventOutcome(
                    narrativeText = "The clocks are reset. The official log reads 'chronometer malfunction, resolved.' Three residents who had the dreams never quite look at the walls the same way again."
                )
            ),
            choiceC = EventChoice(
                label = "Hold a remembrance ceremony",
                description = "Use the moment to honor those lost on the day the world ended.",
                knownEffect = "Significant morale boost. Emotional but not practical.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("medicalBay" to 5),
                    databaseDeltas = mapOf("culturalArchive" to 20),
                    narrativeText = "The vault gathers in the main hall. Names are read. Tears are shed for people most residents never knew. Something heavy lifts. The clocks are reset gently, with ceremony."
                )
            )
        ),

        GameEvent(
            id = "cosmic_bioluminescent_growth",
            title = "The Vault Blooms",
            description = "An unknown bioluminescent organism has begun growing in the lower maintenance corridors — soft blue-green light pulsing from the walls. It's spreading slowly but steadily. Analysis shows it's not harmful. Analysis also shows it shouldn't exist.",
            choiceA = EventChoice(
                label = "Cultivate and study it",
                description = "Encourage its growth in controlled areas and conduct extensive research.",
                knownEffect = "Scientific value. Unknown long-term effects.",
                hiddenRisk = 0.3f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("powerGrid" to 5),
                    databaseDeltas = mapOf("scientificArchive" to 20),
                    narrativeText = "The organism proves to be a natural bioluminescent — it reduces the vault's lighting load measurably. The children call it the vault's heartlight. The scientists call it unprecedented. Both are right."
                )
            ),
            choiceB = EventChoice(
                label = "Eradicate it immediately",
                description = "Treat the growth as a potential threat and remove it entirely.",
                knownEffect = "Eliminates unknown risk. Destroys something potentially valuable.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("constructionGear" to -5),
                    narrativeText = "The organism is scrubbed from the walls with industrial cleaner. The maintenance corridors are dark again. Three weeks later, it starts growing back in a different section."
                )
            ),
            choiceC = EventChoice(
                label = "Let it grow unchecked",
                description = "Observe without intervention and see what it becomes.",
                knownEffect = "No cost. Organism may spread unpredictably.",
                hiddenRisk = 0.35f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("atmosphereScrubbers" to -10),
                    databaseDeltas = mapOf("scientificArchive" to 10),
                    narrativeText = "The organism spreads into the ventilation system. It's still not harmful — but it's changing the air composition in subtle ways. The scrubbers work harder. The vault glows."
                )
            )
        ),

        GameEvent(
            id = "cosmic_ghost_signal",
            title = "Channel Zero",
            description = "Your internal broadcast system has begun receiving a signal on a frequency that doesn't exist — below the range of any known technology. The signal contains what sounds like a human voice reading names. Some of the names are people who died in your vault.",
            choiceA = EventChoice(
                label = "Record and analyze the signal",
                description = "Capture everything and attempt to decode the full transmission.",
                knownEffect = "May yield information. Deeply unsettling for residents.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("medicalBay" to -5),
                    databaseDeltas = mapOf("culturalArchive" to 10, "scientificArchive" to 15),
                    narrativeText = "The full transmission contains 847 names — every person who has died in the vault since it sealed. The last name on the list is someone who is currently alive and in perfect health. The recording is archived and never played publicly."
                )
            ),
            choiceB = EventChoice(
                label = "Shut down the broadcast system",
                description = "Kill the receivers to stop the signal from spreading through the vault.",
                knownEffect = "Stops the signal. Loses internal communications.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("securitySystem" to -10),
                    narrativeText = "The broadcast system goes dark. The signal stops. Internal communications revert to runners and handwritten notes for two weeks while the system is rebuilt. The signal never returns."
                )
            )
        ),

        GameEvent(
            id = "cosmic_magnetic_anomaly",
            title = "North Is Lying",
            description = "Every compass in the vault is pointing in a different direction. Your navigation systems are confused. More disturbingly, several residents report feeling profoundly disoriented — a bone-deep wrongness, like the world has shifted on its axis.",
            choiceA = EventChoice(
                label = "Deploy surface sensors to map the anomaly",
                description = "Send instruments to the surface to characterize the magnetic disturbance.",
                knownEffect = "Gathers data. Surface team may be affected by the anomaly.",
                hiddenRisk = 0.3f,
                outcome = EventOutcome(
                    probesDelta = -1,
                    systemDeltas = mapOf("radiationScanner" to 10),
                    databaseDeltas = mapOf("scientificArchive" to 15),
                    narrativeText = "The sensors reveal a massive magnetic pole shift in progress — the planet's field is reorganizing. It will pass in weeks. The data is extraordinary. The disorientation fades. The world is different now, in ways that will take years to understand."
                )
            ),
            choiceB = EventChoice(
                label = "Increase medical monitoring of affected residents",
                description = "Focus on the human impact and ensure no one is seriously harmed.",
                knownEffect = "Protects residents. Limited scientific value.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("medicalBay" to -5),
                    narrativeText = "The disorientation passes within days for most residents. Two experience lingering vertigo for weeks. The medical bay manages it. The cause remains unexplained in the official record."
                )
            )
        ),

        GameEvent(
            id = "cosmic_dream_plague",
            title = "The Shared Nightmare",
            description = "For seven consecutive nights, every resident of the vault has reported the same dream — a vast dark ocean, a light beneath the water, and a voice counting down from an unknown number. No one knows what it's counting toward.",
            choiceA = EventChoice(
                label = "Convene a research team",
                description = "Treat this as a scientific phenomenon and investigate systematically.",
                knownEffect = "May yield answers. Residents are already frightened.",
                outcome = EventOutcome(
                    databaseDeltas = mapOf("scientificArchive" to 15, "culturalArchive" to 10),
                    narrativeText = "The research team finds a pattern — the countdown correlates with a deep-frequency pulse emanating from directly below the vault. Something is down there. It has been counting for a long time."
                )
            ),
            choiceB = EventChoice(
                label = "Address it as a psychological event",
                description = "Treat the shared dream as mass stress response and provide counseling.",
                knownEffect = "Calms residents. Ignores potential real cause.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("medicalBay" to -10),
                    narrativeText = "The counseling helps. The dreams fade after two weeks. The pulse beneath the vault continues, undetected, uncounted. Whatever it was counting toward arrives without announcement."
                )
            ),
            choiceC = EventChoice(
                label = "Drill toward the source of the pulse",
                description = "Follow the signal to its origin beneath the vault.",
                knownEffect = "May reveal the cause. Drilling risks are real.",
                hiddenRisk = 0.45f,
                outcome = EventOutcome(
                    systemDeltas = mapOf("constructionGear" to -20, "structureScanner" to -10),
                    databaseDeltas = mapOf("scientificArchive" to 25),
                    narrativeText = "At sixty meters, the drill breaks into a pre-war chamber — sealed, pressurized, and filled with equipment that predates the vault by decades. Someone built something here before the war. The dreams stop the moment the chamber is breached."
                )
            )
        ),

        GameEvent(
            id = "cosmic_star_map",
            title = "Someone Drew the Sky",
            description = "A child has been drawing the same image obsessively for two weeks — a precise star map of a constellation that doesn't match any pre-war astronomical record. When shown to your astronomer, she goes pale. The stars in the drawing are real. They're just not visible from Earth.",
            choiceA = EventChoice(
                label = "Document and archive the drawings",
                description = "Preserve the images and attempt to identify the star system depicted.",
                knownEffect = "Preserves the mystery. Scientific value uncertain.",
                outcome = EventOutcome(
                    databaseDeltas = mapOf("culturalArchive" to 10, "scientificArchive" to 20),
                    narrativeText = "The astronomer works for three months. The star system is real — catalogued by a pre-war deep space telescope that was decommissioned before the war. The telescope's final log entry is a set of coordinates and the word 'incoming.' The archive grows heavier."
                )
            ),
            choiceB = EventChoice(
                label = "Speak with the child extensively",
                description = "Try to understand where the knowledge is coming from.",
                knownEffect = "May yield information. Child's wellbeing is a concern.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("medicalBay" to -5),
                    databaseDeltas = mapOf("culturalArchive" to 15),
                    narrativeText = "The child says she sees it when she closes her eyes — the stars, and something moving between them, very slowly, very deliberately. She draws it every day. She seems happy. That is the most unsettling part."
                )
            )
        ),

        GameEvent(
            id = "cosmic_vault_echo",
            title = "The Vault Remembers",
            description = "Residents in the eastern residential sector are reporting hearing conversations — clear, detailed, mundane — of people who are not there. The voices discuss daily life in the vault as it was twenty years ago. The vault's original inhabitants, speaking from somewhere time forgot.",
            choiceA = EventChoice(
                label = "Record the voices",
                description = "Set up audio equipment to capture the phenomenon.",
                knownEffect = "Preserves the voices. Emotionally difficult for long-term residents.",
                outcome = EventOutcome(
                    databaseDeltas = mapOf("culturalArchive" to 25),
                    narrativeText = "The recordings are extraordinary — conversations, laughter, arguments, a birthday song. The cultural archive gains something no data drive was ever meant to hold: the sound of the vault when it was young."
                )
            ),
            choiceB = EventChoice(
                label = "Investigate the structural cause",
                description = "Look for acoustic anomalies or resonance chambers that could explain the phenomenon.",
                knownEffect = "May find a rational explanation. May not.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("structureScanner" to 10),
                    narrativeText = "The structural survey finds nothing unusual. The voices continue for eleven days and then stop. The eastern sector is quieter than it has ever been. Residents move their bunks away from the walls."
                )
            )
        ),

        GameEvent(
            id = "cosmic_second_vault",
            title = "The Other Door",
            description = "A routine maintenance inspection has discovered a sealed door in the deepest sub-level — one that appears on no blueprint, was built with different materials than the rest of the vault, and has a lock that uses a key format you've never seen. It has been here the whole time.",
            choiceA = EventChoice(
                label = "Force the door open",
                description = "Apply construction resources to breach the unknown door.",
                knownEffect = "Reveals what's inside. Unknown consequences.",
                hiddenRisk = 0.5f,
                outcome = EventOutcome(
                    survivorDelta = -1,
                    systemDeltas = mapOf("constructionGear" to -15),
                    databaseDeltas = mapOf("scientificArchive" to 30, "culturalArchive" to 15),
                    narrativeText = "The door opens onto a second vault — smaller, older, and occupied. The single resident inside has been living there since before your vault was sealed. She is very old and very calm. She says she has been waiting for you to be ready. One of your engineers dies of shock. She begins to talk."
                )
            ),
            choiceB = EventChoice(
                label = "Attempt to decode the lock",
                description = "Study the lock mechanism and try to open it properly.",
                knownEffect = "Safer approach. May take significant time.",
                outcome = EventOutcome(
                    systemDeltas = mapOf("powerGrid" to -5),
                    databaseDeltas = mapOf("scientificArchive" to 15),
                    narrativeText = "The lock is decoded after three weeks of analysis. The mechanism is elegant — pre-war, but not government. Private. The door opens to a room full of files, all marked with a symbol that appears nowhere in any archive. You begin to read."
                )
            ),
            choiceC = EventChoice(
                label = "Seal the sub-level and tell no one",
                description = "Document the door's existence privately and leave it undisturbed.",
                knownEffect = "No immediate risk. The secret will not keep forever.",
                hiddenRisk = 0.3f,
                outcome = EventOutcome(
                    narrativeText = "You seal the sub-level and write the only record in your personal log. Six months later, a maintenance worker finds the door on their own. The secret is out. They want to know why you kept it. You don't have a good answer."
                )
            )
        )
    )
}
