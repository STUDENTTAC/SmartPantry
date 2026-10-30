# SmartPantryManager

SmartPantryManager is an Android application developed for my Mobile App Development 700 practical assignment.

The main purpose of the application is to help users manage the food ingredients they already have at home and reduce food waste.

The application allows users to store pantry ingredients, quantities, units and expiry dates. It then compares the available pantry items with stored recipe requirements and suggests recipes that the user can fully prepare.

The application was developed using Java in Android Studio and uses SQLite for local data storage.

---

## Project Information

- Project Name: SmartPantryManager
- Platform: Android
- Programming Language: Java
- Development Environment: Android Studio
- Database: SQLite
- Version Control: Git and GitHub
- Data Storage: SQLite and SharedPreferences

---

## Main Purpose of the Application

The purpose of SmartPantryManager is to help users:

- Keep track of ingredients available at home
- Record ingredient quantities
- Record ingredient units
- Record expiry dates
- Reduce food waste
- Find recipes that can be made using available ingredients
- Use food that is close to expiry
- Automatically update pantry quantities after cooking

---

## Main Features

### Pantry Management

The user can manage pantry ingredients using full CRUD functionality.

CRUD stands for:

- Create – add a new ingredient
- Read – view ingredients stored in the pantry
- Update – edit an existing ingredient
- Delete – remove an ingredient from the pantry

Each pantry ingredient can contain:

- Ingredient name
- Quantity
- Unit
- Expiry date

---

## Suggested Recipes

The Suggested Recipes section uses strict recipe matching.

A recipe is only displayed if the user has every ingredient required for that recipe and has enough quantity of each ingredient.

For example, if a recipe requires:

- 2 slices of bread
- 1 tomato

The recipe will only appear if both bread and tomato are available in sufficient quantities.

If one ingredient is missing or there is not enough quantity, the recipe will not be displayed under Suggested Recipes.

This prevents the application from suggesting recipes that the user cannot actually prepare.

---

## All Recipes

The All Recipes section allows the user to browse the complete recipe library.

This is different from Suggested Recipes.

Suggested Recipes only displays recipes that match the current pantry.

All Recipes displays every recipe stored in the application, including recipes where the user may still be missing some ingredients.

The application contains a collection of seeded recipes that are available when the application is first used.

---

## Recipe Details

When the user selects a recipe, the Recipe Details screen displays information such as:

- Recipe name
- Required ingredients
- Required quantities
- Units
- Ingredient availability
- Cooking method

The screen also contains the Cook This Recipe feature.

---

## Cook & Deduct

Cook & Deduct automatically updates the pantry after the user cooks a recipe.

Before deducting any ingredients, the application checks whether all the required ingredients are still available.

If the recipe can be made successfully:

- Required ingredient quantities are deducted from the pantry
- Ingredients with no remaining quantity can be removed
- Meals Cooked is updated
- Ingredients Rescued can be updated
- Suggested Recipes are recalculated

This helps keep the digital pantry consistent with the ingredients that have actually been used.

---

## Pantry Rescue Mode

Pantry Rescue Mode was added to help reduce food waste.

The feature checks ingredients that are approaching their expiry dates.

Recipes that use ingredients close to expiry are given a Rescue Score.

Recipes with a higher Rescue Score can be given higher priority.

Rescue Mode still uses strict recipe matching, which means the application will not recommend a rescue recipe unless the user has all the required ingredients.

---

## Pantry Health

The Home screen includes a Pantry Health status.

Pantry Health is based on the expiry condition of ingredients stored in the pantry.

Possible statuses include:

- Empty – there are no ingredients in the pantry
- Healthy – pantry items are not currently close to expiry
- Use Soon – one or more ingredients are approaching expiry
- High Risk – one or more ingredients have expired

This gives the user a quick overview of the condition of their pantry.

---

## Expiry Tracking

Each pantry ingredient can have an expiry date.

The application uses the expiry date to identify ingredients that:

- Are still safe to keep
- Should be used soon
- Have expired

Expiry information is also used by Pantry Health and Pantry Rescue Mode.

---

## Settings

The Settings screen allows the user to control application preferences such as expiry alerts.

