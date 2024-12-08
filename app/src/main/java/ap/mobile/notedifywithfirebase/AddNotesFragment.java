package ap.mobile.notedifywithfirebase;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ap.mobile.notedifywithfirebase.database.Note;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AddNotesFragment extends Fragment {
    private EditText titleEditText;
    private EditText noteContentEditText;
    private TextView lastEditedText;
    private SimpleDateFormat timeFormat;
    private DatabaseReference databaseReference;
    private String noteId, noteTitle, noteContent, noteCategory;
    private ImageButton backButton, boldButton, italicButton, uploadButton;
    private ImageView ivCapturedImage;
    private ImageButton ibShared;
    private long lastEditedTimestamp;
    private String imageUrl, getImageUrl;
    private boolean isCaptured = false;

    static AddNotesFragment newInstance(String noteId, String title, String content, String category, String imageUrl) {
        AddNotesFragment fragment = new AddNotesFragment();
        Bundle args = new Bundle();
        args.putString("NOTE_ID", noteId);
        args.putString("NOTE_TITLE", title);
        args.putString("NOTE_CONTENT", content);
        args.putString("NOTE_CATEGORY", category);
        args.putString("imageUrl", imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_notes, container, false);

        timeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        databaseReference = FirebaseDatabase.getInstance("https://notedifycreatenotes-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("notes");

        initializeViews(view);
        setupClickListeners();

        // Check if it's edit mode
        if (getArguments() != null) {
            noteId = getArguments().getString("NOTE_ID");
            noteTitle = getArguments().getString("NOTE_TITLE");
            noteContent = getArguments().getString("NOTE_CONTENT");
            noteCategory = getArguments().getString("NOTE_CATEGORY");
            if (noteId != null) {
                String title = getArguments().getString("NOTE_TITLE");
                String content = getArguments().getString("NOTE_CONTENT");

                getImageUrl = getArguments().getString("imageUrl");

                if(getImageUrl != null) {
                    imageUrl = getImageUrl;
                    isCaptured = true;
                    Toast.makeText(getContext(), getImageUrl, Toast.LENGTH_SHORT).show();
                }

                titleEditText.setText(title);
                noteContentEditText.setText(content);
                updateLastEditedTime();
            } else {
                updateLastEditedTime();
            }
        }

        if (isCaptured) {
            Toast.makeText(getContext(), "Captured " + imageUrl, Toast.LENGTH_SHORT).show();
            Glide.with(this).load(imageUrl).into(ivCapturedImage);
            ivCapturedImage.setVisibility(View.VISIBLE);
        } else {
            ivCapturedImage.setVisibility(View.GONE);
        }

        return view;
    }

    private void initializeViews(View view) {
        titleEditText = view.findViewById(R.id.titleEditText);
        noteContentEditText = view.findViewById(R.id.noteContentEditText);
        lastEditedText = view.findViewById(R.id.lastEditedText);
        backButton = view.findViewById(R.id.backButton);
        ibShared = view.findViewById(R.id.ibShared);
        boldButton = view.findViewById(R.id.boldButton);
        italicButton = view.findViewById(R.id.italicButton);
        uploadButton = view.findViewById(R.id.uploadImageButton);
        ivCapturedImage = view.findViewById(R.id.ivCapturedImage);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> saveNoteAndNavigateBack());
        boldButton.setOnClickListener(v -> applyTextStyle("bold"));
        italicButton.setOnClickListener(v -> applyTextStyle("italic"));

        ibShared.setOnClickListener(v -> {
            if (getActivity() instanceof AddNotes) {
                ((AddNotes) getActivity()).switchToSharedFragment();
            }
        });

        uploadButton.setOnClickListener(v -> {
            Intent intent = new Intent (getActivity(), CameraActivity.class);
            intent.putExtra("NOTE_ID", noteId);
            intent.putExtra("NOTE_TITLE", noteTitle);
            intent.putExtra("NOTE_CONTENT", noteContent);
            intent.putExtra("NOTE_CATEGORY", noteCategory);
            startActivity(intent);
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateLastEditedTime();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        titleEditText.addTextChangedListener(textWatcher);
        noteContentEditText.addTextChangedListener(textWatcher);
    }


    private void updateLastEditedTime() {
        lastEditedTimestamp = System.currentTimeMillis();

        String formattedTime = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(new Date(lastEditedTimestamp));
        lastEditedText.setText("Last edited on " + formattedTime);
    }

    public void saveNoteAndNavigateBack() {
        String title = titleEditText.getText().toString().trim();
        String content = noteContentEditText.getText().toString().trim();

        if (!title.isEmpty() || !content.isEmpty()) {
            if (noteId != null) {
                databaseReference.child(noteId).child("title").setValue(title);
                databaseReference.child(noteId).child("content").setValue(content);
                databaseReference.child(noteId).child("timestamp").setValue(lastEditedTimestamp);
                databaseReference.child(noteId).child("category").setValue(getArguments().getString("NOTE_CATEGORY"));
                if (getImageUrl != null) {
                    databaseReference.child(noteId).child("imageUrl").setValue(imageUrl);
                }
                Toast.makeText(requireContext(), "Note updated", Toast.LENGTH_SHORT).show();
            } else {
                Note note = new Note(title, content,
                        getArguments() != null ? getArguments().getString("NOTE_CATEGORY") : null,
                        false, new ArrayList<>());

                note.setTimestamp(lastEditedTimestamp);


                String id = databaseReference.push().getKey();
                note.setId(id);

                databaseReference.child(id).setValue(note).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireContext(), "Note saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Failed to save note", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        Intent intent = new Intent(requireContext(), MainActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }


    private void applyTextStyle(String style) {
        EditText focusedEditText = titleEditText.hasFocus() ? titleEditText : noteContentEditText;
        int start = focusedEditText.getSelectionStart();
        int end = focusedEditText.getSelectionEnd();

        if (start >= 0 && end > start) {
            Spannable str = focusedEditText.getText();
            if ("bold".equals(style)) {
                str.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if ("italic".equals(style)) {
                str.setSpan(new StyleSpan(Typeface.ITALIC), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }
}