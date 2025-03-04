package dam.pmdm.spyrothedragon.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dam.pmdm.spyrothedragon.MainActivity;
import dam.pmdm.spyrothedragon.R;
import dam.pmdm.spyrothedragon.adapters.CollectiblesAdapter;
import dam.pmdm.spyrothedragon.databinding.FragmentCollectiblesBinding;
import dam.pmdm.spyrothedragon.models.Collectible;
import dam.pmdm.spyrothedragon.utils.BocadilloUtils;

/**
 * Fragmento que muestra la lista de coleccionables en la aplicación.
 * Gestiona la interacción con los coleccionables, la visualización de guías
 * y la reproducción de efectos de sonido.
 */
public class CollectiblesFragment extends Fragment {

    private FragmentCollectiblesBinding binding;
    private RecyclerView recyclerView;
    private CollectiblesAdapter adapter;
    private List<Collectible> collectiblesList;
    private boolean mostrandoBocadilloInfo = false; // Flag para saber qué bocadillo mostrar
    private boolean guiaCerrada = false;
    private Map<Integer, Integer> gemTapCountMap = new HashMap<>();
    private int lastTappedPosition = -1;

    /**
     * Método llamado cuando se crea la vista del fragmento.
     *
     * @param inflater           El LayoutInflater usado para inflar la vista.
     * @param container          El contenedor padre al que se adjuntará la vista.
     * @param savedInstanceState El estado previamente guardado de la actividad.
     * @return La vista inflada del fragmento.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCollectiblesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.recyclerViewCollectibles;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        collectiblesList = new ArrayList<>();
        adapter = new CollectiblesAdapter(collectiblesList);
        recyclerView.setAdapter(adapter);

        // Verificar si la guía ya ha sido cerrada
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            guiaCerrada = mainActivity.isGuiaCerrada();
        }

        if (guiaCerrada) {
            configurarClickEnGemas();
        }

        loadCollectibles();

        // Verificamos si la guía debe mostrarse
        if (!guiaCerrada) {
            if (mostrandoBocadilloInfo) {
                setupBocadilloInfo(root);
            } else {
                setupBocadillo(root);
            }
        }

        return binding.getRoot();
    }

    /**
     * Método llamado cuando la vista del fragmento es destruida.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Configura el listener para los clics en las gemas.
     */
    private void configurarClickEnGemas() {
        adapter.setOnGemClickListener(position -> {
            if (position != lastTappedPosition) {
                gemTapCountMap.put(lastTappedPosition, 0);
                lastTappedPosition = position;
            }
            int currentCount = gemTapCountMap.getOrDefault(position, 0) + 1;
            gemTapCountMap.put(position, currentCount);

            Log.d("CollectiblesFragment", "✅ Click en gema: " + position + " | Toques: " + currentCount);
            Toast.makeText(getContext(), "Gema " + position + " pulsada " + currentCount + " veces", Toast.LENGTH_SHORT).show();

            if (currentCount >= 4) {
                gemTapCountMap.put(position, 0);
                lanzarEasterEgg(position);
            }
        });
    }

    /**
     * Lanza una actividad de Easter Egg cuando se cumple la condición.
     *
     * @param position La posición de la gema que activó el Easter Egg.
     */
    private void lanzarEasterEgg(int position) {
        Log.d("CollectiblesFragment", "🎬 Lanzando Easter Egg para la gema en posición: " + position);
        Toast.makeText(getContext(), "¡Easter Egg desbloqueado en la gema " + position + "!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getActivity(), dam.pmdm.spyrothedragon.EasterEggVideoActivity.class);
        startActivity(intent);
    }

    /**
     * Carga los coleccionables desde un archivo XML.
     */
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

    /**
     * Configura el bocadillo de guía inicial.
     *
     * @param root La vista raíz del fragmento.
     */
    private void setupBocadillo(View root) {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null && mainActivity.isGuiaCerrada()) {
            return; // Si la guía está cerrada, no mostrar el bocadillo
        }

        // Configurar vistas del bocadillo usando la clase utilitaria
        BocadilloUtils.setupBocadilloViews(root, R.string.texto_bocadillo_coleccionables);

        // Obtener referencias a las vistas configuradas en BocadilloUtils
        View bocadillo = root.findViewById(R.id.bocadilloPersonajes);
        View fondoOscuro = root.findViewById(R.id.fondoOscuro);
        ImageButton btnCerrarManual = bocadillo.findViewById(R.id.btnCerrarManual);
        ImageButton btnAdelante = bocadillo.findViewById(R.id.btnAdelante);
        ImageButton btnAtras = bocadillo.findViewById(R.id.btnAtras);

        // Aplicar animaciones
        BocadilloUtils.aplicarAnimaciones(bocadillo);
        mainActivity.reproducirSonido(mainActivity.getSoundBocadillo());

        // Mostrar el bocadillo y los elementos
        bocadillo.setVisibility(View.VISIBLE);
        fondoOscuro.setVisibility(View.VISIBLE);
        fondoOscuro.setClickable(true);
        fondoOscuro.setFocusable(true);

        // Bloquear el RecyclerView
        if (mainActivity != null) {
            mainActivity.bloquearInteraccionRecyclerView(recyclerView, true);
        }

        // Posicionamiento del bocadillo
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) bocadillo.getLayoutParams();
        params.bottomMargin = 450;
        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        bocadillo.setLayoutParams(params);
        bocadillo.requestLayout();

