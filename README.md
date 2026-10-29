# Smart Pantry Manager

A Java Android app that helps reduce food waste. The user tracks the ingredients they
already have (their "pantry") and the app suggests recipes they can cook using
**strictly** those ingredients - no recipe is shown unless every ingredient is in the
pantry in at least the required quantity.

## Features
- Pantry management: add, edit and delete ingredients (name, quantity, unit, optional expiry date)
- Pantry list (RecyclerView + custom adapter) with expiry badges
- 20 pre-loaded recipes (seeded on first run)
- **Suggested Recipes** screen powered by the strict-matching rule (`RecipeMatcher.java`)
- Recipe detail screen with ingredients and numbered method steps
- **Pantry Rescue** mode: recipes using ingredients that expire within 3 days come first
- Settings: expiring-soon alerts toggle (SharedPreferences)
- Input validation, empty-state messages, bottom navigation bar

## Database choice: SQLite (SQLiteOpenHelper)
SQLite runs fully on the device, needs no internet or server, persists data between
app launches and is the approach covered in the module. Three tables: `ingredients`,
`recipes` and `recipe_ingredients`.

## Strict matching
`RecipeMatcher.canMake()` returns true only if **every** ingredient is available in the
required quantity. It copes with plural names (tomato / Tomatoes), unit conversion
(g / kg, ml / l / tsp / tbsp / cup) and the same ingredient stored on several rows.
It is covered by unit tests in `app/src/test/.../RecipeMatcherTest.java`.

## Setup / run
1. Install Android Studio (Hedgehog or newer) with Android SDK 34.
2. Open the project folder (File > Open) and let Gradle sync (Gradle 8.9, AGP 8.7.3, JDK 17).
3. Create an emulator (API 24+) or plug in a device, then press Run.
4. Run unit tests: `./gradlew test`

No Google Maps, mapping SDK or location services are used.
