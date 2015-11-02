package com.daliavi.android.music.app.music;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by telmate on 7/23/15.
 */
public class ArtistAdapter extends ArrayAdapter<ArtistData> {
    Context context;
    int layoutResourceId;
    ArrayList<ArtistData> data;

    public ArtistAdapter(Context context, int layoutResourceId, ArrayList<ArtistData> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ArtistHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ArtistHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);

            row.setTag(holder);
        }
        else
        {
            holder = (ArtistHolder)row.getTag();
        }

        ArtistData artist = data.get(position);
        holder.txtTitle.setText(artist.title);
        String url = artist.icon;

         //you probably don’t want to load them directly in your adapter’s getView() because, well, you should never ever block UI thread with IO.

        Picasso.with(context)
                .load(url)
                .into(holder.imgIcon);

        return row;
    }

    static class ArtistHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
    }
}
