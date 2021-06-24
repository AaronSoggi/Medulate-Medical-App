package com.example.mobile_interactions_aaron_soggi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class detailsOfNote extends AppCompatActivity {

    private TextView iContentOfEditNote, iTitleOfEditNote;
    FloatingActionButton iEditNoteIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_of_note);

        iContentOfEditNote = findViewById(R.id.contentOfEditNote);
        iTitleOfEditNote = findViewById(R.id.titleOfEditNote);
        iEditNoteIcon = findViewById(R.id.gotoEditNote);

        Toolbar toolbar = findViewById(R.id.toolbar_in_editNote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent data = getIntent();

        iEditNoteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), editNoteActivity.class);

                intent.putExtra("title", data.getStringExtra("title"));
                intent.putExtra("content", data.getStringExtra("content"));
                intent.putExtra("noteId", data.getStringExtra("noteId"));

                view.getContext().startActivity(intent);
            }
        });

        iContentOfEditNote.setText(data.getStringExtra("content"));
        iTitleOfEditNote.setText(data.getStringExtra("title"));
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            startActivity(new Intent(detailsOfNote.this, notesActivity.class) );
        }

        return super.onOptionsItemSelected(item);
    }
}
