package com.example.android.musicplayer;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // My views
    Button playBtn;
    Button backBtn;
    Button nextBtn;
    MediaPlayer m1,m2,m3,m4,m5;
    SeekBar positionBar;
    SeekBar volumeBar;
    ImageView musicImg;
    TextView title;
    TextView currentTime; // For the SeekBar
    TextView remainTime; // For the SeekBar
    TextView numOfSongs;



    int songIndex; // the index of the songlist
    int totalTime; // For the SeekBar
    int currentNumOfSongs; // For the text view called numOfSongs
    int totalSongs; // For the text view called numOfSongs
    String[] musicTitles = new String[5];

    ArrayList<MediaPlayer> songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songIndex = 0;

        // The song list and its implementation
        songList = new ArrayList<>();
        m1 = MediaPlayer.create(this, R.raw.bazzi);
        m2 = MediaPlayer.create(this, R.raw.barry);
        m3 = MediaPlayer.create(this, R.raw.bee);
        m4 = MediaPlayer.create(this, R.raw.liar);
        m5 = MediaPlayer.create(this, R.raw.scott);

        // adding music to songList
        songList.add(m1);
        songList.add(m2);
        songList.add(m3);
        songList.add(m4);
        songList.add(m5);

        // setting each songs in the songList its properties
        for(int i =0; i < songList.size(); i++){
            songList.get(i).setLooping(true);
            songList.get(i).seekTo(0);
            songList.get(i).setVolume(10f, 10f);
        }

        // The buttons
        playBtn = findViewById(R.id.playBtn);
        backBtn = findViewById(R.id.backBtn);
        nextBtn = findViewById(R.id.nextBtn);

        // The images
        musicImg = findViewById(R.id.musicImage);

        // The text views
        title = findViewById((R.id.musicTitle));
        currentTime = findViewById(R.id.curTime);
        remainTime = findViewById(R.id.remTime);
        numOfSongs = findViewById(R.id.numberOfSongs);

        // Music Titles
        musicTitles[0] = "Bazzi feat. Camila Cabello - Beautiful";
        musicTitles[1] = "Barry Manilow - Can't Smile Without You";
        musicTitles[2] = "Bee Gees - How Deep Is Your Love";
        musicTitles[3] = "Carolina Liar - Show me What I'm Looking For";
        musicTitles[4] = "Scott McKenzie - San Francisco";

        // The total time of the current song
        totalTime = songList.get(songIndex).getDuration();

        // Setting up how many songs is currently being used
        currentNumOfSongs = 1;
        totalSongs = songList.size();
        numOfSongs.setText(currentNumOfSongs + " / " + totalSongs);

        // Play the current song
        playBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!songList.get(songIndex).isPlaying()){
                    songList.get(songIndex).start();
                    playBtn.setBackgroundResource(R.drawable.pausebtn);
                }else{
                    songList.get(songIndex).pause();
                    playBtn.setBackgroundResource(R.drawable.playbtn);
                }
            }
        });

        // move to previous song from current song
        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(songList.get(songIndex) != songList.get(0)){
                    songList.get(songIndex).pause(); // pause the current song, need to find one that resets the audio not pause
                    if(currentNumOfSongs >= 1){
                        currentNumOfSongs--;
                    }
                    numOfSongs.setText(currentNumOfSongs + " / " + totalSongs);
                    songIndex--;
                    playBtn.setBackgroundResource(R.drawable.pausebtn);
                    changeImg(songIndex);
                    changeTitle(songIndex);
                    if(!songList.get(songIndex).isPlaying()){
                        songList.get(songIndex).seekTo(0); // sets audio position to 0
                        songList.get(songIndex).start();
                    }
                }
            }
        });

        // move back from current song
        nextBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(songList.get(songIndex) != songList.get(4)){
                    songList.get(songIndex).pause();// // pause the current song, need to find one that resets the audio not pause
                    if(currentNumOfSongs <= songList.size()){
                        currentNumOfSongs++;
                    }
                    numOfSongs.setText(currentNumOfSongs + " / " + totalSongs);
                    songIndex++;
                    playBtn.setBackgroundResource(R.drawable.pausebtn);
                    changeImg(songIndex);
                    changeTitle(songIndex);
                    if(!songList.get(songIndex).isPlaying()){
                        songList.get(songIndex).seekTo(0); // sets audio position to 0
                        songList.get(songIndex).start();
                    }
                }
            }
        });

        // set volume of current song
        volumeBar = findViewById(R.id.volumeBar);
        volumeBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        float volumeNum = progress / 100f;
                        songList.get(songIndex).setVolume(volumeNum, volumeNum);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );

        // set position of current song
        positionBar = findViewById(R.id.musicDuration);
        positionBar.setMax(totalTime);
        positionBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            songList.get(songIndex).seekTo(progress);
                            positionBar.setProgress(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );

        // thread (updated positionBar & timeLabel)
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (songList.get(songIndex) != null) {
                    try {
                        Message msg = new Message();
                        msg.what = songList.get(songIndex).getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(1000);
                    } catch (InterruptedException e)
                    {
                        //empty
                    }
                }
            }
        }).start();

    }

    // ignore static for now
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int currentPosition = msg.what;
            // Update positionBar.
            positionBar.setProgress(currentPosition);

            // Update Labels.
            String elapsedTime = createTimeLabel(currentPosition);
            currentTime.setText(elapsedTime);

            String remainingTime = createTimeLabel(totalTime-currentPosition);
            remainTime.setText("- " + remainingTime);
        }
    };

    public String createTimeLabel(int time) {
        String timeLabel;
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }

    // Sonny's custom methods/functions to change the current song title/image
    public void changeImg(int num){
        if(songList.get(num) == songList.get(0)){
            title.setText(musicTitles[0]);
        }else if(songList.get(num) == songList.get(1)){
            title.setText(musicTitles[1]);
        }else if(songList.get(num) == songList.get(2)){
            title.setText(musicTitles[2]);
        }else if(songList.get(num) == songList.get(3)){
            title.setText(musicTitles[3]);
        }else if(songList.get(num) == songList.get(4)){
            title.setText(musicTitles[4]);
        }
    }

    public void changeTitle(int num){
        if(songList.get(num) == songList.get(0)){
            musicImg.setBackgroundResource(R.drawable.bazzi);
        }else if(songList.get(num) == songList.get(1)){
            musicImg.setBackgroundResource(R.drawable.barry);
        }else if(songList.get(num) == songList.get(2)){
            musicImg.setBackgroundResource(R.drawable.love);
        }else if(songList.get(num) == songList.get(3)){
            musicImg.setBackgroundResource(R.drawable.liar);
        }else if(songList.get(num) == songList.get(4)){
            musicImg.setBackgroundResource(R.drawable.san);
        }
    }
}
