package ap.mobile.notedifywithfirebase.homepage;

import static com.google.android.gms.common.util.CollectionUtils.listOf;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ap.mobile.notedifywithfirebase.AddNotes;
import ap.mobile.notedifywithfirebase.R;
import ap.mobile.notedifywithfirebase.database.Note;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private Context context;
    private List<Note> notes;
    private int[] colors;
    private Random random = new Random();
    private int previousIndex = -1;


    public NoteAdapter(Context context, List<Note> notes) {
        this.context = context;
        this.notes = notes;
        colors = new int[]{
                ContextCompat.getColor(context, R.color.light_yellow),
                ContextCompat.getColor(context, R.color.light_pink),
                ContextCompat.getColor(context, R.color.light_green),
                ContextCompat.getColor(context, R.color.light_blue),
                ContextCompat.getColor(context, R.color.light_orange),
                ContextCompat.getColor(context, R.color.light_red),
                ContextCompat.getColor(context, R.color.light_gray),
                ContextCompat.getColor(context, R.color.light_purple),
                ContextCompat.getColor(context, R.color.light_teal),
                ContextCompat.getColor(context, R.color.light_brown)
        };
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.noteTitle.setText(note.getTitle());
        holder.noteContent.setText(note.getContent());

        if (note.getIsPlaceholder()) {
            holder.deleteButton.setVisibility(View.GONE);
            holder.linearLayout.setVisibility(View.GONE);
        } else {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.linearLayout.setVisibility(View.VISIBLE);
        }

        int randomIndex = getNextRandomIndex(colors.length, previousIndex);
        holder.cvNote.setCardBackgroundColor(colors[randomIndex]);
        previousIndex = randomIndex;

        if (note.getSharedTo() != null && !note.getSharedTo().isEmpty()) {
            holder.linearLayout.setVisibility(View.VISIBLE);
            holder.tvShared.setText("Shared to " + note.getSharedTo().size() + " people");
        } else {
            holder.linearLayout.setVisibility(View.GONE);
        }

        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddNotes.class);
            intent.putExtra("NOTE_ID", note.getId());
            intent.putExtra("NOTE_TITLE", note.getTitle());
            intent.putExtra("NOTE_CONTENT", note.getContent());
            intent.putExtra("NOTE_CATEGORY", note.getCategory());
            intent.putExtra("imageUrl", note.getImageUrl());
            ArrayList<String> sharedToList = note.getSharedTo() != null ? new ArrayList<>(note.getSharedTo()) : new ArrayList<>();
            intent.putStringArrayListExtra("NOTE_SHARED_TO", sharedToList);
            context.startActivity(intent);
        });

        holder.deleteButton.setOnClickListener(v -> {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://notedifycreatenotes-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("notes")
                    .child(note.getId());

            databaseReference.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Note deleted", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "Failed to delete note", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle, noteContent, tvShared;
        ImageButton editButton, deleteButton;
        CardView cvNote;
        LinearLayout linearLayout;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.noteTitle);
            noteContent = itemView.findViewById(R.id.noteContent);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            cvNote = itemView.findViewById(R.id.cvNote);
            linearLayout = itemView.findViewById(R.id.linearShared);
            tvShared = itemView.findViewById(R.id.tvShared);

        }
    }

    private int getNextRandomIndex(int length, int previous) {
        int newIndex;
        do {
            newIndex = random.nextInt(length);
        } while (newIndex == previous);
        return newIndex;
    }
}
