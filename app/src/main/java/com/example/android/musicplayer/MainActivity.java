package com.example.android.musicplayer;

import android.Manifest;
import android.app.ListActivity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.musicplayer.R;

import java.io.IOException;
import java.math.BigDecimal;

public class MainActivity extends ListActivity {

    // Equal to half a second (500ms)
    private static final int UPDATE_FREQUENCY = 500;

    // Equal to 4 seconds (4000ms)
    private static final int STEP_VALUE = 4000;

    // A MediaCursorAdapter makes it easy to display data from media files
    private MediaCursorAdapter mediaAdapter = null;

    // Media Player object
    private MediaPlayer player = null;

    // Main Activity Views
    private TextView selectedFile = null;
    private SeekBar seekbar = null;
    private ImageButton playButton = null;
    private ImageButton prevButton = null;
    private ImageButton nextButton = null;

    private boolean isStarted = true; // Checks if a media file has started playing
    private String currentFile = ""; // Current file name
    private boolean isMovingSeekBar = false; // Checks if the seekbar is being interacted with

    // This handler sends and processes Messages and Runnable objects like the one below
    // Eg. The person to send and receive a letter with an assigned postman
    private final Handler handler = new Handler();

    // Provides a common protocol to execute code while this object is active
    // Eg. The letter containing the execution
    private final Runnable updatePositionRunnable = new Runnable() {
        public void run() {
            updatePosition();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Request the user for access to the local storage
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

        // Set the views to their respective views in the XML file
        selectedFile = findViewById(R.id.selectedfile);
        seekbar = findViewById(R.id.seekbar);
        playButton = findViewById(R.id.play);
        prevButton = findViewById(R.id.prev);
        nextButton = findViewById(R.id.next);

        // Create a new Media Player
        player = new MediaPlayer();

        // Set change-based listeners to the media player and seekbar
        player.setOnCompletionListener(onCompletion);
        player.setOnErrorListener(onError);
        seekbar.setOnSeekBarChangeListener(seekBarChanged);

        // Provides random read-write access to the local media storage
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);

        if (null != cursor) {
            cursor.moveToFirst();

            // Use the playlist.xml to store information about the local media files
            mediaAdapter = new MediaCursorAdapter(this, R.layout.playlist, cursor);

            // Create a list with the media files' information
            setListAdapter(mediaAdapter);

            // Set button click listeners to the media player and seekbar
            playButton.setOnClickListener(onButtonClick);
            nextButton.setOnClickListener(onButtonClick);
            prevButton.setOnClickListener(onButtonClick);
        }

    }

