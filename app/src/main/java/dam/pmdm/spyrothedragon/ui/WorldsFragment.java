package dam.pmdm.spyrothedragon.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import dam.pmdm.spyrothedragon.R;
import dam.pmdm.spyrothedragon.adapters.WorldsAdapter;
import dam.pmdm.spyrothedragon.databinding.FragmentWorldsBinding;
import dam.pmdm.spyrothedragon.models.World;

import dam.pmdm.spyrothedragon.MainActivity;


public class WorldsFragment extends Fragment {

    private FragmentWorldsBinding binding;
    private RecyclerView recyclerView;
    private WorldsAdapter adapter;
    private List<World> worldsList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentWorldsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recyclerView = binding.recyclerViewWorlds;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        worldsList = new ArrayList<>();
        adapter = new WorldsAdapter(worldsList);
        recyclerView.setAdapter(adapter);

        loadWorlds();

        // Configurar la visibilidad del bocadillo
        setupBocadillo(root);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadWorlds() {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.worlds);

            // Crear un parser XML
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();
            World currentWorld = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = null;

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();

                        if ("world".equals(tagName)) {
                            currentWorld = new World();
                        } else if (currentWorld != null) {
                            if ("name".equals(tagName)) {
                                currentWorld.setName(parser.nextText());
                            } else if ("description".equals(tagName)) {
                                currentWorld.setDescription(parser.nextText());
                            } else if ("image".equals(tagName)) {
                                currentWorld.setImage(parser.nextText());
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        tagName = parser.getName();

                        if ("world".equals(tagName) && currentWorld != null) {
                            worldsList.add(currentWorld);
                        }
                        break;
                }

                eventType = parser.next();
            }

            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupBocadillo(View root) {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null && mainActivity.isGuiaCerrada()) {
            return; // Si la guía está cerrada, no mostrar el bocadillo
        }

        View bocadillo = root.findViewById(R.id.bocadilloPersonajes);
        TextView textoBocadillo = bocadillo.findViewById(R.id.textoBocadillo);
        View fondoOscuro = root.findViewById(R.id.fondoOscuro);
        ImageButton btnCerrarManual = bocadillo.findViewById(R.id.btnCerrarManual);
        ImageButton btnAdelante = bocadillo.findViewById(R.id.btnAdelante);
        ImageButton btnAtras = bocadillo.findViewById(R.id.btnAtras);

        // Personalizar texto del bocadillo
        textoBocadillo.setText(getString(R.string.texto_bocadillo_mundos));

        // Aplicar animaciones
        Animation fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up);
        bocadillo.startAnimation(fadeIn);
        bocadillo.startAnimation(slideUp);
        mainActivity.reproducirSonido(mainActivity.getSoundBocadillo()); // Usar el ID correcto


        // Aplicar configuración visual
        mostrarBocadillo(bocadillo, fondoOscuro, textoBocadillo, btnCerrarManual, btnAtras, btnAdelante);
        posicionarBocadillo(bocadillo, true); // Bocadillo centrado

        // Llamamos al método en MainActivity para bloquear el RecyclerView
        if (mainActivity != null) {
            mainActivity.bloquearInteraccionRecyclerView(recyclerView, true);
        }

        // Configuración del botón de cierre
        btnCerrarManual.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).mostrarDialogoCerrarManual(bocadillo, fondoOscuro);
        });


        // Configuración del botón Adelante para ir a Coleccionables
        btnAdelante.setOnClickListener(v -> {
            mainActivity.reproducirSonido(mainActivity.getSoundBotonClick()); // Usar el ID correcto
            mainActivity.navegarConTransicion(R.id.navigation_collectibles);
        });

        // Configuración del botón Atrás para regresar a Personajes
        btnAtras.setOnClickListener(v -> {
            mainActivity.reproducirSonido(mainActivity.getSoundBotonClick()); // Usar el ID correcto
            mainActivity.navegarConTransicion(R.id.navigation_characters);
        });
    }

    //  Función para mostrar los elementos del bocadillo
    private void mostrarBocadillo(View bocadillo, View fondoOscuro, TextView texto, ImageButton btnCerrar, ImageButton btnAtras, ImageButton btnAdelante) {
        bocadillo.setVisibility(View.VISIBLE);
        fondoOscuro.setVisibility(View.VISIBLE);
        texto.setVisibility(View.VISIBLE);
        btnCerrar.setVisibility(View.VISIBLE);
        btnAtras.setVisibility(View.VISIBLE);
        btnAdelante.setVisibility(View.VISIBLE);
    }

    //  Función para posicionar el bocadillo en la pantalla
    private void posicionarBocadillo(View bocadillo, boolean centrado) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) bocadillo.getLayoutParams();

        if (centrado) {
            params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        } else {
            params.startToStart = ConstraintLayout.LayoutParams.UNSET;
            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        }

        params.bottomMargin = 450; // Ajustamos la altura del bocadillo
        bocadillo.setLayoutParams(params);
        bocadillo.requestLayout();
    }

}
