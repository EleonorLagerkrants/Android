package dv606.el222ja.assignment2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

/**
 * A simple MP3 player skeleton for 2DV606 Assignment 2.
 *
 * Created by Oleksandr Shpak in 2013.
 * Ported to Android Studio by Kostiantyn Kucher in 2015.
 * Last modified by Kostiantyn Kucher on 04/04/2016.
 */
public class MP3Player extends AppCompatActivity {

    ArrayList<Song> songs;
    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound = false;


    // This is an oversimplified approach which you should improve
    // Currently, if you exit/re-enter activity, a new instance of player is created
    // and you can't, e.g., stop the playback for the previous instance,
    // and if you click a song, you will hear another audio stream started

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the layout
        setContentView(R.layout.activity_mp3_player);

        // Initialize the list of songs
        songs = songList();
        final ListView listView = (ListView) findViewById(R.id.musicList);


        listView.setAdapter(new PlayListAdapter(this, songs));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //When a song is selected, the playSong method in the service class is started
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long arg3) {
                Song song = songs.get(pos);
                musicService.setCurrentPos(pos);
                musicService.playSong(song);
            }
        });

        //Create music-control buttons and add a listener to each
        Button playButton = (Button) findViewById(R.id.playButton);
        playButton.setOnClickListener(playListener);
        Button pauseButton = (Button) findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(pauseListener);
        Button prevButton = (Button) findViewById(R.id.prevButton);
        prevButton.setOnClickListener(prevListener);
        Button nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(nextListener);

    }

    //Connection to service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            musicService = binder.getService();
            //Pass list to service
            musicService.setList(songs);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    //Start service when activity starts
    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    private class PlayListAdapter extends ArrayAdapter<Song> {
        public PlayListAdapter(Context context, ArrayList<Song> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View row, ViewGroup parent) {
            Song data = getItem(position);

            row = getLayoutInflater().inflate(R.layout.layout_row, parent, false);

            TextView name = (TextView) row.findViewById(R.id.musicLabel);
            name.setText(String.valueOf(data));
            row.setTag(data);

            return row;
        }
    }

    /**
     * Checks the state of media storage. True if mounted;
     *
     * @return
     */
    private boolean isStorageAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * Reads song list from media storage.
     *
     * @return
     */
    private ArrayList<Song> songList() {
        ArrayList<Song> songs = new ArrayList<Song>();

        if (!isStorageAvailable()) // Check for media storage
        {
            Toast.makeText(this, R.string.nosd, Toast.LENGTH_SHORT).show();
            return songs;
        }

        Cursor music = getContentResolver().query( // using content resolver to read music from media storage
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.DATA},
                MediaStore.Audio.Media.IS_MUSIC + " > 0 ",
                null, null
        );

        if (music.getCount() > 0) {
            music.moveToFirst();
            Song prev = null;
            do {
                Song song = new Song(music.getString(0), music.getString(1), music.getString(2), music.getString(3));

                if (prev != null) // play the songs in a playlist, if possible
                    prev.setNext(song);

                prev = song;
                songs.add(song);
            }
            while (music.moveToNext());

            prev.setNext(songs.get(0)); // play in loop
        }
        music.close();

        return songs;
    }

    //Method that calls the service method stop
    private void stop() {
        musicService.stop();
    }
    //Method that calls the service method pause
    private void pause() {
        musicService.pause();
    }
    //Method that calls the service method resume
    private void resume() {
        musicService.resume();
    }
    //Method that calls the service method playPrev
    private void playPrev() {
        musicService.playPrev();
    }
    //Method that calls the service method playNext
    private void playNext() {
       musicService.playNext();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_mp3_player, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.stop_player:
                stop();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //OnClickListener for play button
    private View.OnClickListener playListener = new View.OnClickListener() {
        public void onClick(View v) {
            resume();
        }
    };
    //OnClickListener for pause button
    private View.OnClickListener pauseListener = new View.OnClickListener() {
        public void onClick(View v) {
            pause();
        }
    };
    //OnClickListener for play previous button
    private View.OnClickListener prevListener = new View.OnClickListener() {
        public void onClick(View v) {
            playPrev();
        }
    };
    //OnClickListener for play next button
    private View.OnClickListener nextListener = new View.OnClickListener() {
        public void onClick(View v) {
            playNext();
        }
    };

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}