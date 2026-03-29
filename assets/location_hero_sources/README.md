## Location Hero Sources

This folder contains the clean extracted hero images selected from `assets/stitch_main_menu.zip` and normalized to the live `LocationType` names used by the app.

### Mapping Notes

- Each `loc_<category>.png` file is copied into `app/src/main/res/drawable-nodpi/` for direct use by the surface scan UI.
- `loc_cult_territory.png` is currently a fallback copy of the selected military-base variant because the supplied archive did not contain a distinct `cult_territory` image.