    // Request the user for access to the local storage
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed

                } else {
                    // Permission denied! Disable the functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // When the user selects a song from the list, grab it's information and play it
    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        super.onListItemClick(list, view, position, id);

        currentFile = (String) view.getTag();

        startPlay(currentFile);
    }

    // Called when the activity is destroyed. Also destroys the media player
    @Override
    protected void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(updatePositionRunnable);
        player.stop();
        player.reset();
        player.release();

        player = null;
    }

    // Play the selected media file
    private void startPlay(String file) {
        Log.i("Selected: ", file);

        // The file name is a long directory string that needs to be shortened for display
        String fileName = cutFileName(file);
        selectedFile.setText(fileName);

        // Set the song at the beginning
        seekbar.setProgress(0);

        // Stop any current songs playing and reset the media player
        player.stop();
        player.reset();

        // Set the media player's source to the selected media file
        try {
            player.setDataSource(file);
            player.prepare();
            player.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        seekbar.setMax(player.getDuration());
        playButton.setImageResource(R.drawable.pausebtn);

        // Update the handler and seekbar based on the selected media file
        updatePosition();

        isStarted = true;
    }

    // Cut's a media file stored in local storage
    private String cutFileName(String file)
    {
        if(file != null)
        {
            String fileDirectoryName[] = file.split("/Music/");
            String[] fileName = fileDirectoryName[1].split(".mp3");
            return fileName[0];
        }
        else
        {
            return "No file selected.";
        }
    }

    // Stop the current media file from playing when playback has ended
    private void stopPlay() {
        player.stop();
        player.reset();
        playButton.setImageResource(R.drawable.playbtn);
        handler.removeCallbacks(updatePositionRunnable);
        seekbar.setProgress(0);

        isStarted = false;
    }

    // Update the handler and seekbar
    private void updatePosition() {
        handler.removeCallbacks(updatePositionRunnable);

        seekbar.setProgress(player.getCurrentPosition());

        handler.postDelayed(updatePositionRunnable, UPDATE_FREQUENCY);
    }

    // Set up the ListItem View in playlist.xml, binding it to activity_main.xml
    private class MediaCursorAdapter extends SimpleCursorAdapter {

        private MediaCursorAdapter(Context context, int layout, Cursor c) {
            super(context, layout, c,
                    new String[]{MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.TITLE, MediaStore.Audio.AudioColumns.DURATION},
                    new int[]{R.id.displayname, R.id.title, R.id.duration});
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView title = view.findViewById(R.id.title);
            TextView name = view.findViewById(R.id.displayname);
            TextView duration = view.findViewById(R.id.duration);

            name.setText(cursor.getString(
                    cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)));

            title.setText(cursor.getString(
                    cursor.getColumnIndex(MediaStore.MediaColumns.TITLE)));

            long durationInMs = Long.parseLong(cursor.getString(
                    cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION)));

            double durationInMin = ((double) durationInMs / 1000.0) / 60.0;

            durationInMin = new BigDecimal(Double.toString(durationInMin)).setScale(2, BigDecimal.ROUND_UP).doubleValue();

            duration.setText("" + durationInMin);

            view.setTag(cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)));
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(R.layout.playlist, parent, false);

            bindView(v, context, cursor);

            return v;
        }
    }

    // Invoked when the back, play/pause or next button is clicked
    private View.OnClickListener onButtonClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.play: {
                    if (player.isPlaying()) {
                        handler.removeCallbacks(updatePositionRunnable);
                        player.pause();
                        playButton.setImageResource(R.drawable.playbtn);
                    } else {
                        if (isStarted) {
                            player.start();
                            playButton.setImageResource(R.drawable.pausebtn);

                            updatePosition();
                        } else {
                            startPlay(currentFile);
                        }
                    }

                    break;
                }
                case R.id.next: {
                    if(player.isPlaying())
                    {
                        int seekto = player.getCurrentPosition() + STEP_VALUE;

                        if (seekto > player.getDuration())
                            seekto = player.getDuration();

                        player.pause();
                        player.seekTo(seekto);
                        player.start();
                    }
                    else
                    {
                        // do nothing
                    }

                    break;
                }
                case R.id.prev: {
                    if(player.isPlaying())
                    {
                        int seekto = player.getCurrentPosition() - STEP_VALUE;

                        if (seekto < 0)
                            seekto = 0;

                        player.pause();
                        player.seekTo(seekto);
                        player.start();
                    }
                    else
                    {
                        // do nothing
                    }

                    break;
                }
            }
        }
    };

    // Invoked when playback of a media source has completed
    private MediaPlayer.OnCompletionListener onCompletion = new MediaPlayer.OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mp) {
            stopPlay();
        }
    };

    // Reports errors
    private MediaPlayer.OnErrorListener onError = new MediaPlayer.OnErrorListener() {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {

            return false;
        }
    };

    // Invoked when the progress level of the seekbar has changed
    private SeekBar.OnSeekBarChangeListener seekBarChanged = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            isMovingSeekBar = false;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isMovingSeekBar = true;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (isMovingSeekBar) {
                player.seekTo(progress);

                Log.i("OnSeekBarChangeListener", "onProgressChanged");
            }
        }
    };
}