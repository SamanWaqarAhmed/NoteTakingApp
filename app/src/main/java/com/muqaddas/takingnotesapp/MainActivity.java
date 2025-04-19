package com.muqaddas.takingnotesapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import androidx.appcompat.app.AlertDialog;


public class MainActivity extends AppCompatActivity {

    private EditText noteInput;
    private Button addNoteButton;
    private RecyclerView notesRecyclerView;

    private List<String> notesList;
    private NotesAdapter adapter;
    private int editingPosition = -1;  // Position of the note being edited

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noteInput = findViewById(R.id.noteInput);
        addNoteButton = findViewById(R.id.addNoteButton);
        notesRecyclerView = findViewById(R.id.notesRecyclerView);

        notesList = new ArrayList<>();
        loadNotes(); // Load saved notes before adapter

        adapter = new NotesAdapter(notesList);
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notesRecyclerView.setAdapter(adapter);

        addNoteButton.setOnClickListener(v -> {
            String inputNote = noteInput.getText().toString().trim();
            if (!inputNote.isEmpty()) {
                if (addNoteButton.getText().toString().equals("Save Note")) {
                    // Editing an existing note
                    notesList.set(editingPosition, inputNote);
                    adapter.notifyItemChanged(editingPosition);
                    addNoteButton.setText("Add Note"); // Reset to "Add Note"
                } else {
                    // Adding a new note
                    notesList.add(inputNote);
                    adapter.notifyItemInserted(notesList.size() - 1);
                }
                noteInput.setText("");  // Clear the input field
                saveNotes();  // Save notes after adding/editing
            }
        });

        adapter.setOnNoteClickListener(position -> {
            String noteToEdit = notesList.get(position);
            noteInput.setText(noteToEdit); // Set the text of the selected note to the input field

            // Change button text to "Save Note" to indicate the user is editing
            addNoteButton.setText("Save Note");

            // Set the editing position
            editingPosition = position;  // Store the position of the note being edited
        });

        adapter.setOnNoteLongClickListener(position -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Delete Note")
                    .setMessage("Are you sure you want to delete this note?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        notesList.remove(position);
                        adapter.notifyItemRemoved(position);
                        saveNotes();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

    }

    // Save notes to SharedPreferences
    private void saveNotes() {
        SharedPreferences prefs = getSharedPreferences("NotesApp", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        StringBuilder sb = new StringBuilder();
        for (String note : notesList) {
            sb.append(note).append(",,"); // using , as delimiter
        }
        editor.putString("notes", sb.toString());
        editor.apply();
    }

    // Load notes from SharedPreferences
    private void loadNotes() {
        SharedPreferences prefs = getSharedPreferences("NotesApp", MODE_PRIVATE);
        String saved = prefs.getString("notes", "");
        if (!saved.isEmpty()) {
            String[] items = saved.split(",,");

            // Add all loaded notes to the list
            notesList.addAll(Arrays.asList(items));
        }
    }
}
