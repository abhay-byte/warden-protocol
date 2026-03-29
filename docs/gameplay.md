# Gameplay

## Run Structure

Every run begins with:

- 1,000 survivors
- 3 surface probes
- 0 years since the war
- 100% integrity across core systems, scanners, and archives

The player repeats a scan-and-decide loop until the vault opens or the population collapses.

## Main Decisions

- `Search`: advance time, trigger passive decay, and roll a random event before the next site appears
- `Probe`: consume one probe to reveal anomaly details and generate a deeper field report
- `Open`: commit to the current site and calculate settlement casualties, score, and ending narrative

## Surface Evaluation

Each generated location contains:

- Location archetype and handcrafted name
- Short and long descriptive text
- Radiation level
- Water availability
- Food potential
- Shelter quality
- Resource richness
- Surface hostility
- Optional anomaly
- Travel route, duration, risk summary, attrition band, and score penalty

Scanner health controls how much of that information is visible. When scanner systems degrade badly enough, site telemetry becomes unknown.

## Vault Management

The bunker tracks 12 operational systems:

- Power grid
- Food stores
- Medical bay
- Security system
- Construction gear
- Atmosphere scrubbers
- Radiation scanner
- Water scanner
- Agricultural scanner
- Structure scanner
- Resource scanner
- Threat assessment

The run also tracks 2 archives:

- Cultural archive
- Scientific archive

Searching again applies passive decay to the systems and can directly kill survivors if food, air, medical support, or power have collapsed.

## Events

The event catalog combines:

- Base vault incidents
- Base surface incidents
- Base cosmic incidents
- Expanded vault/surface/cosmic encounter pools
- Apex-threat encounters for harsher late-feeling outcomes

Events present 2 or 3 choices. Each choice can modify:

- Survivors
- Individual vault systems
- Archive integrity
- Probe count
- Outcome narrative

Some options carry hidden risk rolls, so a good-looking choice can still land badly.

A single `Search` can trigger more than one event before the next site appears. The first event is guaranteed, then each additional event becomes 20% less likely than the previous follow-up roll, so long event chains remain possible but get progressively rarer.

## Settlement Resolution

Opening the vault applies immediate casualties using:

- Travel attrition from the selected route
- Radiation severity
- Water scarcity
- Combined food and water failure
- Hostility level
- Shelter weakness under hazardous conditions

The final score then combines:

- Surviving population
- Surface site quality
- Hostility penalty
- Remaining system integrity
- Archive preservation
- Travel score penalty

The game generates a classification, settlement name, narrative epilogue, and a telemetry block for the result screen and saved run archive.
