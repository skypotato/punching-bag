package org.skypotato.punchingbag;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private GameView mGameView;
    private GameView.GameThread mGameThread;
    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGameView = (GameView) findViewById(R.id.gameView);
        mGameThread = mGameView.mThread;

        player = MediaPlayer.create(this, R.raw.bgm_back);
        player.setVolume(0.3f, 0.3f);        // 볼륨 설정
        player.setLooping(true);              // 반복 연주
        player.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        player.start();
        mGameThread.resumeThread();
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.pause();
        mGameThread.pauseThread();
    }


}
