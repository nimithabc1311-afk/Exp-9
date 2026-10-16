# Experiment 9: Demonstrate Data Persistence Using SharedPreferences and SQLite

|---|---|
| **Name** | Nimitha BC |
| **USN** | 25MCAR0132 |
| **Course** | MCA |
|**Experiment**| 7 |

---

## 1. About the Experiment

Android apps lose everything held in memory when they are closed. **Data persistence** means saving data to the device's storage so that it is still available the next time the app is opened.

This experiment builds a single Android app that demonstrates the two most common local persistence options:

| Mechanism | Used for | Used in this app for |
|-----------|----------|----------------------|
| **SharedPreferences** | Small key-value data such as settings or simple profile info | Student profile (name, USN) and an "app opened N times" counter |
| **SQLite** | Structured data stored in tables and queried with SQL | A notes list with full CRUD (Create, Read, Update, Delete) |

---

## 2. Concept / Technology Behind It

### SharedPreferences
- Stores data as key-value pairs in a small XML file that is private to the app.
- `getSharedPreferences(name, MODE_PRIVATE)` opens the file.
- `edit().putString(key, value).apply()` writes data (asynchronously).
- `getString(key, default)` and `getInt(key, default)` read data back.
- Best for small amounts of simple data such as user settings.

### SQLite
- A lightweight relational database built into Android.
- `SQLiteOpenHelper` creates the database on first use (`onCreate`) and handles schema changes (`onUpgrade`).
- `ContentValues` with `insert()` and `update()` write data, `rawQuery()` with a `Cursor` reads data, and `delete()` removes data.
- Best for larger, structured data that needs to be added, edited, deleted and listed.

Both are stored in the app's private storage, so the data **survives closing the app and restarting the device**.

---

## 3. Scenario Used to Demonstrate It

A **Student Profile & Notes Manager**:

1. The student enters their **name and USN** and taps **Save Profile**. This is stored using **SharedPreferences**. The app also keeps count of how many times it has been opened.
2. The student writes **notes**, which are stored as rows in a **SQLite** table.
   - Tap a note to **edit** it.
   - Long-press a note to **delete** it.
   - Use **Clear All** to remove every note.
3. When the app is closed completely and opened again, the profile, launch counter and notes are all still there.

---

## 4. Project Folder and File Structure

```
Experiment9/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           └── java/com/example/experiment9/
│               ├── MainActivity.kt
│               └── DatabaseHelper.kt
├── screenshots/
│   ├── output.png
│   ├── test1_profile.png
│   ├── test2_notes.png
│   └── test3_persistence.png
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

| File | Purpose |
|------|---------|
| `MainActivity.kt` | Builds the screen in code (no XML layout needed), saves and loads the profile with SharedPreferences, and calls the database methods. |
| `DatabaseHelper.kt` | Contains the `Note` data class and the `SQLiteOpenHelper` that creates the `notes` table (`id`, `text`) and provides insert, read, update and delete methods. |
| `AndroidManifest.xml` | Declares `MainActivity` as the launcher activity. |
| `build.gradle.kts` | Gradle configuration and dependencies. |
| `screenshots/` | Output and test case screenshots shown in this README. |

---

## 5. Output

<!-- Replace output.png in the screenshots folder with your own screenshot -->

![Output Screenshot](screenshots/output.png)

<br><br><br><br>

---

## 6. Test Cases

### Test Case 1: Save and display the profile (SharedPreferences)

| | |
|---|---|
| **Input** | Name: `Nimitha BC`, USN: `25MCAR0132`, then tap **Save Profile** |
| **Expected result** | Toast "Profile saved" appears and the line *Saved profile -> Name: Nimitha BC \| USN: 25MCAR0132* is shown |
| **Status** | Pass |

**Screenshot:**

![Test Case 1](screenshots/test1_profile.png)

<br><br><br><br>

---

### Test Case 2: Add, edit and delete notes (SQLite)

| | |
|---|---|
| **Input** | Type a note and tap **Add Note**; tap a note, change the text and tap **Update Note**; long-press a note to delete it |
| **Expected result** | The list refreshes after every action and each note shows its database id |
| **Status** | Pass |

**Screenshot:**

![Test Case 2](screenshots/test2_notes.png)

<br><br><br><br>

---

### Test Case 3: Data persists after closing and reopening the app

| | |
|---|---|
| **Input** | Swipe the app away from recent apps, then open it again |
| **Expected result** | Name, USN and notes are restored automatically and the "App opened" counter has increased |
| **Status** | Pass |

**Screenshot:**

![Test Case 3](screenshots/test3_persistence.png)

<br><br><br><br>

---

## 8. Conclusion

The experiment shows that SharedPreferences is suited to small key-value data such as a profile, while SQLite is suited to structured data that needs full CRUD operations. Both keep data on the device after the app is closed.
