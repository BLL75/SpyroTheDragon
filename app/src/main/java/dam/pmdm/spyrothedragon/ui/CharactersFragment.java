package dam.pmdm.spyrothedragon.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
    private List<Character> charactersList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCharactersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Inicializamos el RecyclerView y el adaptador
        recyclerView = binding.recyclerViewCharacters;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        charactersList = new ArrayList<>();
        adapter = new CharactersAdapter(charactersList);
        recyclerView.setAdapter(adapter);

        // Cargamos los personajes desde el XML
        loadCharacters();

        // Configurar la visibilidad del bocadillo
        setupBocadillo(root);

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadCharacters() {
        try {
            // Cargamos el archivo XML desde res/xml (NOTA: ahora se usa R.xml.characters)
            InputStream inputStream = getResources().openRawResource(R.raw.characters);

            // Crear un parser XML
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();
            Character currentCharacter = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = null;

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();

                        if ("character".equals(tagName)) {
                            currentCharacter = new Character();
                        } else if (currentCharacter != null) {
                            if ("name".equals(tagName)) {
                                currentCharacter.setName(parser.nextText());
                            } else if ("description".equals(tagName)) {
                                currentCharacter.setDescription(parser.nextText());
                            } else if ("image".equals(tagName)) {
                                currentCharacter.setImage(parser.nextText());
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        tagName = parser.getName();

                        if ("character".equals(tagName) && currentCharacter != null) {
                            charactersList.add(currentCharacter);
                        }
                        break;
                }

                eventType = parser.next();
            }

            adapter.notifyDataSetChanged(); // Notificamos al adaptador que los datos han cambiado
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupBocadillo(View root) {
        View bocadillo = root.findViewById(R.id.bocadilloPersonajes);
        TextView textoBocadillo = bocadillo.findViewById(R.id.textoBocadillo);
        View fondoOscuro = root.findViewById(R.id.fondoOscuro);
        ImageButton btnCerrarManual = bocadillo.findViewById(R.id.btnCerrarManual);
        ImageButton btnAdelante = bocadillo.findViewById(R.id.btnAdelante);

        // Personalizar texto para Personajes
        textoBocadillo.setText(getString(R.string.texto_bocadillo_personajes));

        // Asegurar que el bocadillo sea visible
        bocadillo.setVisibility(View.VISIBLE);
        fondoOscuro.setVisibility(View.VISIBLE);

        // Configuración del botón de cierre
        btnCerrarManual.setOnClickListener(v -> mostrarDialogoCerrarManual(bocadillo, fondoOscuro));

        // Configuración del botón Adelante para cambiar a Mundos
        btnAdelante.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).getNavController().navigate(R.id.navigation_worlds);
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
                    ((MainActivity) requireActivity()).setGuiaActiva(false); // Reactivar navegación
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

}