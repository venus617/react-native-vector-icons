package com.oblador.vectoricons;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.Rect;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

import com.facebook.react.views.text.ReactFontManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class VectorIconsModuleImpl {

    public static final String NAME = "RNVectorIcons";

    private static final Map<String, Typeface> sTypefaceCache = new HashMap<String, Typeface>();

    public static String getImageForFont(String fontFamily, String glyph, Integer fontSize, Integer color, Context context) throws java.io.IOException, FileNotFoundException {
        File cacheFolder = context.getCacheDir();
        String cacheFolderPath = cacheFolder.getAbsolutePath() + "/";

        float scale = context.getResources().getDisplayMetrics().density;
        String scaleSuffix = "@" + (scale == (int) scale ? Integer.toString((int) scale) : Float.toString(scale)) + "x";
        int size = Math.round(fontSize*scale);
        String cacheKey = fontFamily + ":" + glyph + ":" + color;
        String hash = Integer.toString(cacheKey.hashCode(), 32);
        String cacheFilePath = cacheFolderPath + hash + "_" + Integer.toString(fontSize) + scaleSuffix + ".png";
        String cacheFileUrl = "file://" + cacheFilePath;
        File cacheFile = new File(cacheFilePath);

        if(cacheFile.exists()) {
            return cacheFileUrl;
        }

        FileOutputStream fos = null;
        Typeface typeface = ReactFontManager.getInstance().getTypeface(fontFamily, 0, context.getAssets());
        Paint paint = new Paint();
        paint.setTypeface(typeface);
        paint.setColor(color);
        paint.setTextSize(size);
        paint.setAntiAlias(true);
        Rect textBounds = new Rect();
        paint.getTextBounds(glyph, 0, glyph.length(), textBounds);

        int offsetX = 0;
        int offsetY = size - (int) paint.getFontMetrics().bottom;

        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(glyph, offsetX, offsetY, paint);

        try {
            fos = new FileOutputStream(cacheFile);
            bitmap.compress(CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            fos = null;

            return cacheFileUrl;
        }
        finally {
            if (fos != null) {
                try {
                    fos.close();
                    fos = null;
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
