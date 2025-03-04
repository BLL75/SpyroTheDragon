package dam.pmdm.spyrothedragon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "SpyroAppPrefs";
    private static final String KEY_WELCOME_VISTO = "welcome_visto";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Si la bienvenida ya fue vista, ir directamente a MainActivity
        if (prefs.getBoolean(KEY_WELCOME_VISTO, false)) {
            navegarAMain();
            return;
        }

        setContentView(R.layout.activity_welcome);

        ImageButton btnComenzar = findViewById(R.id.btnComenzar);
        btnComenzar.setOnClickListener(v -> {
            guardarWelcomeVisto(prefs);
            navegarAMain();
        });
    }

    /**
     * Guarda en SharedPreferences que la pantalla de bienvenida ya fue vista.
     *
     * @param prefs Instancia de SharedPreferences.
     */
    private void guardarWelcomeVisto(SharedPreferences prefs) {
        prefs.edit().putBoolean(KEY_WELCOME_VISTO, true).apply();
    }

    /**
     * Navega a MainActivity y cierra WelcomeActivity.
     */
    private void navegarAMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finishAffinity(); // Cierra todas las actividades en la pila
    }
}
