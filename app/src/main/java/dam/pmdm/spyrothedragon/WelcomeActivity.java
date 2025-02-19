package dam.pmdm.spyrothedragon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ImageButton btnComenzar = findViewById(R.id.btnComenzar);
        btnComenzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar a la actividad de Personajes, tengo que cambiar que MainActivity por PersonajesActivity
                //Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                //startActivity(intent);
                finish();
            }
        });
    }
}

