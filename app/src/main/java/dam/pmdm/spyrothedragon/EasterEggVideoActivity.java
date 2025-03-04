package dam.pmdm.spyrothedragon;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class EasterEggVideoActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private SurfaceView surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easter_egg_video);

        inicializarSurfaceView();
    }

    /**
     * Configura el SurfaceView y su Callback.
     */
    private void inicializarSurfaceView() {
        surfaceView = findViewById(R.id.surfaceView);
        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                iniciarReproduccion(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                liberarMediaPlayer();
            }
        });
    }

    /**
     * Inicia la reproducción del video.
     *
     * @param holder SurfaceHolder donde se mostrará el video.
     */
    private void iniciarReproduccion(SurfaceHolder holder) {
        liberarMediaPlayer(); // Asegura que no haya un MediaPlayer previo

        try {
            mediaPlayer = new MediaPlayer();
            Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.easter_egg);
            mediaPlayer.setDataSource(this, videoUri);
            mediaPlayer.setDisplay(holder);
            mediaPlayer.setOnPreparedListener(mp -> {
                Log.d("EasterEggVideoActivity", "🎬 Video preparado, iniciando reproducción...");
                mediaPlayer.start();
            });
            mediaPlayer.setOnCompletionListener(mp -> Log.d("EasterEggVideoActivity", "✅ Video finalizado."));
            mediaPlayer.prepareAsync();
        } catch (IOException | IllegalStateException e) {
            Log.e("EasterEggVideoActivity", "❌ Error al reproducir el video", e);
        }
    }

    /**
     * Libera los recursos del MediaPlayer de manera segura.
     */
    private void liberarMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
            } catch (Exception e) {
                Log.e("EasterEggVideoActivity", "⚠️ Error al liberar MediaPlayer", e);
            } finally {
                mediaPlayer = null;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        liberarMediaPlayer();
    }
}
