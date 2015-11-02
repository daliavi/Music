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
 * Created by telmate on 8/25/15.
 */
public class TrackAdapter extends ArrayAdapter<TrackData>  {
    Context context;
    int layoutResourceId;
    ArrayList<TrackData> data;

    public TrackAdapter(Context context, int layoutResourceId, ArrayList<TrackData> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TrackHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new TrackHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.imgAlbumIcon);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTrackName);
            holder.txtAlbum = (TextView)row.findViewById(R.id.txtAlbumName);

            row.setTag(holder);
        }
        else
        {
            holder = (TrackHolder)row.getTag();
        }

        TrackData track = data.get(position);
        holder.txtTitle.setText(track.track_title);
        holder.txtAlbum.setText(track.album_title);
        String url = track.track_icon;

        //you probably don’t want to load them directly in your adapter’s getView() because, well, you should never ever block UI thread with IO.

        Picasso.with(context)
                .load(url)
                .into(holder.imgIcon);

        return row;
    }

    static class TrackHolder {
        ImageView imgIcon;
        TextView txtTitle;
        TextView txtAlbum;
    }
}
