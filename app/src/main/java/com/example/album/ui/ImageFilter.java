package com.example.album.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;


import java.util.Random;

public class ImageFilter {
    public static String[] filter_values= {"NONE","GRAY","INVERT","CONTRAST","TINT","RELIEF","SNOW","OLD","SHADING","CORNER","BRIGHTNESS"};
    public static String[] auto_filter_values= {"SNOW","CONTRAST","TINT","TINT","RELIEF","OLD","CORNER","TINT","BRIGHTNESS","TINT"};

    public static Bitmap applyFilter(Bitmap bitmap, String filter, Object... options) {
        switch (filter){
            case "NONE":
                return bitmap;
            case "GRAY":
                return changeToGray(bitmap);
            case "CONTRAST":
                if (options.length < 1) {
                    return changeToContrast(bitmap, 50);
                }
                return changeToContrast(bitmap, (Integer)options[0]);
            case "BRIGHTNESS":
                if (options.length < 1) {
                    return changeToBrightness(bitmap, 70);
                }
                return changeToBrightness(bitmap, (Integer)options[0]);
            case "CORNER":
                if (options.length < 1) {
                    return roundCorner(bitmap, 200);
                }
                return roundCorner(bitmap, (Integer)options[0]);
            case "TINT":
                if (options.length < 1) {
                    return tintImage(bitmap, 40);
                }
                return tintImage(bitmap, (Integer)options[0]);
            case "SNOW":
                return applySnowEffect(bitmap);
            case "INVERT":
                return chageToInvert(bitmap);
            case "OLD":
                return changeToOld(bitmap);
            case "RELIEF":
                if (options.length < 1) {
                    return changeToRelief(bitmap, -1234567);
                }
                return changeToRelief(bitmap, (Integer)options[0] - 100000);
            case "SHADING":
                if (options.length < 1) {
                    return changeToShading(bitmap, 10);
                }
                return changeToShading(bitmap, (Integer)options[0]);

        }
        return bitmap;
    }
    public static Bitmap tintImage(Bitmap src, int degree) {
        double PI = 3.14159d;
        double FULL_CIRCLE_DEGREE = 360d;
        double HALF_CIRCLE_DEGREE = 180d;
        double RANGE = 256d;

        int width = src.getWidth();
        int height = src.getHeight();

        int[] pix = new int[width * height];
        src.getPixels(pix, 0, width, 0, 0, width, height);

        int RY, GY, BY, RYY, GYY, BYY, R, G, B, Y;
        double angle = (PI * (double)degree) / HALF_CIRCLE_DEGREE;

        int S = (int)(RANGE * Math.sin(angle));
        int C = (int)(RANGE * Math.cos(angle));

        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                int index = y * width + x;
                int r = ( pix[index] >> 16 ) & 0xff;
                int g = ( pix[index] >> 8 ) & 0xff;
                int b = pix[index] & 0xff;
                RY = ( 70 * r - 59 * g - 11 * b ) / 100;
                GY = (-30 * r + 41 * g - 11 * b ) / 100;
                BY = (-30 * r - 59 * g + 89 * b ) / 100;
                Y  = ( 30 * r + 59 * g + 11 * b ) / 100;
                RYY = ( S * BY + C * RY ) / 256;
                BYY = ( C * BY - S * RY ) / 256;
                GYY = (-51 * RYY - 19 * BYY ) / 100;
                R = Y + RYY;
                R = ( R < 0 ) ? 0 : (( R > 255 ) ? 255 : R );
                G = Y + GYY;
                G = ( G < 0 ) ? 0 : (( G > 255 ) ? 255 : G );
                B = Y + BYY;
                B = ( B < 0 ) ? 0 : (( B > 255 ) ? 255 : B );
                pix[index] = 0xff000000 | (R << 16) | (G << 8 ) | B;
            }

        Bitmap outBitmap = Bitmap.createBitmap(width, height, src.getConfig());
        outBitmap.setPixels(pix, 0, width, 0, 0, width, height);

        pix = null;

