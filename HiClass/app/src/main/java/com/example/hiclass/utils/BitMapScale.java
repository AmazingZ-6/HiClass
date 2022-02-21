package com.example.hiclass.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class BitMapScale {

    public static Bitmap imageCompress(Bitmap oldBitmap, int screenW, int screenH) {
        Bitmap newBitmap = oldBitmap;
        int width = oldBitmap.getWidth();//读取旧图的宽度
        int height = oldBitmap.getHeight();//读取旧图的高度
        float scaleWidth;//缩放宽度
        float scaleHeight;//缩放高度
        int number = 100;//缩放比例,原图的百分比
        Matrix matrix = new Matrix();
        int newWidth = width;//新图的宽度，初始取原图
        int newHeight = height;//新图的高度，初始取原图
        while (newWidth > screenW || newHeight > screenH) {
            scaleWidth = (float) ((number * width * 0.01) / width);//按百分比进行缩放
            scaleHeight = (float) ((number * height * 0.01) / height);//同上
            matrix.postScale(scaleWidth, scaleHeight);
            newBitmap = Bitmap.createBitmap(oldBitmap, 0, 0, width, height, matrix, true);//根据缩放比例获取新图
            newWidth = newBitmap.getWidth();//获取新图的宽度
            newHeight = newBitmap.getHeight();//获取新图的高度
            number -= 10;//缩放比例减小
        }
        return newBitmap;
    }
}
