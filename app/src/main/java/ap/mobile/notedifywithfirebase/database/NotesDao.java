package ap.mobile.notedifywithfirebase.database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotesDao {
    private final DatabaseReference databaseReference;

    public NotesDao() {
        // Mengacu ke node "notes" di Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance("https://notedifycreatenotes-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("notes");
    }

    // Insert data
    public void insertNote(Note note) {
        String id = databaseReference.push().getKey();
        if (id != null) {
            note.setId(id);
            databaseReference.child(id).setValue(note)
                    .addOnSuccessListener(aVoid -> {
                        // Berhasil menambahkan catatan
                        System.out.println("Note added successfully.");
                    })
                    .addOnFailureListener(e -> {
                        // Gagal menambahkan catatan
                        System.err.println("Failed to add note: " + e.getMessage());
                    });
        }
    }

    // Get all notes
    public void getAllNotes(ValueEventListener listener) {
        databaseReference.addValueEventListener(listener);
    }

    // Delete data
    public void deleteNote(String id) {
        databaseReference.child(id).removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Berhasil menghapus catatan
                    System.out.println("Note deleted successfully.");
                })
                .addOnFailureListener(e -> {
                    // Gagal menghapus catatan
                    System.err.println("Failed to delete note: " + e.getMessage());
                });
    }
}
