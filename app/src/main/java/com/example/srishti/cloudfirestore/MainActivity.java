package com.example.srishti.cloudfirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";
    private static String KEY_TITLE = "title";
    private static String KEY_DESCRIPTION = "description";

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
                String data = "";
                for(QueryDocumentSnapshot documentSnapshot : value){
                    Note note = documentSnapshot.toObject(Note.class);
                    note.setId(documentSnapshot.getId());
                    String priority = String.valueOf(note.getPriority());
                    data += "Priority: " + priority + "\n" +
                            "Id: " + note.getId() + "\n" + "Title: " +  note.getTitle() + "\n" + "Description: " + note.getDescription() + "\n\n";
                }
                dataTV.setText(data);
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
        Task task1 = reference.whereGreaterThan("priority", 2)
                .orderBy("priority")
                .get();
        Task task2 = reference.whereLessThan("priority", 2)
                .orderBy("priority")
                .get();
        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(task1, task2);
        allTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
            @Override
            public void onSuccess(List<QuerySnapshot> querySnapshots) {
                String data = "";
                for(QuerySnapshot querySnapshot : querySnapshots){
                    for(QueryDocumentSnapshot documentSnapshot : querySnapshot){
                        Note note = documentSnapshot.toObject(Note.class);
                        note.setId(documentSnapshot.getId());
                        String priority = String.valueOf(note.getPriority());
                        data += "Priority: " + priority + "\n" +
                                "Id: " + note.getId() + "\n" + "Title: " +  note.getTitle() + "\n" + "Description: " + note.getDescription() + "\n\n";
                    }
                }
                dataTV.setText(data);
            }
        });
    }
}