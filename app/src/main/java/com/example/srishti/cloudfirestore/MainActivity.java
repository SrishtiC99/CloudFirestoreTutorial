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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";
    private static String KEY_TITLE = "title";
    private static String KEY_DESCRIPTION = "description";

    private EditText titleET;
    private EditText descriptionET;
    private TextView dataTV;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference reference = db.collection("Notebook");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleET = findViewById(R.id.edit_text_title);
        descriptionET = findViewById(R.id.edit_text_description);
        dataTV = findViewById(R.id.text_view_data);

        findViewById(R.id.button_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });
        findViewById(R.id.button_load).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNote();
            }
        });
        findViewById(R.id.button_update_description).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDescription();
            }
        });
        findViewById(R.id.button_delete_description).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDescription();
            }
        });
        findViewById(R.id.button_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        reference.document("Note 1")
                .addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            Toast.makeText(MainActivity.this, "Error while loading!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, error.toString());
                            return;
                        }
                        if(value.exists()){
                            Note note = value.toObject(Note.class);
                            dataTV.setText("Title: " + note.getTitle() + "\n" +
                                    "Description: " + note.getDescription());
                        }else {
                            dataTV.setText("");
                        }
                    }
                });
    }

    public void saveNote(){
        String title = titleET.getText().toString();
        String description = descriptionET.getText().toString();
        Note note = new Note(title, description);
        db.collection("Notebook").document("Note 1").set(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivity.this, "Note Saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @org.jetbrains.annotations.NotNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }

    private void loadNote(){
        reference.document("Note 1").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            Note note = documentSnapshot.toObject(Note.class);
                            dataTV.setText("Title: " + note.getTitle() + "\n" +
                            "Description: " + note.getDescription());

                        }else {
                            Toast.makeText(MainActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }

    private void updateDescription(){
        String description = descriptionET.getText().toString();
        Map<String, Object> note = new HashMap<>();
        note.put(KEY_DESCRIPTION, description);
        // merge
        //reference.document("Note 1").set(note, SetOptions.merge());
        // update
        reference.document("Note 1").update(KEY_DESCRIPTION, description);
    }

    private void deleteDescription(){
        reference.document("Note 1").update(KEY_DESCRIPTION, FieldValue.delete());
        // another method
        /*Map<String, Object> note = new HashMap<>();
        note.put(KEY_DESCRIPTION, FieldValue.delete());
        reference.document("Note 1").update(note);

         */
    }

    private void delete(){
        reference.document("Note 1").delete();
    }
}