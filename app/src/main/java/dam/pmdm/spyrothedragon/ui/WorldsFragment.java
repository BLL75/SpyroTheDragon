package dam.pmdm.spyrothedragon.ui;

import dam.pmdm.spyrothedragon.utils.BocadilloUtils;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import dam.pmdm.spyrothedragon.adapters.WorldsAdapter;
import dam.pmdm.spyrothedragon.databinding.FragmentWorldsBinding;
import dam.pmdm.spyrothedragon.models.World;

public class WorldsFragment extends Fragment {

    private FragmentWorldsBinding binding;
    private RecyclerView recyclerView;
    private WorldsAdapter adapter;
    private List<World> worldsList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWorldsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        inicializarRecyclerView();
        cargarMundos();
        configurarBocadillo(root);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Inicializa el RecyclerView y su adaptador.
     */
    private void inicializarRecyclerView() {
        recyclerView = binding.recyclerViewWorlds;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new WorldsAdapter(worldsList);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Carga la lista de mundos desde el archivo XML en res/raw.
     */
    private void cargarMundos() {
        try (InputStream inputStream = getResources().openRawResource(R.raw.worlds)) {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();
            World mundoActual = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String etiqueta;

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        etiqueta = parser.getName();
                        if ("world".equals(etiqueta)) {
                            mundoActual = new World();
                        } else if (mundoActual != null) {
                            switch (etiqueta) {
                                case "name":
                                    mundoActual.setName(parser.nextText());
                                    break;
                                case "description":
                                    mundoActual.setDescription(parser.nextText());
                                    break;
                                case "image":
                                    mundoActual.setImage(parser.nextText());
                                    break;
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        etiqueta = parser.getName();
                        if ("world".equals(etiqueta) && mundoActual != null) {
                            worldsList.add(mundoActual);
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
     * Configura la visibilidad y animaciones del bocadillo.
     *
     * @param root Vista raíz del fragmento.
     */
    private void configurarBocadillo(View root) {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity == null || mainActivity.isGuiaCerrada()) {
            return; // Si la guía está cerrada, no mostrar el bocadillo
        }

        // Configurar vistas del bocadillo usando la clase utilitaria
        BocadilloUtils.setupBocadilloViews(root, R.string.texto_bocadillo_mundos);

        // Obtener referencias a las vistas configuradas en BocadilloUtils
        View bocadillo = root.findViewById(R.id.bocadilloPersonajes);
        View fondoOscuro = root.findViewById(R.id.fondoOscuro);

        // Aplicar animaciones
        // Aplicar animaciones usando la clase utilitaria
        BocadilloUtils.aplicarAnimaciones(bocadillo);
        mainActivity.reproducirSonido(mainActivity.getSoundBocadillo());

        mostrarBocadillo(bocadillo, fondoOscuro);
        posicionarBocadillo(bocadillo, true);

        // Configurar botones del bocadillo
        ImageButton btnCerrarManual = bocadillo.findViewById(R.id.btnCerrarManual);
        ImageButton btnAdelante = bocadillo.findViewById(R.id.btnAdelante);
        ImageButton btnAtras = bocadillo.findViewById(R.id.btnAtras);
        configurarBotonesBocadillo(mainActivity, bocadillo, fondoOscuro, btnCerrarManual, btnAdelante, btnAtras);

        // Bloquear interacción con el RecyclerView si existe
        if (recyclerView != null) {
            mainActivity.bloquearInteraccionRecyclerView(recyclerView, true);
        }
    }

    /**
     * Aplica animaciones al bocadillo.
     */

    /**
     * Muestra el bocadillo y el fondo oscuro.
     */
    private void mostrarBocadillo(View bocadillo, View fondoOscuro) {
        bocadillo.setVisibility(View.VISIBLE);
        fondoOscuro.setVisibility(View.VISIBLE);
    }

    /**
     * Configura los botones del bocadillo para cerrar y navegar entre pantallas.
     */
    private void configurarBotonesBocadillo(MainActivity mainActivity, View bocadillo, View fondoOscuro,
                                            ImageButton btnCerrarManual, ImageButton btnAdelante, ImageButton btnAtras) {

        btnCerrarManual.setOnClickListener(v -> mainActivity.mostrarDialogoCerrarManual(bocadillo, fondoOscuro));

        btnAdelante.setOnClickListener(v -> {
            mainActivity.reproducirSonido(mainActivity.getSoundBotonClick());
            mainActivity.navegarConTransicion(R.id.navigation_collectibles);
        });

        btnAtras.setOnClickListener(v -> {
            mainActivity.reproducirSonido(mainActivity.getSoundBotonClick());
            mainActivity.navegarConTransicion(R.id.navigation_characters);
        });
    }

    /**
     * Posiciona el bocadillo en la pantalla.
     *
     * @param bocadillo Vista del bocadillo.
     * @param centrado  True si debe estar centrado, false si debe estar alineado a un lado.
     */
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
