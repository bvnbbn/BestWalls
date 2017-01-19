package com.example.android.bestwalls.helper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bestwalls.NavDrawerItem;
import com.example.android.bestwalls.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by Vikas on 7/1/2016.
 *
 *  This adapter class inflates the
 *  nav_drawer_row.xml layout by displaying appropriate
 *  wallpaper category name and the number of photos in
 *  that category.
 */
public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {
    List<NavDrawerItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    Context context;

    public NavigationDrawerAdapter(Context context, List<NavDrawerItem> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;


    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.nav_drawer_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NavDrawerItem current = data.get(position);
        holder.title.setText(current.getTitle());
        if (position == 0) {
            holder.photocount.setText("(100)");

        } else {
            holder.photocount.setText("(" + current.getPhotoNo() + ")");
        }
    }

    @Override
    public int getItemCount() {
        return data.size();

    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView photocount;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            photocount = (TextView) itemView.findViewById(R.id.photocount);

        }

    }
}
