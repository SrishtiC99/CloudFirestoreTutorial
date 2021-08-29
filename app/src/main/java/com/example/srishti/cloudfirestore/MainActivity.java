package com.example.srishti.cloudfirestore;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";

    private EditText titleET;
    private EditText descriptionET;
    private EditText priorityET;
    private TextView dataTV;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference reference = db.collection("Notebook");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleET = findViewById(R.id.edit_text_title);
        descriptionET = findViewById(R.id.edit_text_description);
        priorityET = findViewById(R.id.edit_text_priority);
        dataTV = findViewById(R.id.text_view_data);

        findViewById(R.id.button_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNote();
            }
        });
        findViewById(R.id.button_load).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNotes();
            }
        });
        dataTV.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        reference.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if(error != null){
                    return;
                }
                for(DocumentChange dc : value.getDocumentChanges()){
                    DocumentSnapshot documentSnapshot = dc.getDocument();
                    String id = documentSnapshot.getId();
                    int oldIndex = dc.getOldIndex();
                    int newIndex = dc.getNewIndex();

                    switch (dc.getType()){
                        case ADDED:
                            dataTV.setText("\nAdded: " + id +
                                    "\nOld Index: " + oldIndex + "New Index: " + newIndex);
                            break;
                        case MODIFIED:
                            dataTV.append("\nModified: " + id +
                                    "\nOld Index: " + oldIndex + "New Index: " + newIndex);
                            break;
                        case REMOVED:
                            dataTV.append("\nRemoved: " + id +
                                    "\nOld Index: " + oldIndex + "New Index: " + newIndex);
                            break;

                    }
                }
            }
        });
    }

    public void addNote(){
        String title = titleET.getText().toString();
        String description = descriptionET.getText().toString();
        String priority = priorityET.getText().toString();
        Note note = new Note(title, description, Integer.parseInt(priority));
        reference.add(note)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(MainActivity.this, "Note added", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadNotes(){

    }
}