package ap.mobile.notedifywithfirebase;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import ap.mobile.notedifywithfirebase.shared.User;
import ap.mobile.notedifywithfirebase.shared.UserAdapter;

public class SharedToFragment extends Fragment {
    private ImageButton backButton;
    private RecyclerView rvUserContainer;
    private TextView tvSharedTitle;

    private List<String> NOTE_SHARED_TO = new ArrayList<>();
    private UserAdapter userAdapter;

    private DatabaseReference database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shared_to, container, false);

        database = FirebaseDatabase.getInstance("https://notedifycreatenotes-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("notes");

        if (getArguments() != null) {
            NOTE_SHARED_TO = getArguments().getStringArrayList("NOTE_SHARED_TO");
        }

        rvUserContainer = view.findViewById(R.id.rvUserContainer);
        backButton = view.findViewById(R.id.backButton);
        tvSharedTitle = view.findViewById(R.id.tvSharedTitle);

        rvUserContainer.setLayoutManager(new LinearLayoutManager(getContext()));

        userAdapter = new UserAdapter(generateRandomUsers(), this::addUserToSharedList);
        rvUserContainer.setAdapter(userAdapter);

        backButton.setOnClickListener(v -> {
            if (getActivity() instanceof AddNotes) {
                ((AddNotes) getActivity()).switchToAddNotesFragment();
            }
        });

        if (getArguments() != null) {
            String title = getArguments().getString("NOTE_TITLE", "Shared To Fragment");
            tvSharedTitle.setText(title);
        }

        return view;
    }

    private List<User> generateRandomUsers() {
        String[] randomNames = {
                "Adi Putra", "Siti Nurhaliza", "Budi Santoso", "Dewi Ayu",
                "Tono Wijaya", "Yanti Sari", "Rudi Pratama", "Maya Lestari",
                "Agus Rahman", "Intan Dewi", "Hendra Kusuma", "Putri Ayu"
        };
        int[] profilePhotos = {R.drawable.friend_1, R.drawable.friend_2, R.drawable.friend_3};
        Random random = new Random();

        List<User> users = new ArrayList<>();
        Set<String> addedNames = new HashSet<>(NOTE_SHARED_TO);

        while (users.size() < 12 && addedNames.size() < randomNames.length) {
            String name = randomNames[random.nextInt(randomNames.length)];
            if (addedNames.contains(name)) {
                continue;
            }
            int photo = profilePhotos[random.nextInt(profilePhotos.length)];
            users.add(new User(name, photo));
            addedNames.add(name);
        }
        return users;
    }


    public void addUserToSharedList(String userName) {
        if (userName == null || NOTE_SHARED_TO.contains(userName)) {
            return;
        }

        Bundle arguments = getArguments();
        if (arguments == null) {
            Toast.makeText(getContext(), "Note information is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        String noteId = arguments.getString("NOTE_ID");
        if (noteId == null) {
            Toast.makeText(getContext(), "Note ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("/" + noteId + "/sharedTo", NOTE_SHARED_TO);
        updates.get("/" + noteId + "/sharedTo");
        NOTE_SHARED_TO.add(userName);

        database.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    userAdapter.removeUser(userName);
                    Toast.makeText(getContext(), "Note shared to " + userName, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to add user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}