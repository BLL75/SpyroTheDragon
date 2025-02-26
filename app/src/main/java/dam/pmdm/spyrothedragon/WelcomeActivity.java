package dam.pmdm.spyrothedragon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recuperar el estado de welcomeVisto desde SharedPreferences
        SharedPreferences prefs = getSharedPreferences("SpyroAppPrefs", MODE_PRIVATE);
        boolean welcomeVisto = prefs.getBoolean("welcome_visto", false);

        // Si ya hemos pasado por WelcomeActivity antes, ir directamente a MainActivity
        if (welcomeVisto) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_welcome);

        ImageButton btnComenzar = findViewById(R.id.btnComenzar);
        btnComenzar.setOnClickListener(v -> {
            // Guardar en SharedPreferences que WelcomeActivity ya se mostró
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("welcome_visto", true);
            editor.apply();

            // Ir a MainActivity y cerrar WelcomeActivity
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}

