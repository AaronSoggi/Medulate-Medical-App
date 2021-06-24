package com.example.mobile_interactions_aaron_soggi;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class    notesActivity extends AppCompatActivity {

    FloatingActionButton createNoteBtn;

    // used to display the notes from the fire store
    RecyclerView iRecyclerView;
    StaggeredGridLayoutManager staggeredGridLayoutManager;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    FirestoreRecyclerAdapter<firebaseNoteModel, NoteViewHolder> noteAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        setTitle("Diary");

        Toolbar toolbar = findViewById(R.id.toolbarOfAddNote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        firebaseAuth=FirebaseAuth.getInstance();
        createNoteBtn = findViewById(R.id.createNoteBtn);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        createNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(notesActivity.this,CreateNoteActivity.class));
            }
        });


        Query query = firebaseFirestore.collection("notes").document(firebaseUser.getUid())
                .collection("myNotes").orderBy("title", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<firebaseNoteModel> UsersNotes = new FirestoreRecyclerOptions.Builder<firebaseNoteModel>()
                .setQuery(query, firebaseNoteModel.class).build();

        noteAdapter = new FirestoreRecyclerAdapter<firebaseNoteModel, NoteViewHolder>(UsersNotes) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull firebaseNoteModel firebaseNoteModel) {


                ImageView popUpMenu = noteViewHolder.itemView.findViewById(R.id.menuPopUpBtn);


                noteViewHolder.noteTitle.setText(firebaseNoteModel.getTitle());
                noteViewHolder.noteContent.setText(firebaseNoteModel.getContent());

                String documentID = noteAdapter.getSnapshots().getSnapshot(i).getId();

                noteViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(),detailsOfNote.class);
                        intent.putExtra("title", firebaseNoteModel.getTitle());
                        intent.putExtra("content", firebaseNoteModel.getContent());


                        intent.putExtra("noteId", documentID);


                        view.getContext().startActivity(intent);
                    }
                });

                popUpMenu.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View view) {


                        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                        popupMenu.setGravity(Gravity.END);
                        popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {

                                Intent intent = new Intent(view.getContext(),editNoteActivity.class);

                                intent.putExtra("title", firebaseNoteModel.getTitle());
                                intent.putExtra("content", firebaseNoteModel.getContent());
                                intent.putExtra("noteId", documentID);

                                view.getContext().startActivity(intent);
                                return false;
                            }
                        });

                        popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {


                                DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(documentID);
                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(view.getContext(), "Note has been deleted successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(view.getContext(), "failed to delete note", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                        });
                        popupMenu.show();

                    }
                });
            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout, parent, false);
                return new NoteViewHolder(view);
            }
        };

        iRecyclerView = findViewById(R.id.recyclerView);
        iRecyclerView.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        iRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        iRecyclerView.setAdapter(noteAdapter);

    }

    public class NoteViewHolder extends RecyclerView.ViewHolder
    {
        private TextView noteTitle;
        private TextView noteContent;
        LinearLayout iNote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.noteTitle);
            noteContent=itemView.findViewById(R.id.noteContent);
            iNote = itemView.findViewById(R.id.note);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


        noteAdapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();


        if(noteAdapter != null){
            noteAdapter.stopListening();

        }
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            startActivity(new Intent(notesActivity.this, MainMenu.class) );
            //onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}