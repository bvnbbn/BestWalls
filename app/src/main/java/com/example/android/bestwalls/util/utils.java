package com.example.android.bestwalls.util;

import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.android.bestwalls.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

/**
 * Created by Vikas on 6/26/2016.
 */
public class utils
{
    private String TAG = utils.class.getSimpleName();
    private Context context;
    private PrefManager pref;

    //constructor

    public utils(Context context)
    {
        this.context=context;
        pref= new PrefManager(context);

    }

    //getting screen width

    @SuppressWarnings("deprecation")
    public int getScreenWidth()
    {
        int columnWidth;
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        try
        {
            display.getSize(point);

        }catch(NoSuchMethodError ignore)
        {
            //older device
            point.x=display.getWidth();
            point.y= display.getHeight();

        }
        columnWidth=point.x;
        return columnWidth;
    }

    public void reportImage(Bitmap bitmap)
    {
        File myDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                pref.getGalleryName());
        myDir.mkdirs();
        Random generator = new Random();
        int n=10000;
        n=generator.nextInt(n);
        String fname = "Wallpaper-" + n+ ".jpg";
        File file = new File(myDir,fname);
        if(file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();


            Uri uri = getImageContentUri(context, file);
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setType("text/html");
            intent.setData(Uri.parse("mailto:" + "vikaskum660@gmail.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Wallpaper HD: Rport Image");
            intent.putExtra(Intent.EXTRA_TEXT, "Write your Report below...");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            Intent chooserIntent = Intent.createChooser(intent, "Report Via");
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            try {
                context.startActivity(chooserIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        catch( Exception e)
        {
            e.printStackTrace();
        }
    }

    public void shareImage(Bitmap bitmap,CoordinatorLayout coordinatorLayout)
    {
        File myDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                pref.getGalleryName());
        myDir.mkdirs();
        Random generator = new Random();
        int n=10000;
        n= generator.nextInt(n);
        String fname = "Wallpaper-"+ n+ ".jpg";
        File file = new File(myDir,fname);
        if(file.exists())
            file.delete();
        try
        {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            Uri uri = getImageContentUri(context,file);
            Intent wall_intent =  new Intent(Intent.ACTION_SEND);
            wall_intent.setType("image/*");
            wall_intent.putExtra("mimeType", "image/*");
            wall_intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            Intent chooserIntent = Intent.createChooser(wall_intent,
                    "Share Using");
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            try {
                context.startActivity(chooserIntent);
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }catch(Exception e )

        {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, context.getString(R.string.toast_wallpaper_set_failed), Snackbar.LENGTH_SHORT);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }

    }

    public void saveImageToSDcard(Bitmap bitmap,CoordinatorLayout coordinatorLayout)
    {
        File myDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),pref.getGalleryName() );
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n= generator.nextInt(n);
        String fname= "Wallpaper-"+ n + ".jpg";
        File file = new File(myDir,fname);
        if(file.exists())
        {
            file.delete();

        }
        try
        {
            FileOutputStream  out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Snackbar snackbar = Snackbar.make(coordinatorLayout, context.getString(R.string.toast_saved).replace("#", "\"" + pref.getGalleryName() + "\""), Snackbar.LENGTH_SHORT);
            View sbView = snackbar.getView();
            TextView textView = (TextView)sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
            Log.d(TAG, "Wallpaper saved to: " + file.getAbsolutePath());

        }catch(Exception e)
        {
            e.printStackTrace();
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, context.getString(R.string.toast_saved_failed), Snackbar.LENGTH_SHORT);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
    public static Uri getImageContentUri(Context context, File imageFile)
    {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    public void setAsWallpaper(Bitmap bitmap,CoordinatorLayout coordinatorLayout)
    {
        File myDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                pref.getGalleryName());

        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Wallpaper-" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            Uri uri = getImageContentUri(context,file);
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
            //Intent intent = new Intent(wallpaperManager.getCropAndSetWallpaperIntent(uri));
            //_context.startActivity(intent);
            Intent wall_intent =  new Intent(Intent.ACTION_ATTACH_DATA);
            wall_intent.setDataAndType(uri, "image/*");
            wall_intent.putExtra("mimeType", "image/*");
            Intent chooserIntent = Intent.createChooser(wall_intent,
                    "Set As");
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                context.startActivity(chooserIntent);
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, context.getString(R.string.toast_wallpaper_set_failed), Snackbar.LENGTH_SHORT);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
