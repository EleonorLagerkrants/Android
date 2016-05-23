package dv606.el222ja.assignment2;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Eleonor on 2016-04-16.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener{

    private MediaPlayer player;
    private ArrayList<Song> songs;
    private final IBinder musicBind = new MusicBinder();
    int length;
    int currentPos;
    String songTitle;

    public void onCreate(){
        super.onCreate();
        player = new MediaPlayer();
        initMusicPlayer();
    }

    public void initMusicPlayer() {
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
    }

    public void playSong(final Song song) {
        //final Song playSong = song;
        if(song == null) return;
        try {
            if (player.isPlaying()) player.stop(); // stop the current song

            player.reset(); // reset the resource of player
            player.setDataSource(this, Uri.parse(song.getPath())); // set the song to play
            player.setAudioStreamType(AudioManager.STREAM_NOTIFICATION); // select the audio stream
            player.prepare(); // prepare the resource
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() // handle the completion
            {
                @Override
                public void onCompletion(MediaPlayer mp) {
                        playSong(song.getNext());
                }
            });

            player.start();
            setSongTitle(song.getName());

        } catch (Exception e) {
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    public void setList(ArrayList<Song> songList) {
        songs = songList;
    }

    public void setCurrentPos(int pos) {
        this.currentPos = pos;
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void stop(){
        try
        {
            if (player.isPlaying()) player.stop(); // stop the current song

        }
        catch(Exception e)
        {
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void pause() {
        try {
            if (player.isPlaying()) {
                player.pause(); // pause the current song
                length = player.getCurrentPosition();
            }

        } catch (Exception e) {
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void resume() {
        try {
            if (player.isPlaying() == false) {
                player.seekTo(length);
                player.start();
            }
        } catch (Exception e) {
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void playPrev() {
        int sPos = getCurrentPos();
        if(sPos != 0)
            sPos--;
        if (sPos == songs.size()) sPos = 0;
        playSong(songs.get(sPos));
        setCurrentPos(sPos);
    }

    public void playNext() {
        int sPos = getCurrentPos();
        if(sPos != songs.size())
            sPos++;
        if (sPos == songs.size()) sPos = 0;
        playSong(songs.get(sPos));
        setCurrentPos(sPos);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();

        Intent notIntent = new Intent(this, MP3Player.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.play)
                .setOngoing(true)
                .setContentTitle(getString(R.string.not_title))
        .setContentText(getSongTitle());
        Notification not = builder.build();

        startForeground(2, not);
    }


    public void setSongTitle(String title) {
        this.songTitle = title;
    }

    public String getSongTitle() {
        return this.songTitle;
    }
    @Override
    public void onDestroy() {
        stopForeground(true);
    }
}
