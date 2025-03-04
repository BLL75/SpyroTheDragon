package dam.pmdm.spyrothedragon.ui;

import dam.pmdm.spyrothedragon.utils.BocadilloUtils;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import dam.pmdm.spyrothedragon.models.Character;
import dam.pmdm.spyrothedragon.adapters.CharactersAdapter;
import dam.pmdm.spyrothedragon.databinding.FragmentCharactersBinding;

public class CharactersFragment extends Fragment {

    private FragmentCharactersBinding binding;
    private RecyclerView recyclerView;
    private CharactersAdapter adapter;
    private List<Character> charactersList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCharactersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        inicializarRecyclerView();
        cargarPersonajes();
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
        recyclerView = binding.recyclerViewCharacters;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CharactersAdapter(charactersList);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Carga la lista de personajes desde el archivo XML en res/raw.
     */
    private void cargarPersonajes() {
        try (InputStream inputStream = getResources().openRawResource(R.raw.characters)) {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();
            Character personajeActual = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String etiqueta;

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        etiqueta = parser.getName();
                        if ("character".equals(etiqueta)) {
                            personajeActual = new Character();
                        } else if (personajeActual != null) {
                            if ("name".equals(etiqueta)) {
                                personajeActual.setName(parser.nextText());
                            } else if ("description".equals(etiqueta)) {
                                personajeActual.setDescription(parser.nextText());
                            } else if ("image".equals(etiqueta)) {
                                personajeActual.setImage(parser.nextText());
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        etiqueta = parser.getName();
                        if ("character".equals(etiqueta) && personajeActual != null) {
                            charactersList.add(personajeActual);
                        }
                        break;
                }
                eventType = parser.next();
            }

            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.e("CharactersFragment", "❌ Error al cargar personajes", e);
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
            Log.d("cerrarGuia", "✅ Guía cerrada, no se muestra el bocadillo");
            return;
        }

        // Configurar vistas del bocadillo usando la clase utilitaria
        BocadilloUtils.setupBocadilloViews(root, R.string.texto_bocadillo_personajes);

        // Obtener referencias a las vistas configuradas en BocadilloUtils
        View bocadillo = root.findViewById(R.id.bocadilloPersonajes);
        View fondoOscuro = root.findViewById(R.id.fondoOscuro);

        // Aplicar animaciones usando la clase utilitaria
        BocadilloUtils.aplicarAnimaciones(bocadillo);

        mainActivity.reproducirSonido(mainActivity.getSoundBocadillo());

        bocadillo.setVisibility(View.VISIBLE);
        fondoOscuro.setVisibility(View.VISIBLE);

        // Bloquear interacción con el RecyclerView si existe
        if (recyclerView != null) {
            mainActivity.bloquearInteraccionRecyclerView(recyclerView, true);
        }

        // Configurar botones del bocadillo
        ImageButton btnCerrarManual = bocadillo.findViewById(R.id.btnCerrarManual);
        ImageButton btnAdelante = bocadillo.findViewById(R.id.btnAdelante);
        ImageButton btnAtras = bocadillo.findViewById(R.id.btnAtras);
        configurarBotonesBocadillo(mainActivity, bocadillo, fondoOscuro, btnCerrarManual, btnAdelante, btnAtras);
    }

    /**
     * Configura los botones del bocadillo para cerrar y navegar entre pantallas.
     */
    private void configurarBotonesBocadillo(MainActivity mainActivity, View bocadillo, View fondoOscuro,
                                            ImageButton btnCerrarManual, ImageButton btnAdelante, ImageButton btnAtras) {

        btnCerrarManual.setOnClickListener(v -> mainActivity.mostrarDialogoCerrarManual(bocadillo, fondoOscuro));

        btnAdelante.setOnClickListener(v -> {
            mainActivity.reproducirSonido(mainActivity.getSoundBotonClick());
            mainActivity.navegarConTransicion(R.id.navigation_worlds);
        });

        btnAtras.setOnClickListener(v -> {
            mainActivity.reproducirSonido(mainActivity.getSoundBotonClick());
            mainActivity.getNavController().navigate(R.id.navigation_characters);
        });
    }
}
