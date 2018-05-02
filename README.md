# Tap The Map

<p align="center">
  <img src="main_menu.png" align="center" alt="Main menu" />
</p>

## Game settings
All game settings are hardcoded in `.gameplay.Gameplay.Settings`. In future, we may move them to a separate file or download from somewhere.

## Country identifiers
We use country identifiers in multiple places of the app. `Country` is an identifier class from which you can obtain both ISO alpha 2 codes (e.g. `GR` for Greece) and codes given by European Union (e.g. `EL` for Greece). European country codes, received from Eurostat API, are here: http://ec.europa.eu/eurostat/statistics-explained/index.php/Glossary:Country_codes.

Names of all assets (OBJ files, textures etc.) use EU names.

## AR support
Application uses EasyAR to handle augmented reality. All marker images are defined in `assets/targets.json`, images are held in the same folder.
