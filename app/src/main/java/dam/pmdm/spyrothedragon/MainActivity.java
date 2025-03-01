package dam.pmdm.spyrothedragon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import dam.pmdm.spyrothedragon.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private boolean guiaActiva = false;
    private boolean guiaCerrada = false;

    NavController navController = null;

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
            }
            else {
                // Si se navega a una pantalla donde se desea mostrar la flecha de atrás, habilítala
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        });

    }

    private boolean selectedBottomMenu(@NonNull MenuItem menuItem) {
        // Si la guía está activa, no permitir navegación
        if (guiaActiva) {
            return false; // Bloquea cambios de pestaña
        }

        if (menuItem.getItemId() == R.id.nav_characters)
            navController.navigate(R.id.navigation_characters);
        else if (menuItem.getItemId() == R.id.nav_worlds)
            navController.navigate(R.id.navigation_worlds);
        else
            navController.navigate(R.id.navigation_collectibles);

        return true;
    }

    public void setGuiaActiva(boolean estado) {
        this.guiaActiva = estado;
    }

    public boolean isGuiaActiva() {
        return guiaActiva;
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
                })
                .setNegativeButton(getString(R.string.cancelar), null)
                .show();
    }

    public void bloquearInteraccionRecyclerView(RecyclerView recyclerView, boolean bloquear) {
        if (recyclerView != null) {
            if (bloquear) {
                recyclerView.setOnTouchListener((v, event) -> true); // Bloquea interacción táctil
                //recyclerView.setAlpha(0.5f); // Reducir opacidad para indicar que está deshabilitado
            } else {
                recyclerView.setOnTouchListener(null); // Permite interacción táctil nuevamente
                //recyclerView.setAlpha(1f); // Restaura la opacidad
            }
        }
    }





}