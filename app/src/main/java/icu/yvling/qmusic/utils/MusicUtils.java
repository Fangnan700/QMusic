package icu.yvling.qmusic.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

import icu.yvling.qmusic.models.Music;

public class MusicUtils {

    public static List<Music> getMusicData(Context context) {
        List<Music> list = new ArrayList<Music>();
        // 媒体库查询语句
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                if(
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)) != null &&
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)) != null &&
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)) != null
                ) {
                    Music music = new Music();
                    music.setMusicName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)).strip());        //歌曲名称
                    music.setArtistName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)).strip());      //歌手
                    music.setSourcePath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)).strip());        //歌曲路径
                    music.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                    music.setDuration(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));

//                song.album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));//专辑名
//                song.size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));//歌曲大小

                    if (music.getMusicName().contains("-")) {
                        String[] str = music.getMusicName().split("-");
                        music.setArtistName(str[0]);
                        music.setMusicName(str[1]);
                    }
                    list.add(music);
                }
            }
            // 释放资源
            cursor.close();
        }
        return list;
    }
}
