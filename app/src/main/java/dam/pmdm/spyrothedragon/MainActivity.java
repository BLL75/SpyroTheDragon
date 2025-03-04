package dam.pmdm.spyrothedragon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import dam.pmdm.spyrothedragon.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private boolean guiaCerrada = false;
    private NavController navController;
    private SoundPool soundPool;
    private int soundBotonClick, soundBocadillo, soundFinalGuia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recuperar el estado de guiaCerrada desde SharedPreferences
        SharedPreferences prefs = getSharedPreferences("SpyroAppPrefs", MODE_PRIVATE);
        guiaCerrada = prefs.getBoolean("guia_cerrada", false);
        boolean welcomeVisto = prefs.getBoolean("welcome_visto", false);

        // Si la pantalla de inicio no ha sido vista, abrir WelcomeActivity
        if (!welcomeVisto) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            finish(); // Cierra MainActivity hasta que el usuario vuelva de WelcomeActivity
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
        if (navHostFragment != null) {
            navController = NavHostFragment.findNavController(navHostFragment);
            NavigationUI.setupWithNavController(binding.navView, navController);
            NavigationUI.setupActionBarWithNavController(this, navController);
        }

        binding.navView.setOnItemSelectedListener(this::selectedBottomMenu);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_characters ||
                    destination.getId() == R.id.navigation_worlds ||
                    destination.getId() == R.id.navigation_collectibles) {
                // Para las pantallas de los tabs, no queremos que aparezca la flecha de atrás
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else {
                // Si se navega a una pantalla donde se desea mostrar la flecha de atrás, habilítala
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        });

        // Configurar SoundPool
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(3)
                .setAudioAttributes(audioAttributes)
                .build();

        // Cargar sonidos en memoria
        soundBotonClick = soundPool.load(this, R.raw.sfx_boton_click, 1);
        soundBocadillo = soundPool.load(this, R.raw.sfx_bocadillo_aparece, 1);
        soundFinalGuia = soundPool.load(this, R.raw.sfx_final_guia, 1);

        // Verificar si los sonidos se cargaron correctamente
        if (soundBotonClick == 0 || soundBocadillo == 0 || soundFinalGuia == 0) {
            Log.e("MainActivity", "Error al cargar uno o más sonidos");
        } else {
            Log.d("MainActivity", "Todos los sonidos se han cargado correctamente");
        }

        // Listener para verificar cuándo los sonidos están listos
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (status == 0) {
                    Log.d("MainActivity", "Sonido cargado correctamente: " + sampleId);
                } else {
                    Log.e("MainActivity", "Error al cargar el sonido: " + sampleId);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (soundPool != null) {
            soundPool.autoPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (soundPool != null) {
            soundPool.autoResume();
        }
    }

    private boolean selectedBottomMenu(@NonNull MenuItem menuItem) {

        if (menuItem.getItemId() == R.id.nav_characters)
            navegarConTransicion(R.id.navigation_characters);
        else if (menuItem.getItemId() == R.id.nav_worlds)
            navegarConTransicion(R.id.navigation_worlds);
        else
            navegarConTransicion(R.id.navigation_collectibles);

        return true;
    }

    public void setGuiaCerrada(boolean estado) {
        this.guiaCerrada = estado;

        // Guardar el estado en SharedPreferences
        SharedPreferences prefs = getSharedPreferences("SpyroAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("guia_cerrada", estado);
        editor.apply();
    }

    public boolean isGuiaCerrada() {
        return guiaCerrada;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Infla el menú
        getMenuInflater().inflate(R.menu.about_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Gestiona el clic en el ítem de información
        if (item.getItemId() == R.id.action_info) {
            showInfoDialog();  // Muestra el diálogo
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public NavController getNavController() {
        return navController;
    }

    private void showInfoDialog() {
        // Crear un diálogo de información
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_about)
                .setMessage(R.string.text_about)
                .setPositiveButton(R.string.accept, null)
                .show();
    }

    //  Función en MainActivity para cerrar la guía desde cualquier fragmento
    public void mostrarDialogoCerrarManual(View bocadillo, View fondoOscuro) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.titulo_cerrar_manual))
                .setMessage(getString(R.string.mensaje_cerrar_manual))
                .setPositiveButton(getString(R.string.aceptar), (dialog, which) -> {
                    bocadillo.setVisibility(View.GONE);
                    fondoOscuro.setVisibility(View.GONE);
                    setGuiaCerrada(true); // Guardamos en SharedPreferences que la guía fue cerrada

                    // Navegar al fragment de Characters
                    NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
                    navController.navigate(R.id.navigation_characters);
                })
                .setNegativeButton(getString(R.string.cancelar), null)
                .show();
    }


    // Método para bloquear el RecyclerView cuando la guía está activa
    public void bloquearInteraccionRecyclerView(RecyclerView recyclerView, boolean bloquear) {
        if (recyclerView != null) {
            recyclerView.setOnTouchListener(bloquear ? (v, event) -> true : null);
        }
    }

    // Método para aplicar transiciones entre pantallas
    public void navegarConTransicion(int destino) {
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
        if (navHostFragment != null) {
            navHostFragment.getView().startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_transition));
            navController.navigate(destino);
        }
    }

    public void reproducirSonido(int soundId) {
        if (soundPool == null) {
            Log.e("MainActivity", "SoundPool no está inicializado");
            return;
        }

        if (soundId != 0) {
            Log.d("MainActivity", "Intentando reproducir sonido con ID: " + soundId);
            int streamId = soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
            if (streamId == 0) {
                Log.e("MainActivity", "Error al reproducir el sonido con ID: " + soundId);
            } else {
                Log.d("MainActivity", "Sonido reproducido correctamente. Stream ID: " + streamId);
            }
        } else {
            Log.e("MainActivity", "ID de sonido no válido: " + soundId);
        }
    }

    // Métodos públicos para obtener los IDs de los sonidos
    public int getSoundBotonClick() {
        return soundBotonClick;
    }

    public int getSoundBocadillo() {
        return soundBocadillo;
    }

    public int getSoundFinalGuia() {
        return soundFinalGuia;
    }
}