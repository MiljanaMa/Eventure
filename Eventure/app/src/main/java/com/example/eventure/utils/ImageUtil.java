package com.example.eventure.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtil {

    public static String encodeImageToBase64(Uri imageUri, Context context) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            Bitmap resizedBitmap = resizeBitmap(bitmap, 800);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);

            byte[] bytes = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap resizeBitmap(Bitmap image, int maxWidth) {
        int width = image.getWidth();
        int height = image.getHeight();
        float aspectRatio = (float) width / (float) height;
        int newWidth = maxWidth;
        int newHeight = (int) (newWidth / aspectRatio);
        return Bitmap.createScaledBitmap(image, newWidth, newHeight, true);
    }

    public static Bitmap decodeBase64ToImage(String imageBase64) {
        byte[] decodedBytes = Base64.decode(imageBase64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
