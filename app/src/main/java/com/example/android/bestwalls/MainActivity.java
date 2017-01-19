package com.example.android.bestwalls;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.bestwalls.app.AppController;
import com.example.android.bestwalls.picasa.model.Category;

import java.util.ArrayList;
import java.util.List;

//import android.support.v4.app.Fragment;



public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private List<Category> albumsList;
    private ArrayList<NavDrawerItem> navDrawerItems;


    String name = new String("Main Grid Screen");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       //MultiplePermissionsActivity p=new MultiplePermissionsActivity();

       // Intent p = new Intent (getApplicationContext(),MultiplePermissionsActivity.class);
        //startActivity(p);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // Getting the albums from shared preferences
        albumsList = AppController.getInstance().getPrefManager().getCategories();

        // Insert "Recently Added" in navigation drawer first position
        //Category recentAlbum = new Category(null, getString(R.string.nav_drawer_recently_added), "(100)");

      //  albumsList.add(0, recentAlbum);

        // Loop through albums in add them to navigation drawer adapter
        for (Category a : albumsList) {
            navDrawerItems.add(new NavDrawerItem(true, a.getId(), a.getTitle(), a.getPhotoNo()));
            // titles a.getTitle()
        }
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        displayView(0);


    }

    private void displayView(int position) {
        // update the main content by replacing fragments

        Fragment fragment = null;
        String albumId="";
        switch (position) {
            case 0:
                // Recently added item selected
                // don't pass album id to grid fragment
             //   Log.e(TAG, "GridFragment is creating");
                albumId=albumsList.get(position).getId();
                fragment = GridFragement.newInstance(albumId);
                break;

            default:
                // selected wallpaper category
                // send album id to grid fragment to list all the wallpapers
                albumId = albumsList.get(position).getId();
                fragment = GridFragement.newInstance(albumId);
                break;
        }

        if (fragment != null) {

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
            // set the toolbar title
            getSupportActionBar().setTitle(albumsList.get(position).getTitle());
        } else {
            // error in creating fragment
            Log.e(TAG, "Error in creating fragment");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this,
                    SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

}