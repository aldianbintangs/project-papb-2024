package ap.mobile.notedifywithfirebase.search;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Random;

import ap.mobile.notedifywithfirebase.AddNotes;
import ap.mobile.notedifywithfirebase.R;
import ap.mobile.notedifywithfirebase.SearchActivity;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.NotesVH> {

    private final Context ctx;
    private List<Notes> dataset;
    private OnItemClickListener listener;
    private OnNoteDeletedListener deleteListener;
    private DatabaseReference notesRef;
    private static final String[] BACKGROUNDCOLORS = {"#FDFAE6", "#FDEBAB"};

    public interface OnNoteDeletedListener {
        void onNoteDeleted();
    }

    public SearchAdapter(Context ctx, List<Notes> dataset, OnItemClickListener listener, OnNoteDeletedListener deleteListener) {
        this.ctx = ctx;
        this.dataset = dataset;
        this.listener = listener;
        this.deleteListener = deleteListener;
        this.notesRef = FirebaseDatabase.getInstance(SearchActivity.FirebaseURL).getReference("notes");
    }

    public class NotesVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView tvTitle, tvContent, tvCategory, tvDate;
        private final ImageButton btDelete;
        private final CardView cardBackground;
        private Notes note;

        public NotesVH(@NonNull View itemView) {
            super(itemView);
            this.tvTitle = itemView.findViewById(R.id.tvTitle);
            this.tvContent = itemView.findViewById(R.id.tvContent);
            this.tvCategory = itemView.findViewById(R.id.tvCategory);
            this.tvDate = itemView.findViewById(R.id.tvDate);
            this.btDelete = itemView.findViewById(R.id.btDelete);
            this.cardBackground = itemView.findViewById(R.id.cvSearchNote);

            this.btDelete.setOnClickListener(this);
            itemView.setOnClickListener(v -> listener.onItemClick(note));
        }

        private int getRandomColor() {
            Random random = new Random();
            String selectedColor = BACKGROUNDCOLORS[random.nextInt(BACKGROUNDCOLORS.length)];
            return Color.parseColor(selectedColor);
        }

        public void bind(Notes note) {
            this.note = note;
            this.tvTitle.setText(note.getTitle());
            this.tvContent.setText(note.getContent());
            this.tvCategory.setText(note.getCategory());
            this.tvDate.setText(note.getDate());

            int randomColor = getRandomColor();
            cardBackground.setBackgroundColor(randomColor);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btDelete) {
                deleteNote();
            }
        }

        private void deleteNote() {
            if (note != null && note.getId() != null) {
                notesRef.child(note.getId()).removeValue()
                        .addOnSuccessListener(unused -> {
                            if (deleteListener != null) {
                                deleteListener.onNoteDeleted();
                            }
                            Toast.makeText(ctx, "Note deleted", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> Toast.makeText(ctx, "Delete failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }
    }

    @NonNull
    @Override
    public NotesVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(this.ctx)
                .inflate(R.layout.item_search_note, parent, false);
        return new NotesVH(rowView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesVH holder, int position) {
        holder.bind(this.dataset.get(position));
    }

    @Override
    public int getItemCount() {
        return this.dataset.size();
    }

    public void setNotes(List<Notes> notes) {
        this.dataset = notes;
    }

    public interface OnItemClickListener {
        void onItemClick(Notes note);
    }
}