        return outBitmap;
    }
    public static Bitmap roundCorner(Bitmap src, float round) {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create bitmap output
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // set canvas for painting
        Canvas canvas = new Canvas(result);
        canvas.drawARGB(0, 0, 0, 0);

        // config paint
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);

        // config rectangle for embedding
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        // draw rect to canvas
        canvas.drawRoundRect(rectF, round, round, paint);

        // create Xfer mode
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        // draw source image to canvas
        canvas.drawBitmap(src, rect, rect, paint);

        // return final image
        return result;
    }
    private static Bitmap changeToOld(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int pixColor = 0;
        int pixR = 0;
        int pixG = 0;
        int pixB = 0;
        int newR = 0;
        int newG = 0;
        int newB = 0;
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < height; i++)
        {
            for (int k = 0; k < width; k++)
            {
                pixColor = pixels[width * i + k];
                pixR = Color.red(pixColor);
                pixG = Color.green(pixColor);
                pixB = Color.blue(pixColor);
                newR = (int) (0.393 * pixR + 0.769 * pixG + 0.189 * pixB);
                newG = (int) (0.349 * pixR + 0.686 * pixG + 0.168 * pixB);
                newB = (int) (0.272 * pixR + 0.534 * pixG + 0.131 * pixB);
                int newColor = Color.argb(255, newR > 255 ? 255 : newR, newG > 255 ? 255 : newG, newB > 255 ? 255 : newB);
                pixels[width * i + k] = newColor;
            }
        }

        Bitmap returnBitmap = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
        return returnBitmap;
    }

    private static Bitmap chageToInvert(Bitmap bitmap) {
        // create new bitmap with the same settings as source bitmap
        Bitmap bmOut = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        // color info
        int A, R, G, B;
        int pixelColor;
        // image size
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        // scan through every pixel
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                // get one pixel
                pixelColor = bitmap.getPixel(x, y);
                // saving alpha channel
                A = Color.alpha(pixelColor);
                // inverting byte for each R/G/B channel
                R = 255 - Color.red(pixelColor);
                G = 255 - Color.green(pixelColor);
                B = 255 - Color.blue(pixelColor);
                // set newly-inverted pixel to output image
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        // return final bitmap
        return bmOut;
    }

public static Bitmap applySnowEffect(Bitmap source) {
    // get image size
    int width = source.getWidth();
    int height = source.getHeight();
    int[] pixels = new int[width * height];
    // get pixel array from source
    source.getPixels(pixels, 0, width, 0, 0, width, height);
    // random object
    Random random = new Random();

    int R, G, B, index = 0, thresHold = 50;
    // iteration through pixels
    for(int y = 0; y < height; ++y) {
        for(int x = 0; x < width; ++x) {
            // get current index in 2D-matrix
            index = y * width + x;
            // get color
            R = Color.red(pixels[index]);
            G = Color.green(pixels[index]);
            B = Color.blue(pixels[index]);
            // generate threshold
            thresHold = random.nextInt(255);
            if(R > thresHold && G > thresHold && B > thresHold) {
                pixels[index] = Color.rgb(255, 255, 255);
            }
        }
    }
    // output bitmap
    Bitmap bmOut = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
    bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
    return bmOut;
}
public static Bitmap changeToBrightness(Bitmap src, int value) {
    // image size
    int width = src.getWidth();
    int height = src.getHeight();
    // create output bitmap
    Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
    // color information
    int A, R, G, B;
    int pixel;

    // scan through all pixels
    for(int x = 0; x < width; ++x) {
        for(int y = 0; y < height; ++y) {
            // get pixel color
            pixel = src.getPixel(x, y);
            A = Color.alpha(pixel);
            R = Color.red(pixel);
            G = Color.green(pixel);
            B = Color.blue(pixel);

            // increase/decrease each channel
            R += value;
            if(R > 255) { R = 255; }
            else if(R < 0) { R = 0; }

            G += value;
            if(G > 255) { G = 255; }
            else if(G < 0) { G = 0; }

            B += value;
            if(B > 255) { B = 255; }
            else if(B < 0) { B = 0; }

            // apply new pixel color to output bitmap
            bmOut.setPixel(x, y, Color.argb(A, R, G, B));
        }
    }
    // return final image
    return bmOut;
    }
    private static Bitmap changeToRelief(Bitmap bitmap, int value) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
//
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        int index = 0;
        // iteration through pixels
        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
                // get current index in 2D-matrix
                index = y * width + x;
                // AND
                pixels[index] &= (value);
            }
        }
        // output bitmap
        Bitmap returnBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        returnBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return returnBitmap;
    }
    public static Bitmap changeToContrast(Bitmap src, double value) {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;
        // get contrast value
        double contrast = Math.pow((100 + value) / 100, 2);

        // scan through all pixels
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                // apply filter contrast for every channel R, G, B
                R = Color.red(pixel);
                R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(R < 0) { R = 0; }
                else if(R > 255) { R = 255; }

                G = Color.red(pixel);
                G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(G < 0) { G = 0; }
                else if(G > 255) { G = 255; }

                B = Color.red(pixel);
                B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(B < 0) { B = 0; }
                else if(B > 255) { B = 255; }

                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        // return final image
        return bmOut;
    }
    private static Bitmap changeToGray(Bitmap bitmap) {
        int width, height;
        width = bitmap.getWidth();
        height = bitmap.getHeight();

        Bitmap grayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(grayBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);

        paint.setColorFilter(filter);
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return grayBitmap;
    }
    public static Bitmap changeToShading(Bitmap source, int shadingColor) {
        // get image size
        int width = source.getWidth();
        int height = source.getHeight();
        int[] pixels = new int[width * height];
        // get pixel array from source
        source.getPixels(pixels, 0, width, 0, 0, width, height);

        int index = 0;
        // iteration through pixels
        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
                // get current index in 2D-matrix
                index = y * width + x;
                // AND
                pixels[index] &= shadingColor;
            }
        }
        // output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
        return bmOut;
    }
}
