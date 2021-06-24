package com.example.mobile_interactions_aaron_soggi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
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

public class CreateNoteActivity extends AppCompatActivity {


    EditText iCreateTitleOfNote, iCreateContentOfNote;
    FloatingActionButton iSaveNote;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    ProgressBar iProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        iSaveNote = findViewById(R.id.saveNote);
        iCreateContentOfNote = findViewById(R.id.CreateTitleOfNote);
        iCreateTitleOfNote = findViewById(R.id.CreateContentOfNote);
        iProgressBar = findViewById(R.id.progressBarCreateNote);

        Toolbar toolbar = findViewById(R.id.toolbar_in_CreateNote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth=firebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        iSaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = iCreateTitleOfNote.getText().toString();
                String content = iCreateContentOfNote.getText().toString();
                if(title.isEmpty() || content.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Both fields are required", Toast.LENGTH_SHORT).show();
                }
                else
                    {
                        iProgressBar.setVisibility(view.VISIBLE);

                    DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document();

                    Map<String,Object> note = new HashMap<>();
                    //storing the title and content from the user
                    note.put("title", title);
                    note.put("content", content);

                    //storing the note on the fireStore
                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // if this happens successfully we will then display a message to the user.
                            Toast.makeText(getApplicationContext(), "note created", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(CreateNoteActivity.this, notesActivity.class));

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to create note", Toast.LENGTH_SHORT).show();
                            iProgressBar.setVisibility(view.INVISIBLE);

                        }
                    });


                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            startActivity(new Intent(CreateNoteActivity.this, notesActivity.class) );
            //onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}