package dam.pmdm.spyrothedragon.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import dam.pmdm.spyrothedragon.MainActivity;
import dam.pmdm.spyrothedragon.R;
import dam.pmdm.spyrothedragon.adapters.CollectiblesAdapter;
import dam.pmdm.spyrothedragon.databinding.FragmentCollectiblesBinding;
import dam.pmdm.spyrothedragon.models.Collectible;

public class CollectiblesFragment extends Fragment {

    private FragmentCollectiblesBinding binding;
    private RecyclerView recyclerView;
    private CollectiblesAdapter adapter;
    private List<Collectible> collectiblesList;
    private boolean mostrandoBocadilloInfo = false; // Flag para saber qué bocadillo mostrar

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCollectiblesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recyclerView = binding.recyclerViewCollectibles;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        collectiblesList = new ArrayList<>();
        adapter = new CollectiblesAdapter(collectiblesList);
        recyclerView.setAdapter(adapter);

        loadCollectibles();
        // Verificamos si estamos en el bocadillo de información o en el de coleccionables
        if (mostrandoBocadilloInfo) {
            setupBocadilloInfo(root);
        } else {
            setupBocadillo(root);
        }
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadCollectibles() {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.collectibles);

            // Crear un parser XML
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();
            Collectible currentCollectible = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = null;

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();

                        if ("collectible".equals(tagName)) {
                            currentCollectible = new Collectible();
                        } else if (currentCollectible != null) {
                            if ("name".equals(tagName)) {
                                currentCollectible.setName(parser.nextText());
                            } else if ("description".equals(tagName)) {
                                currentCollectible.setDescription(parser.nextText());
                            } else if ("image".equals(tagName)) {
                                currentCollectible.setImage(parser.nextText());
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        tagName = parser.getName();

                        if ("collectible".equals(tagName) && currentCollectible != null) {
                            collectiblesList.add(currentCollectible);
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

        // Personalizar texto para Coleccionables
        textoBocadillo.setText(getString(R.string.texto_bocadillo_coleccionables));

        // Mostrar el bocadillo y los elementos
        bocadillo.setVisibility(View.VISIBLE);
        fondoOscuro.setVisibility(View.VISIBLE);
        fondoOscuro.setClickable(true);
        fondoOscuro.setFocusable(true);

        // Posicionamiento del bocadillo
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) bocadillo.getLayoutParams();
        params.bottomMargin = 450;
        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        bocadillo.setLayoutParams(params);
        bocadillo.requestLayout();

        // Configuración del botón de cierre con confirmación
        // Configuración del botón de cierre
        btnCerrarManual.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).mostrarDialogoCerrarManual(bocadillo, fondoOscuro);
        });

        // Botón Adelante - Oculta este bocadillo y muestra el de información
        btnAdelante.setOnClickListener(v -> {
            bocadillo.setVisibility(View.GONE);
            fondoOscuro.setVisibility(View.GONE);
            mostrandoBocadilloInfo = true; // Guardamos que ahora se muestra el bocadillo informativo
            setupBocadilloInfo(root);
        });

        // Botón Atrás - Vuelve a Mundos
        btnAtras.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).getNavController().navigate(R.id.navigation_worlds);
        });
    }

    private void setupBocadilloInfo(View root) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View bocadilloInfo = inflater.inflate(R.layout.bocadillo_info, (ViewGroup) root, false);
        ((ViewGroup) root).addView(bocadilloInfo);

        TextView textoBocadilloInfo = bocadilloInfo.findViewById(R.id.textoBocadilloInfo);
        View fondoOscuro = root.findViewById(R.id.fondoOscuro);
        ImageButton btnCerrarManual = bocadilloInfo.findViewById(R.id.btnCerrarManual);
        ImageButton btnAdelanteBocadilloInfo = bocadilloInfo.findViewById(R.id.btnAdelanteBocadilloInfo);
        ImageButton btnAtrasBocadilloInfo = bocadilloInfo.findViewById(R.id.btnAtrasBocadilloInfo);

        // Personalizar texto del bocadillo de información
        textoBocadilloInfo.setText(getString(R.string.texto_bocadillo_info));

        // Mostrar bocadillo y fondo
        bocadilloInfo.setVisibility(View.VISIBLE);
        fondoOscuro.setVisibility(View.VISIBLE);

        // Botón de cierre - Llama a la confirmación
        // Configuración del botón de cierre
        btnCerrarManual.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).mostrarDialogoCerrarManual(bocadilloInfo, fondoOscuro);
        });

        btnAdelanteBocadilloInfo.setOnClickListener(v -> {
            ((ViewGroup) root).removeView(bocadilloInfo); // Eliminar bocadillo actual
            setupBocadilloResumen(root);
        });

        // Botón Atrás - Vuelve al bocadillo de coleccionables
        btnAtrasBocadilloInfo.setOnClickListener(v -> {
            ((ViewGroup) root).removeView(bocadilloInfo);
            mostrandoBocadilloInfo = false; // Volvemos al estado anterior
            setupBocadillo(root);
        });
    }

    private void setupBocadilloResumen(View root) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View bocadilloResumen = inflater.inflate(R.layout.bocadillo_resumen, (ViewGroup) root, false);
        ((ViewGroup) root).addView(bocadilloResumen);

        TextView textoResumen = bocadilloResumen.findViewById(R.id.textoResumen);
        Button btnFinalizarGuia = bocadilloResumen.findViewById(R.id.btnFinalizarGuia);
        View fondoOscuro = root.findViewById(R.id.fondoOscuro);

        // Mostrar el bocadillo de resumen
        bocadilloResumen.setVisibility(View.VISIBLE);
        fondoOscuro.setVisibility(View.VISIBLE);

        // Configurar botón para finalizar la guía
        btnFinalizarGuia.setOnClickListener(v -> {
            bocadilloResumen.setVisibility(View.GONE);
            fondoOscuro.setVisibility(View.GONE);

            // Guardar en SharedPreferences que la guía ha sido completada
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.setGuiaCerrada(true);
            }
        });
    }
}
