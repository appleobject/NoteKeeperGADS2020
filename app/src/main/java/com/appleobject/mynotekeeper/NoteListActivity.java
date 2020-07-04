package com.appleobject.mynotekeeper;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;


import java.util.List;
//Our app starting point with the intent-filter. Launcher
public class NoteListActivity extends AppCompatActivity {
    private NoteRecyclerAdapter mNoteRecyclerAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NoteListActivity.this, NoteActivity.class));

            }
        });

        initializeDisplayContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNoteRecyclerAdapter.notifyDataSetChanged();
    }

    // NoteInfo class and CourseInfo are the data class.
    // While DataManager is the singleton class with the static method - getInstance().
    // The getInstance() - check if the instance is null
    // before calling on initializeCourses() & initializeExampleNotes() which are methods inside the singleton
    // the singleton class is use to call the method that returns the list of note
    // and all other methods on the note, these methods are a member function of the DataManager
    // getCourses. So again, I'm using the DataManager singleton, getting the instance back,
    // and then getting the list of notes back from it.
    private void initializeDisplayContent() {

        RecyclerView recyclerView = findViewById(R.id.list_notes);
        LinearLayoutManager notesLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(notesLayoutManager);

        List<NoteInfo> notes = DataManager.getInstance().getNotes();
        mNoteRecyclerAdapter = new NoteRecyclerAdapter(this,notes);
        recyclerView.setAdapter(mNoteRecyclerAdapter);

    }

}