The settings are stored using SharedPreferences.

This means the selected preference remains saved even after the user closes and reopens the application.

---

## Database

SmartPantryManager uses SQLite for local data storage.

SQLite was selected because it works directly on the Android device and does not require an internet connection or external database server.

The main database tables are:

### Ingredients

Stores the ingredients currently owned by the user.

```text
ingredients
-----------------------------
id              INTEGER PK
name            TEXT
quantity        REAL
unit            TEXT
expiry_date     TEXT
```

### Recipes

Stores the main recipe information.

```text
recipes
-----------------------------
id              INTEGER PK
name            TEXT
method          TEXT
```

### Recipe Ingredients

Stores the ingredients required for each recipe.

```text
recipe_ingredients
-----------------------------
id                  INTEGER PK
recipe_id           INTEGER
ingredient_name     TEXT
quantity            REAL
unit                TEXT
```

The `recipe_id` field connects recipe requirements to the correct recipe.

---

## Technologies Used

The following technologies and Android concepts were used in the project:

- Java
- Android Studio
- SQLite
- RecyclerView
- Custom Adapters
- Activities
- Intents
- XML layouts
- SharedPreferences
- Gradle
- Git
- GitHub

---

## Project Structure

The project follows a standard Android Studio structure.

```text
SmartPantry/
│
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/example/smartpantry/
│   │       │
│   │       ├── res/
│   │       │   ├── layout/
│   │       │   ├── drawable/
│   │       │   ├── mipmap/
│   │       │   └── values/
│   │       │
│   │       └── AndroidManifest.xml
│   │
│   └── build.gradle
│
├── gradle/
├── README.md
├── settings.gradle
└── gradlew
```

The exact Gradle filenames may appear as `.gradle` or `.gradle.kts` depending on the Android Studio project configuration.

---

# How to Run the Project

## Requirements

Before running the application, make sure the following are installed:

- Android Studio
- Android SDK
- Java Development Kit
- Git if cloning the project from GitHub

For this project, Java 21 was used successfully with the Gradle configuration.

---

## Option 1: Open an Existing Copy of the Project

If the project is already downloaded on the computer:

1. Open Android Studio.
2. Select `Open`.
3. Browse to the SmartPantry project folder.
4. Select the main SmartPantry folder.
5. Click `OK`.
6. Allow Android Studio to load the project.
7. Wait for Gradle Sync to complete.
8. Check that Java 21 is selected as the Gradle JDK if Android Studio asks for a JDK.
9. Connect an Android phone or start an Android emulator.
10. Click the green `Run` button.
11. Select the device.
12. Wait for the application to install and open.

---

## Option 2: Clone the Project from GitHub

The project can also be downloaded using Git.

Open PowerShell or Command Prompt and run:

```bash
git clone https://github.com/STUDENTTAC/SmartPantry.git
```

Then move into the project folder:

```bash
cd SmartPantry
```

After cloning:

1. Open Android Studio.
2. Select `Open`.
3. Select the cloned `SmartPantry` folder.
4. Allow Gradle Sync to complete.
5. Make sure the correct Android SDK is installed.
6. Make sure Java 21 is selected if required.
7. Connect an Android device or start an emulator.
8. Click `Run`.

---

## Running the App on a Physical Android Device

A physical Android phone can be used instead of the emulator.

### Enable Developer Options

On the Android phone:

1. Open Settings.
2. Open About Phone.
3. Open Software Information if required.
4. Tap Build Number several times until Developer Options are enabled.

### Enable USB Debugging

1. Open Developer Options.
2. Enable USB Debugging.
3. Connect the phone to the computer using a USB cable.
4. Select File Transfer if the phone asks for a USB mode.
5. Accept the USB debugging permission message on the phone.

The phone should then appear in the device selector in Android Studio.

Select the phone and click Run.

---

## Running the App Using an Emulator

To use an Android emulator:

1. Open Android Studio.
2. Open Device Manager.
3. Create or select an Android Virtual Device.
4. Start the emulator.
5. Wait for Android to load completely.
6. Select the emulator from the device list.
7. Click Run.

