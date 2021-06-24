package com.example.mobile_interactions_aaron_soggi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class editNoteActivity extends AppCompatActivity {

    Intent data;
    EditText iEditTitle, iEditContent;
    FloatingActionButton iSubmitNewNote;

    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);



        iEditTitle = findViewById(R.id.editTitleOfNote);
        iEditContent = findViewById(R.id.editContentOfNote);
        iSubmitNewNote = findViewById(R.id.saveEditNote);

        Toolbar toolbar = findViewById(R.id.toolbarOfEditNote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data = getIntent();

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();


        iSubmitNewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newTitle = iEditTitle.getText().toString();
                String newContent = iEditContent.getText().toString();

                if(newTitle.isEmpty()|newContent.isEmpty())
                {

                    Toast.makeText(getApplicationContext(), "Please ensure no fields are left empty", Toast.LENGTH_SHORT).show();
                }
                else{

                    DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(data.getStringExtra("noteId"));
                    Map<String,Object> note = new HashMap<>();

                    note.put("title", newTitle);
                    note.put("content", newContent);
                    // saving the note
                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Note has been updated successfully", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(editNoteActivity.this, notesActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to update note", Toast.LENGTH_SHORT).show();;
                        }
                    });

                }
            }
        });


        String noteTitle = data.getStringExtra("title");
        String noteContent = data.getStringExtra("content");
        iEditContent.setText(noteContent);
        iEditTitle.setText(noteTitle);

    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            startActivity(new Intent(editNoteActivity.this, detailsOfNote.class) );
        }

        return super.onOptionsItemSelected(item);
    }
}