        // Configuración del botón de cierre
        btnCerrarManual.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).mostrarDialogoCerrarManual(bocadillo, fondoOscuro);
        });

        // Botón Adelante - Oculta este bocadillo y muestra el de información
        btnAdelante.setOnClickListener(v -> {
            bocadillo.setVisibility(View.GONE);
            fondoOscuro.setVisibility(View.GONE);
            mainActivity.reproducirSonido(mainActivity.getSoundBotonClick());
            mostrandoBocadilloInfo = true; // Guardamos que ahora se muestra el bocadillo informativo
            setupBocadilloInfo(root);
        });

        // Botón Atrás - Vuelve a Mundos
        btnAtras.setOnClickListener(v -> {
            mainActivity.reproducirSonido(mainActivity.getSoundBotonClick());
            ((MainActivity) requireActivity()).getNavController().navigate(R.id.navigation_worlds);
        });
    }

    /**
     * Configura el bocadillo de información.
     *
     * @param root La vista raíz del fragmento.
     */
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

        // Aplicar animaciones
        // Aplicar animaciones usando la clase utilitaria
        BocadilloUtils.aplicarAnimaciones(bocadilloInfo);

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.reproducirSonido(mainActivity.getSoundBocadillo());

        // Mostrar bocadillo y fondo
        bocadilloInfo.setVisibility(View.VISIBLE);
        fondoOscuro.setVisibility(View.VISIBLE);

        if (mainActivity != null) {
            mainActivity.bloquearInteraccionRecyclerView(recyclerView, true);
        }

        // Configuración del botón de cierre
        btnCerrarManual.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).mostrarDialogoCerrarManual(bocadilloInfo, fondoOscuro);
        });

        btnAdelanteBocadilloInfo.setOnClickListener(v -> {
            mainActivity.reproducirSonido(mainActivity.getSoundBotonClick());
            ((ViewGroup) root).removeView(bocadilloInfo); // Eliminar bocadillo actual
            setupBocadilloResumen(root);
        });

        // Botón Atrás - Vuelve al bocadillo de coleccionables
        btnAtrasBocadilloInfo.setOnClickListener(v -> {
            mainActivity.reproducirSonido(mainActivity.getSoundBotonClick());
            ((ViewGroup) root).removeView(bocadilloInfo);
            mostrandoBocadilloInfo = false; // Volvemos al estado anterior
            setupBocadillo(root);
        });
    }

    /**
     * Configura el bocadillo de resumen de la guía.
     *
     * @param root La vista raíz del fragmento.
     */
    private void setupBocadilloResumen(View root) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View bocadilloResumen = inflater.inflate(R.layout.bocadillo_resumen, (ViewGroup) root, false);
        ((ViewGroup) root).addView(bocadilloResumen);

        TextView textoResumen = bocadilloResumen.findViewById(R.id.textoResumen);
        Button btnFinalizarGuia = bocadilloResumen.findViewById(R.id.btnFinalizarGuia);
        View fondoOscuro = root.findViewById(R.id.fondoOscuro);

        // Aplicar animaciones
        // Aplicar animaciones usando la clase utilitaria
        BocadilloUtils.aplicarAnimaciones(bocadilloResumen);
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.reproducirSonido(mainActivity.getSoundBocadillo());

        // Mostrar el bocadillo de resumen
        bocadilloResumen.setVisibility(View.VISIBLE);
        fondoOscuro.setVisibility(View.VISIBLE);

        // Configurar botón para finalizar la guía
        btnFinalizarGuia.setOnClickListener(v -> {
            bocadilloResumen.setVisibility(View.GONE);
            fondoOscuro.setVisibility(View.GONE);
            if (mainActivity != null) {
                mainActivity.reproducirSonido(mainActivity.getSoundFinalGuia());
                mainActivity.getNavController().navigate(R.id.navigation_characters);
            }

            // Guardar en SharedPreferences que la guía ha sido completada
            if (mainActivity != null) {
                mainActivity.setGuiaCerrada(true);
            }
        });
    }
}