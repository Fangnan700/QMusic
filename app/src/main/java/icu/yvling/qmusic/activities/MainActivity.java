package icu.yvling.qmusic.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.SeekBar;
import android.widget.Toast;

import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.List;

import icu.yvling.qmusic.App;
import icu.yvling.qmusic.R;
import icu.yvling.qmusic.adapters.MusicAdapter;
import icu.yvling.qmusic.databinding.ActivityMainBinding;
import icu.yvling.qmusic.models.Music;
import icu.yvling.qmusic.player.QPlayer;
import icu.yvling.qmusic.utils.MusicUtils;
import icu.yvling.qmusic.utils.TimeCounter;

public class MainActivity extends AppCompatActivity implements TimeCounter.CounterOwner {

    private static long lastClickTime;
    private static long interval = 500;
    private static ActivityMainBinding mainBinding;
    private static QPlayer player;
    private static Context context;

    public Event event = new Event(0L, 0L, null, null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        /* 设置状态栏颜色 */
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(getColor(R.color.transparent));

        /* 绑定视图 */
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainBinding.setEvent(event);

        /* 获取播放器对象 */
        player = App.getQPlayer();

        /* 询问是否扫描音乐 */
        startScan();

        /*
         * 按钮点击事件
         * */

        /* 播放/暂停 */
        mainBinding.playAndPauseButton.setOnClickListener(view -> {

            clickAnimator(view);

            if (!isLimited(System.currentTimeMillis())) {
                if (player.isPlaying()) {
                    player.pause();
                    mainBinding.playAndPauseButton.setImageResource(R.drawable.baseline_play_circle_24);
                } else {
                    player.start();
                    if (player.isPlaying()) {
                        mainBinding.playAndPauseButton.setImageResource(R.drawable.baseline_pause_circle_24);
                    }
                }
                mainBinding.seekBar.setMax((int) player.getDuration());
            }
        });

        /* 上一曲 */
        mainBinding.previousMusicButton.setOnClickListener(view -> {

            clickAnimator(view);

            if (!isLimited(System.currentTimeMillis())) {
                mainBinding.seekBar.setMax((int) player.getDuration());
                mainBinding.seekBar.setProgress(0);
                player.preMusic();
                if (player.isPlaying()) {
                    mainBinding.playAndPauseButton.setImageResource(R.drawable.baseline_pause_circle_24);
                }
            }
        });

        /* 下一曲 */
        mainBinding.nextMusicButton.setOnClickListener(view -> {

            clickAnimator(view);

            if (!isLimited(System.currentTimeMillis())) {
                mainBinding.seekBar.setMax((int) player.getDuration());
                mainBinding.seekBar.setProgress(0);
                player.nextMusic();
                if (player.isPlaying()) {
                    mainBinding.playAndPauseButton.setImageResource(R.drawable.baseline_pause_circle_24);
                }
            }
        });

        /* 添加音乐 */
        mainBinding.addMusicButton.setOnClickListener(view -> {

            clickAnimator(view);

            if (!isLimited(System.currentTimeMillis())) {
                startScan();
            }
        });



        /*
         * 进度条拖动事件
         * */
        mainBinding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                player.seek(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mainBinding.playAndPauseButton.setImageResource(R.drawable.baseline_play_circle_24);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(player.isPlaying()) {
                    mainBinding.playAndPauseButton.setImageResource(R.drawable.baseline_pause_circle_24);
                }
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        TimeCounter.addClient(this);
    }

    @Override
    protected void onDestroy() {
        TimeCounter.removeClient(this);
        super.onDestroy();
    }


    /*
     * 定时触发器
     * */
    @Override
    public void onCount() {
        event.getDuration().set(formatDuration(player.getDuration()));
        event.getCurrentPosition().set(formatDuration(player.getCurrentPosition()));
        event.getMusicName().set(player.getCurrentMusicName());
        event.getArtistName().set(player.getCurrentArtistName());
    }


    /*
     * 时间格式化
     * */
    public static String formatDuration(long t) {
        long seconds = t / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /*
     * 与视图交互的对象
     * */
    public static class Event {
        public final ObservableField<String> musicName;

        public final ObservableField<String> artistName;
        public final ObservableField<String> duration;
        public final ObservableField<String> currentPosition;

        public Event(Long duration, Long currentPosition, String musicName, String artistName) {
            this.duration = new ObservableField<>(formatDuration(duration));
            this.currentPosition = new ObservableField<>(formatDuration(currentPosition));
            this.musicName = new ObservableField<>(musicName);
            this.artistName = new ObservableField<>(artistName);
        }

        /* 获取当前歌曲时长 */
        public ObservableField<String> getDuration() {
            return duration;
        }

        /* 获取当前歌曲进度 */
        public ObservableField<String> getCurrentPosition() {
            return currentPosition;
        }

        /* 获取当前歌曲名字 */
        public ObservableField<String> getMusicName() {
            return musicName;
        }

        /* 获取当前歌手名字 */
        public ObservableField<String> getArtistName() {
            return artistName;
        }
    }


    /*
     * 扫描音乐文件
     * */
    public static void scanMusic(Context context) {
        if (!XXPermissions.isGranted(context, Permission.READ_MEDIA_AUDIO)) {
            XXPermissions.with(context)
                    // 申请单个权限
                    .permission(Permission.READ_MEDIA_AUDIO)
                    .request((permissions, allGranted) -> {
                        if (!allGranted) {
                            Toast.makeText(context, "获取权限失败", Toast.LENGTH_LONG).show();
                        }
                    });
        }

        /* 绑定适配器 */
        List<Music> musicList = MusicUtils.getMusicData(context);
        MusicAdapter musicAdapter = new MusicAdapter(context, R.layout.music_list, musicList);
        mainBinding.musicListView.setAdapter(musicAdapter);

        /* 扫描本地文件 */
        for (int i = 0; i < musicList.size(); i++) {
            player.add(musicList.get(i));
        }

        /* 定义点击事件 */
        mainBinding.musicListView.setOnItemClickListener((adapterView, view, i, l) -> {
            mainBinding.seekBar.setMax((int) player.getMusicList().get(i).getDuration());
            mainBinding.seekBar.setProgress(0);
            player.load(i);
            player.start();
            if (player.isPlaying()) {
                mainBinding.playAndPauseButton.setImageResource(R.drawable.baseline_pause_circle_24);
            }
            mainBinding.seekBar.setMax((int) player.getDuration());
        });
    }


    /*
     * 点击速率限制
     * */
    public static boolean isLimited(long currentTime) {
        if (currentTime - lastClickTime < interval) {
            Toast.makeText(context, "点太快啦！！！", Toast.LENGTH_SHORT).show();
            return true;
        }
        lastClickTime = currentTime;
        return false;
    }


    /*
     * 确认是否扫描本地文件
     * */
    public static void startScan() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("是否扫描本地文件？");
        builder.setPositiveButton("是", (dialog, which) -> {
            scanMusic(context);
            scanMusic(context);
        });
        builder.setNegativeButton("否", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    /*
    * 点击动画
    * */
    public static void clickAnimator(View v) {
        // 使用 ViewPropertyAnimator 定义缩放动画
        v.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(200)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        // 在动画结束时恢复原始大小
                        v.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(100)
                                .start();
                    }
                })
                .start();
    }


}