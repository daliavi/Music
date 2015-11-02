package com.daliavi.android.music.app.music;


public class ArtistData {
    private static ArtistData mInstance = null; //remove after test
    public String icon;
    public String title;
    public String artist_id;

    public ArtistData() {
        super();
    }


    public ArtistData(String icon, String title, String artist_id){
        super();
        this.icon = icon;
        this.title = title;
        this.artist_id = artist_id;
    }

    public void addArtist(ArtistData artistlist, String icon, String title, String artist_id){

    }
//remove after test
    public static ArtistData getInstance(){

        if(mInstance == null)
        {
            mInstance = new ArtistData();
        }
        return mInstance;
    }
//remove after test
    public String getTitle(){
        return this.title;
    }
}
