package dam.pmdm.spyrothedragon;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class EasterEggVideoActivity extends Activity {

    private MediaPlayer mediaPlayer;
    private SurfaceView surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easter_egg_video);

        surfaceView = findViewById(R.id.surfaceView);
        SurfaceHolder holder = surfaceView.getHolder();

        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                playVideo(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {}
        });
    }

    private void playVideo(SurfaceHolder holder) {
        try {
            mediaPlayer = new MediaPlayer();
            Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.easter_egg);
            mediaPlayer.setDataSource(this, videoUri);
            mediaPlayer.setDisplay(holder);
            mediaPlayer.setOnPreparedListener(mp -> {
                Log.d("EasterEggVideoActivity", "🎬 Video preparado, iniciando reproducción...");
                mediaPlayer.start();
            });
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e("EasterEggVideoActivity", "❌ Error al reproducir el video: " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
