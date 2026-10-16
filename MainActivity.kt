package com.example.experiment9

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Experiment 9: Data persistence using SharedPreferences and SQLite.
 *  - SharedPreferences -> small key/value data (student profile, launch counter)
 *  - SQLite            -> structured data (list of notes with add/edit/delete)
 * The UI is built in code, so no XML layout files are needed.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var db: DatabaseHelper
    private lateinit var adapter: ArrayAdapter<String>

    private lateinit var etName: EditText
    private lateinit var etUsn: EditText
    private lateinit var tvProfile: TextView
    private lateinit var etNote: EditText
    private lateinit var btnAdd: Button

    private var notes: List<Note> = emptyList()
    private var selectedId: Long? = null   // null = adding, otherwise editing this note

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = getSharedPreferences("student_profile", Context.MODE_PRIVATE)
        db = DatabaseHelper(this)

        // Count how many times the app has been opened (stored in SharedPreferences)
        val launches = prefs.getInt("launch_count", 0) + 1
        prefs.edit().putInt("launch_count", launches).apply()

        buildUi()
        loadProfile()
        refreshNotes()
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }

        // Keep content clear of the status bar, nav bar and keyboard
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
            )
            v.setPadding(40 + bars.left, 30 + bars.top, 40 + bars.right, 30 + bars.bottom)
            insets
        }

        root.addView(heading("Experiment 9: Data Persistence", 22f))

        // ---------- Section 1: SharedPreferences ----------
        root.addView(heading("1. SharedPreferences - Student Profile", 16f))

        etName = EditText(this).apply { hint = "Name" }
        etUsn = EditText(this).apply { hint = "USN" }
        val btnSaveProfile = Button(this).apply {
            text = "Save Profile"
            setOnClickListener { saveProfile() }
        }
        tvProfile = TextView(this).apply {
            textSize = 14f
            setPadding(0, 10, 0, 20)
        }
        root.addView(etName)
        root.addView(etUsn)
        root.addView(btnSaveProfile)
        root.addView(tvProfile)

        // ---------- Section 2: SQLite ----------
        root.addView(heading("2. SQLite - Notes (tap = edit, long-press = delete)", 16f))

        etNote = EditText(this).apply { hint = "Write a note" }
        root.addView(etNote)

        btnAdd = Button(this).apply {
            text = "Add Note"
            setOnClickListener { addOrUpdateNote() }
        }
        val btnClear = Button(this).apply {
            text = "Clear All"
            setOnClickListener {
                db.deleteAll()
                resetNoteInput()
                refreshNotes()
                toast("All notes deleted")
            }
        }
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        row.addView(btnAdd, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        row.addView(btnClear, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        root.addView(row)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf<String>())
        val listView = ListView(this).apply {
            this.adapter = this@MainActivity.adapter
            setOnItemClickListener { _, _, position, _ ->
                val note = notes[position]
                selectedId = note.id
                etNote.setText(note.text)
                etNote.setSelection(note.text.length)
                btnAdd.text = "Update Note"
            }
            setOnItemLongClickListener { _, _, position, _ ->
                db.deleteNote(notes[position].id)
                resetNoteInput()
                refreshNotes()
                toast("Note deleted")
                true
            }
        }
        root.addView(listView, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
        ))

        setContentView(root)
    }

    // ---------- SharedPreferences logic ----------
    private fun saveProfile() {
        val name = etName.text.toString().trim()
        val usn = etUsn.text.toString().trim()
        if (name.isEmpty() || usn.isEmpty()) {
            toast("Enter both name and USN")
            return
        }
        prefs.edit().putString("name", name).putString("usn", usn).apply()
        loadProfile()
        toast("Profile saved")
    }

    private fun loadProfile() {
        val name = prefs.getString("name", "") ?: ""
        val usn = prefs.getString("usn", "") ?: ""
        val launches = prefs.getInt("launch_count", 1)
        etName.setText(name)
        etUsn.setText(usn)
        tvProfile.text = if (name.isEmpty()) {
            "No profile saved yet\nApp opened: $launches time(s)"
        } else {
            "Saved profile -> Name: $name | USN: $usn\nApp opened: $launches time(s)"
        }
    }

    // ---------- SQLite logic ----------
    private fun addOrUpdateNote() {
        val text = etNote.text.toString().trim()
        if (text.isEmpty()) {
            toast("Write something first")
            return
        }
        val id = selectedId
        if (id == null) {
            db.insertNote(text)
            toast("Note added")
        } else {
            db.updateNote(id, text)
            toast("Note updated")
        }
        resetNoteInput()
        refreshNotes()
    }

    private fun resetNoteInput() {
        selectedId = null
        etNote.setText("")
        btnAdd.text = "Add Note"
    }

    private fun refreshNotes() {
        notes = db.getAllNotes()
        adapter.clear()
        adapter.addAll(notes.map { "#${it.id}   ${it.text}" })
        adapter.notifyDataSetChanged()
    }

    // ---------- Small helpers ----------
    private fun heading(text: String, size: Float) = TextView(this).apply {
        this.text = text
        textSize = size
        typeface = Typeface.DEFAULT_BOLD
        setPadding(0, 20, 0, 10)
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
