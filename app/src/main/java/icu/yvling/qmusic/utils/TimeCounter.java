package icu.yvling.qmusic.utils;

import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;

public class TimeCounter {

    /* 用户列表 */
    private static volatile ArrayList<CounterOwner> counterOwners = new ArrayList<>();

    /* 添加用户 */
    public static void addClient(CounterOwner owner) {
        counterOwners.add(owner);
    }

    /* 移除用户 */
    public static void removeClient(CounterOwner owner) {
        counterOwners.remove(owner);
    }

    /* 初始化触发器 */
    public static void init() {
        /* 使用子线程循环调用Activity的onCount方法，实现监听播放器的功能 */
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(26);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                for (CounterOwner counterOwner : counterOwners) {
                    counterOwner.onCount();
                }
            }
        }).start();
    }


    /* 每个Activity需要定时访问服务时，必须实现此接口 */
    public interface CounterOwner {
        void onCount();
    }
}
