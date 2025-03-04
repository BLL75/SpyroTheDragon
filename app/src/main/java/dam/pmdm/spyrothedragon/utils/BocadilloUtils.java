package dam.pmdm.spyrothedragon.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import dam.pmdm.spyrothedragon.R;

public class BocadilloUtils {

    public static void setupBocadilloViews(View root, int textoResId) {
        View bocadillo = root.findViewById(R.id.bocadilloPersonajes);
        TextView textoBocadillo = bocadillo.findViewById(R.id.textoBocadillo);
        View fondoOscuro = root.findViewById(R.id.fondoOscuro);
        ImageButton btnCerrarManual = bocadillo.findViewById(R.id.btnCerrarManual);
        ImageButton btnAdelante = bocadillo.findViewById(R.id.btnAdelante);
        ImageButton btnAtras = bocadillo.findViewById(R.id.btnAtras);

        textoBocadillo.setText(root.getContext().getString(textoResId));
    }

    // Método para aplicar animaciones al bocadillo
    public static void aplicarAnimaciones(View bocadillo) {
        Animation fadeIn = AnimationUtils.loadAnimation(bocadillo.getContext(), R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(bocadillo.getContext(), R.anim.slide_up);
        bocadillo.startAnimation(fadeIn);
        bocadillo.startAnimation(slideUp);
    }
}