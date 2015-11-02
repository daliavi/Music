package com.daliavi.android.music.app.music;

/**
 * Created by telmate on 8/25/15.
 */
public class TrackData {
    public String track_icon;
    public String track_title;
    public String album_title;

    public TrackData() {
        super();
    }


    public TrackData(String icon, String title, String album){
        super();
        this.track_icon = icon;
        this.track_title = title;
        this.album_title = album;
    }

    public void addTrack(TrackData tracklist, String icon, String title, String album){


    }
}
