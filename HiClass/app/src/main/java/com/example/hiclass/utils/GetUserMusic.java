package com.example.hiclass.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.example.hiclass.data_class.MusicBean;

import java.util.ArrayList;
import java.util.List;


public class GetUserMusic {
    public static List<MusicBean> musics = new ArrayList<MusicBean>();

    public static List<MusicBean> getAllSongs(Context context) {
        musics.clear();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                MediaStore.Audio.Media.IS_MUSIC, null, MediaStore.Audio.Media.IS_MUSIC);

        if (cursor != null) {
            MusicBean song;
            while (cursor.moveToNext()) {
                song = new MusicBean();
                int i = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                song.setTitle(cursor.getString(i));
                i = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                song.setSinger(cursor.getString(i));
                i = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
                song.setFileName(cursor.getString(i));
                song.setFileUrl(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                musics.add(song);
            }
            cursor.close();
        }
        return musics;
    }

}



