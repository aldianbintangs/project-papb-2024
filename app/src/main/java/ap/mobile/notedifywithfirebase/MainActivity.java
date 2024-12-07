package ap.mobile.notedifywithfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ap.mobile.notedifywithfirebase.database.Note;
import ap.mobile.notedifywithfirebase.homepage.NoteAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private List<Note> noteList;
    private DatabaseReference notesRef;
    private TextView userNoteCount;
    private FloatingActionButton fab;
    private TextView dailyQuote, dailyQuoteAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        userNoteCount = findViewById(R.id.userNoteCount);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        noteList = new ArrayList<>();
        adapter =  new NoteAdapter(this, noteList);
        recyclerView.setAdapter(adapter);

        notesRef = FirebaseDatabase.getInstance("https://notedifycreatenotes-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("notes");

        fab = findViewById(R.id.fab); // Inisialisasi FloatingActionButton

        // Menambahkan onClickListener untuk FloatingActionButton
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Anda dapat menambahkan aksi di sini, seperti membuka halaman baru
                // Sebagai contoh, membuka activity untuk menambah note baru
                Intent intent = new Intent(MainActivity.this, AddNotes.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "FAB Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        dailyQuote = findViewById(R.id.dailyQuote);
        dailyQuoteAuthor = findViewById(R.id.dailyQuoteAuthor);

        fetchDailyQuote();
        loadNotes();
    }

    private void fetchDailyQuote() {
        String url = "https://zenquotes.io/api/today";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject quoteObject = response.getJSONObject(0);
                            String quote = quoteObject.getString("q");
                            String author = quoteObject.getString("a");

                            dailyQuote.setText("\"" + quote + "\"");
                            dailyQuoteAuthor.setText("- " + author);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dailyQuote.setText("Failed to load quote.");
                    }
                }
        );
        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    private void loadNotes() {
        notesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                noteList.clear();
                for (DataSnapshot noteSnapshot : snapshot.getChildren()) {
                    Note note = noteSnapshot.getValue(Note.class);
                    if (note != null) {
                        note.setId(noteSnapshot.getKey()); // Set ID dari Firebase
                        noteList.add(note);
                    }
                }
                adapter.notifyDataSetChanged();
                userNoteCount.setText(noteList.size() + " notes");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load notes: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
