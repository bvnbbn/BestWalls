package com.example.android.bestwalls.app;

/**
 * Created by Vikas on 6/26/2016.
 * All the app configuration like google username ,picasa api urls,gallery
 * directory name and other static variables goes in this class
 */
public class AppConst
{
    // Number of columns of Grid View
    // by default 2 but user can configure this in settings activity
    public static final int NUM_OF_COLUMNS = 2;

    // Gridview image padding
    public static final int GRID_PADDING = 0; // in dp

    // Gallery directory name to save wallpapers
    public static final String SDCARD_DIR_NAME = "BestWalls";

    // Picasa/Google web album username
    public static final String PICASA_USER = "vikaskum660";
  //  public static final       _ALBUM_ID =6301537682785172209;


    // Public albums list url
    public static final String URL_PICASA_ALBUMS = "https://picasaweb.google.com/data/feed/api/user/_PICASA_USER_?kind=album&alt=json";

    // Picasa album photos url
    public static final String URL_ALBUM_PHOTOS =  "https://picasaweb.google.com/data/feed/api/user/_PICASA_USER_/albumid/_ALBUM_ID_?alt=json";

    // Picasa recenlty added photos url
    public static final String URL_RECENTLY_ADDED = "https://picasaweb.google.com/data/feed/api/user/_PICASA_USER_?kind=photo&alt=json";
}


