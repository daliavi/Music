package com.daliavi.android.music.app.music;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


/**
 * A placeholder fragment containing a simple view.
 */
public class TrackListActivityFragment extends Fragment {

    public TrackListActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_track_list, container, false);
        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
            String intent_value = intent.getStringExtra(Intent.EXTRA_TEXT);

            Context context = this.getActivity();
            int duration = Toast.LENGTH_SHORT;


            Toast toast = Toast.makeText(context, intent_value + " from Intent Fragment", duration);
            toast.show();

        }

        return rootView;
    }




}