A physical device may be easier to use on computers with limited RAM or processing power.

---

## Building an APK

The application can also be built as an APK.

In Android Studio:

1. Open the project.
2. Select `Build`.
3. Select `Generate App Bundles or APKs`.
4. Select `Generate APKs`.
5. Wait for the build to complete.

The generated debug APK is normally located inside:

```text
app/build/outputs/apk/debug/
```

The file is normally called:

```text
app-debug.apk
```

This APK can be copied to an Android phone and installed for testing.

---

## Testing the Application

After opening the application, the following functions can be tested.

### Pantry CRUD Test

1. Open My Pantry.
2. Add a new ingredient.
3. Enter a name, quantity, unit and expiry date.
4. Save the ingredient.
5. Confirm that it appears in the pantry.
6. Edit the ingredient.
7. Change the quantity.
8. Save the changes.
9. Delete the ingredient.
10. Confirm the deletion.

---

## Recipe Matching Test

1. Add ingredients required by one of the recipes.
2. Open Suggested Recipes.
3. Confirm that the recipe appears.
4. Return to the pantry.
5. Delete one required ingredient.
6. Return to Suggested Recipes.
7. Confirm that the recipe no longer appears.

This demonstrates strict recipe matching.

---

## Cook & Deduct Test

1. Add enough ingredients for a recipe.
2. Open Suggested Recipes.
3. Select the recipe.
4. Open Recipe Details.
5. Select Cook This Recipe.
6. Return to the pantry.
7. Confirm that the required quantities were deducted.

---

## Rescue Mode Test

1. Add ingredients with expiry dates close to the current date.
2. Add enough ingredients to make a matching recipe.
3. Open Pantry Rescue Mode.
4. Check the Rescue Score.
5. Confirm that recipes using ingredients close to expiry are given priority.

---

## Settings Test

1. Open Settings.
2. Change the expiry alert option.
3. Leave the Settings screen.
4. Return to Settings.
5. Confirm that the selected preference is still saved.

---

## Version Control

Git was used throughout the development of the project.

The project was committed in different stages instead of uploading the entire finished application in one commit.

Development stages included:

- Initial project setup
- Home screen development
- Pantry database development
- Pantry CRUD functionality
- Recipe database
- Strict recipe matching
- Pantry Rescue Mode
- Settings
- User interface improvements
- Cook & Deduct
- Pantry Health
- All Recipes library
- Final testing and improvements

This provides a development history showing how the project changed over time.

---

## Problems Encountered During Development

### Gradle and Java Compatibility

One problem was caused by an incompatible Java version being used with Gradle.

The project was later configured to use Java 21, which allowed the build to complete successfully.

### Duplicate Android Resources

Another problem occurred when both PNG and WEBP launcher resources existed with the same Android resource names.

The duplicate resources were removed and the project was rebuilt successfully.

### Emulator Performance

The Android emulator was slow on the development computer.

Testing the application on a physical Android phone provided a more reliable testing environment.

---

## What I Learned

During this project, I gained more practical experience with Android application development.

I improved my understanding of:

- Java
- Android Studio
- Activities
- Intents
- XML layouts
- RecyclerView
- Adapters
- SQLite databases
- CRUD operations
- SharedPreferences
- Application logic
- Input validation
- Git
- GitHub
- Debugging Android applications

I also learned how important testing and debugging are when different parts of an Android application are connected together.

---

## Future Improvements

If the application was developed further, possible improvements could include:

- More recipes
- User accounts
- Better accessibility support
- Advanced expiry notifications
- More detailed pantry statistics
- Search and filtering
- Recipe favourites
- Cloud backup
- Optional barcode scanning

---

## Conclusion

SmartPantryManager is designed to make pantry management easier and help reduce food waste.

The application allows users to manage their ingredients using CRUD functionality, receive strict recipe suggestions, monitor expiry dates, use Pantry Rescue Mode and automatically update pantry quantities through Cook & Deduct.

The project also demonstrates the use of Java, Android Studio, SQLite, RecyclerView, SharedPreferences and Git version control in one complete Android application.

---

## Author

Developed as part of the Mobile App Development 700 practical assignment.