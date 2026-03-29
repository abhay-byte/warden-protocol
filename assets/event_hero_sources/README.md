## Event Hero Sources

This folder contains the clean extracted event-art images normalized from `assets/stitch_main_menu(1).zip`.

### Mapping Notes

- The app uses 18 shared event image keys rather than per-event art.
- Each `event_<image_key>.png` file is copied into `app/src/main/res/drawable-nodpi/` for direct use by the event screen.
- `event_vault_catastrophe.png` is currently a documented fallback copy of the selected `vault_power_infrastructure` source because the supplied archive did not include a distinct `vault_catastrophe` image.
