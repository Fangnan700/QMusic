package icu.yvling.qmusic.player;

import android.media.MediaPlayer;

import java.io.IOException;
import java.util.ArrayList;

import icu.yvling.qmusic.models.Music;

public class QPlayer {
    private volatile MediaPlayer player;
    private boolean isNotFirst;
    private ArrayList<Music> musicList;
    private int currentMusicIndex = 0;

    /* 获取是否在播放 */
    public boolean isPlaying() {
        return player.isPlaying();
    }

    /* 获取播放列表 */
    public ArrayList<Music> getMusicList() {
        return musicList;
    }

    /* 获取总时长 */
    public synchronized long getDuration() {
        try {
            return player.getDuration();
        } catch (Throwable t) {
            return 0;
        }
    }

    /* 获取当前进度 */
    public synchronized long getCurrentPosition() {
        try {
            return player.getCurrentPosition();
        } catch (Throwable t) {
            return 0;
        }
    }

    /* 初始化播放器 */
    public void init() {
        player = new MediaPlayer();
        musicList = new ArrayList<>();
        isNotFirst = false;
    }

    /* 添加音乐 */
    public synchronized void add(Music music) {
        musicList.add(music);
    }

    /* 加载音乐 */
    public synchronized void load(int musicIndex) {
        if (musicList.get(musicIndex) == null) {
            return;
        }
        currentMusicIndex = musicIndex;
        player.reset();
        try {
            player.setDataSource(musicList.get(musicIndex).getSourcePath());
            player.prepare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /* 播放音乐 */
    public synchronized void start() {
        if(musicList.size() > 0) {
            if (isNotFirst) {
                player.start();
            } else {
                isNotFirst = true;
                load(currentMusicIndex);
                start();
            }
        }
    }

    /* 暂停音乐 */
    public synchronized void pause() {
        if (player.isPlaying()) {
            player.pause();
        }
    }

    /* 停止音乐 */
    public synchronized void stop() {
        if (player.isPlaying()) {
            player.stop();
            load(currentMusicIndex);
        }
    }

    /* 跳转音乐 */
    public synchronized void seek(long pos) {
        if(player.isPlaying()) {
            player.seekTo((int) pos);
        } else {
            player.start();
            player.seekTo((int) pos);
        }
    }

    /* 上一首 */

    public synchronized void preMusic() {
        if(musicList.size() > 0) {
            if (currentMusicIndex <= 0) {
                currentMusicIndex = musicList.size() - 1;
            } else {
                currentMusicIndex -= 1;
            }
            reset();
            load(currentMusicIndex);
            start();
        }
    }

    /* 下一首 */
    public synchronized void nextMusic() {
        if(musicList.size() > 0) {
            if (currentMusicIndex >= musicList.size() - 1) {
                currentMusicIndex = 0;
            } else {
                currentMusicIndex += 1;
            }
            load(currentMusicIndex);
            start();
        }
    }

    /* 获取当前音乐名字 */
    public synchronized String getCurrentMusicName() {
        try {
            return musicList.get(currentMusicIndex).getMusicName();
        } catch (Throwable t) {
            return "QMusic";
        }
    }

    /* 获取当前歌手名字 */
    public synchronized String getCurrentArtistName() {
        try {
            return musicList.get(currentMusicIndex).getArtistName();
        } catch (Throwable t) {
            return "喻灵";
        }
    }

    /* 重置播放器 */
    public synchronized void reset() {
        player.reset();
    }
}
