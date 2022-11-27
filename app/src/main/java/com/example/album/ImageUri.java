package com.example.album;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ImageUri {
    public static Uri getImageUri(@NonNull Context inContext, @NonNull Bitmap inImage){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @SuppressLint("NewApi")
    public static Bitmap blurRenderScript(Context context,Bitmap smallBitmap, int radius) {
        try {
            smallBitmap = RGB565toARGB888(smallBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap bitmap = Bitmap.createBitmap(
                smallBitmap.getWidth(), smallBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(context);

        Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
                Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(radius); // radius must be 0 < r <= 25
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();

        return bitmap;
    }

    private static Bitmap RGB565toARGB888(Bitmap img) throws Exception {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        //Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }

    public static Uri saveImage(Context context, Bitmap bitmap, String directory)
            throws FileNotFoundException {
        // Create an image file name
        Uri imageUri;
        String timeStamp = Long.toString(System.currentTimeMillis());
        String imageFileName = "JPEG_" + timeStamp + "_";
        OutputStream fos;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            ContentResolver resolver = context.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName + ".jpg");
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
            contentValues.put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES
                            + "/"
                            + context.getString(R.string.app_name)
                            + "/"
                            + directory
            );
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            fos = resolver.openOutputStream(imageUri);
        }else{
            String imagesDir = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .toString();
            File image = new File(
                    imagesDir
                            + "/"
                            +context.getString(R.string.app_name)
                            + "/"
                            + directory,
                    imageFileName + ".jpg"
            );
            fos = new FileOutputStream(image);
            imageUri = Uri.fromFile(image);
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageUri;
    }
}
