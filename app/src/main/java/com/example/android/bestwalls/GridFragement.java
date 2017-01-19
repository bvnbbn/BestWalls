package com.example.android.bestwalls;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.bestwalls.app.AppConst;
import com.example.android.bestwalls.app.AppController;
import com.example.android.bestwalls.helper.GridViewAdapter;
import com.example.android.bestwalls.picasa.model.Wallpaper;
import com.example.android.bestwalls.util.PrefManager;
import com.example.android.bestwalls.util.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vikas on 7/5/2016.
 */
public class GridFragement extends Fragment

{
    private static  final String TAG= GridFragement.class.getSimpleName();
    private utils util;
    private GridViewAdapter adapter;
    private GridView gridView;
    private int columnWidth;
    private static final String bundleAlbumId="albumId";
    private String selectedAlbumId;
    private List<Wallpaper> photoList;
    private ProgressBar pbLoader;
    private PrefManager pref;


    //Picasa JSON response node keys

    private static final String TAG_FEED ="feed",TAG_ENTRY ="entry",
    TAG_MEDIA_GROUP = "media$group",
    TAG_MEDIA_CONTENT ="media$content",TAG_IMG_URL="url",
    TAG_IMG_WIDTH="width",TAG_IMG_HEIGHT="height",
    TAG_ID="id",
    TAG_T="$t";

    public GridFragement()
    {}

    public static GridFragement newInstance(String albumID)
    {
        GridFragement f = new GridFragement();
        Bundle args = new Bundle();
        args.putString(bundleAlbumId, albumID);
        f.setArguments(args);
        return f;
    }


    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState)
    {
        photoList = new ArrayList<Wallpaper>();
        pref=new PrefManager(getActivity());

        //getting Album Id of the item selcted in NAvigation drawer
        //if Album Id is null user is selected recently addedoption


        if(getArguments().getString(bundleAlbumId)!=null)
        {
            selectedAlbumId=getArguments().getString(bundleAlbumId);

        }
        else
        {
            Log.d(TAG,"Selected album id:"+ getArguments().getString(bundleAlbumId));
            selectedAlbumId = null;

        }

        //preparing the request url
        String url = null;
        if(selectedAlbumId == null)
        {
            // Recently added album url
            url = AppConst.URL_RECENTLY_ADDED.replace("_PICASA_USER_",
                    pref.getGoogleUsername());

        }
        else
        {
            //select an album replace the Album Id in the url

            url = AppConst.URL_ALBUM_PHOTOS.replace("_PICASA_USER_",
                    pref.getGoogleUsername()).replace("_ALBUM_ID_",selectedAlbumId);


        }

        Log.d(TAG,"Final request url"+ url);
        View rootView = inflater.inflate(R.layout.fragment_grid,container,false);
        //Hiding the gridview and showing laoder image before making the
         //http request
        gridView=(GridView)rootView.findViewById(R.id.grid_view);
        gridView.setVisibility(View.GONE);
        pbLoader=(ProgressBar)rootView.findViewById(R.id.pbLoader);
        pbLoader.setVisibility(View.VISIBLE);
        util = new utils(getActivity());


        //Making volleys json object request to fetch list of photos
        //of an album

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "List of photos json response:" + response.toString());
                try {
                    //parsing the json response
                    JSONArray entry = response.getJSONObject(TAG_FEED).getJSONArray(TAG_ENTRY);

                    //looping through each photo and adding it to the list

                    for (int i = 0; i < entry.length(); i++) {
                        JSONObject photoObj = (JSONObject) entry.get(i);
                        JSONArray mediacontentArry = photoObj.getJSONObject(TAG_MEDIA_GROUP)
                                .getJSONArray(TAG_MEDIA_CONTENT);

                        if (mediacontentArry.length() > 0) {

                            JSONObject mediaObj = (JSONObject) mediacontentArry.get(0);


                            String url = mediaObj.getString(TAG_IMG_URL);


                            String photoJson = photoObj.getJSONObject(TAG_ID).getString(TAG_T) + "&imgmax=d";

                            int width = mediaObj.getInt(TAG_IMG_WIDTH);
                            int height = mediaObj
                                    .getInt(TAG_IMG_HEIGHT);

                            Wallpaper p = new Wallpaper(photoJson, url, width,
                                    height);

                            // Adding the photo to list data set
                            photoList.add(p);

                            Log.d(TAG, "Photo: " + url + ", w: "
                                    + width + ", h: " + height);
                        }
                    }
                    // Notify list adapter about dataset changes. So
                    // that it renders grid again
                    adapter.notifyDataSetChanged();

                    // Hide the loader, make grid visible
                    pbLoader.setVisibility(View.GONE);
                    gridView.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(),
                            getString(R.string.msg_unknown_error),
                            Toast.LENGTH_LONG).show();
                }
            }

        },new Response.ErrorListener(){
            @Override

            public void onErrorResponse(VolleyError error)
            {
                Log.e(TAG,"Error"+error.getMessage());
                //unable to fetch wallpapers

                //either google username is wrong or devices doesn't have internet connection

                Toast.makeText(getActivity(),getString(R.string.msg_wall_fetch_error),Toast.LENGTH_LONG).show();

            }

        });

        //Remove the url from cache

        AppController.getInstance().getRequestQueue().getCache().remove(url);


        //Disable the cache for this url so that it always fetched updated
        //json
        jsonObjectRequest.setShouldCache(false);


        //Adding the request to request queeu

        AppController.getInstance().addToRequestQueue(jsonObjectRequest);


        //Initializing the grid view

        InitilizeGridLayout();


        //GridView Adapter

        adapter = new GridViewAdapter(getActivity(),photoList,columnWidth);

        //setting the grid view adapter

        gridView.setAdapter(adapter);


        //Grid item selecet listener
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
               // selecting the grid image we launch fullscreen activity

                Intent i = new Intent(getActivity(),
                      FullScreenViewActivity.class);

                // Passing selected image to fullscreen activity
                Wallpaper photo = photoList.get(position);

                i.putExtra(FullScreenViewActivity.TAG_SEL_IMAGE, photo);
                startActivity(i);

            }
        });


    return rootView;


    }

    /*Method to calculate the grrid dimensions calculates number columns
    and columns with in grid

     */

    private void InitilizeGridLayout() {
        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                AppConst.GRID_PADDING, r.getDisplayMetrics());

        // Column width
        columnWidth = (int) ((util.getScreenWidth() - ((pref
                .getNoOfGridColumns() + 1) * padding)) / pref
                .getNoOfGridColumns());

        // Setting number of grid columns
        gridView.setNumColumns(pref.getNoOfGridColumns());
        gridView.setColumnWidth(columnWidth);
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setPadding((int) padding, (int) padding, (int) padding,
                (int) padding);

        // Setting horizontal and vertical padding
        gridView.setHorizontalSpacing((int) padding);
        gridView.setVerticalSpacing((int) padding);
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
