package icu.yvling.qmusic;

import android.app.Application;

import icu.yvling.qmusic.player.QPlayer;
import icu.yvling.qmusic.utils.TimeCounter;

public class App extends Application {
    private static QPlayer qPlayer;

    @Override
    public void onCreate() {
        super.onCreate();

        TimeCounter.init();
        qPlayer = new QPlayer();
        qPlayer.init();


    }

    public static QPlayer getQPlayer() {
        return qPlayer;
    }
}
