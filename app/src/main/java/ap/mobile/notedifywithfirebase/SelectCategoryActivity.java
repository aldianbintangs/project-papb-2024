package ap.mobile.notedifywithfirebase;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SelectCategoryActivity extends AppCompatActivity {

    private CardView cvIdeas, cvGoals, cvGuidance, cvBuy, cvRoutines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);

        cvIdeas = findViewById(R.id.card_idea);
        cvGoals = findViewById(R.id.card_goals);
        cvGuidance = findViewById(R.id.card_guidance);
        cvBuy = findViewById(R.id.card_buy);
        cvRoutines = findViewById(R.id.card_routine);

        cvIdeas.setOnClickListener(v -> {
            Intent intent = new Intent(SelectCategoryActivity.this, AddNotes.class);
            intent.putExtra("NOTE_CATEGORY", "Interesting Ideas");
            startActivity(intent);
            finish();
        });

        cvGoals.setOnClickListener(v -> {
            Intent intent = new Intent(SelectCategoryActivity.this, AddNotes.class);
            intent.putExtra("NOTE_CATEGORY", "Goals");
            startActivity(intent);
            finish();
        });

        cvGuidance.setOnClickListener(v -> {
            Intent intent = new Intent(SelectCategoryActivity.this, AddNotes.class);
            intent.putExtra("NOTE_CATEGORY", "Guidance");
            startActivity(intent);
            finish();
        });

        cvBuy.setOnClickListener(v -> {
            Intent intent = new Intent(SelectCategoryActivity.this, AddNotes.class);
            intent.putExtra("NOTE_CATEGORY", "Buying Something");
            startActivity(intent);
            finish();
        });

        cvRoutines.setOnClickListener(v -> {
            Intent intent = new Intent(SelectCategoryActivity.this, AddNotes.class);
            intent.putExtra("NOTE_CATEGORY", "Routine Tasks");
            startActivity(intent);
            finish();
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}