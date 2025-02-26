package dam.pmdm.spyrothedragon.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        if (mainActivity != null && mainActivity.isGuiaCerrada()){
            return; // Si la guía está cerrada, no mostrar el bocadillo
        }

        View bocadillo = root.findViewById(R.id.bocadilloPersonajes);
        TextView textoBocadillo = bocadillo.findViewById(R.id.textoBocadillo);
        View fondoOscuro = root.findViewById(R.id.fondoOscuro);
        ImageButton btnCerrarManual = bocadillo.findViewById(R.id.btnCerrarManual);
        ImageButton btnAdelante = bocadillo.findViewById(R.id.btnAdelante);
        ImageButton btnAtras = bocadillo.findViewById(R.id.btnAtras);

        // Personalizar texto para Mundos
        textoBocadillo.setText(getString(R.string.texto_bocadillo_mundos));

        // Asegurar que el bocadillo y sus elementos sean visibles
        bocadillo.setVisibility(View.VISIBLE);
        fondoOscuro.setVisibility(View.VISIBLE);
        textoBocadillo.setVisibility(View.VISIBLE);
        btnCerrarManual.setVisibility(View.VISIBLE);
        btnAtras.setVisibility(View.VISIBLE);
        btnAdelante.setVisibility(View.VISIBLE);

        // Ajustar la posición en Coleccionables para alinearlo con la pestaña derecha
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) bocadillo.getLayoutParams();
        params.bottomMargin = 450; // Ajustamos la altura para alinearlo con la pestaña de Coleccionables
        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        bocadillo.setLayoutParams(params);
        bocadillo.requestLayout(); // Forzar actualización del layout

        // Configuración del botón de cierre
        btnCerrarManual.setOnClickListener(v -> mostrarDialogoCerrarManual(bocadillo, fondoOscuro));

        // Configuración del botón Adelante para cambiar a Coleccionables
        btnAdelante.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).getNavController().navigate(R.id.navigation_collectibles);
        });

        // Configuración del botón Atrás para regresar a Personajes
        btnAtras.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).getNavController().navigate(R.id.navigation_characters);
        });
    }


    // Método para mostrar el AlertDialog de confirmación
    private void mostrarDialogoCerrarManual(View bocadillo, View fondoOscuro) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cerrar Manual")
                .setMessage("¿Seguro que quieres cerrar la guía?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    bocadillo.setVisibility(View.GONE);
                    fondoOscuro.setVisibility(View.GONE);

                    MainActivity mainActivity = (MainActivity) getActivity();
                    if (mainActivity != null) {
                        mainActivity.setGuiaCerrada(true); // Guardamos el estado en SharedPreferences
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

}
