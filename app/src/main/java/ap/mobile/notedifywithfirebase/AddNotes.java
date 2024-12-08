package ap.mobile.notedifywithfirebase;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

public class AddNotes extends AppCompatActivity {
    private AddNotesFragment addNotesFragment;
    private SharedToFragment sharedToFragment;
    private String imageUrl, getImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);

        getImageUrl = getIntent().getStringExtra("imageUrl");

        if (getImageUrl != null) {
            imageUrl = getImageUrl;
        }

        addNotesFragment = AddNotesFragment.newInstance(
                getIntent().getStringExtra("NOTE_ID"),
                getIntent().getStringExtra("NOTE_TITLE"),
                getIntent().getStringExtra("NOTE_CONTENT"),
                getIntent().getStringExtra("NOTE_CATEGORY"),
                imageUrl
        );

        sharedToFragment = new SharedToFragment();

        ArrayList<String> sharedTo = getIntent().getStringArrayListExtra("NOTE_SHARED_TO");
        String string = getIntent().getStringExtra("NOTE_ID");
        Bundle bundle = new Bundle();
        bundle.putString("NOTE_ID", string);
        bundle.putStringArrayList("NOTE_SHARED_TO", sharedTo);
        sharedToFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, addNotesFragment)
                .commit();
    }

    public void switchToSharedFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerView, sharedToFragment);
        transaction.commit();
    }

    public void switchToAddNotesFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerView, addNotesFragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        if (currentFragment instanceof AddNotesFragment) {
            ((AddNotesFragment) currentFragment).saveNoteAndNavigateBack();
        } else {
            super.onBackPressed();
        }
    }
}