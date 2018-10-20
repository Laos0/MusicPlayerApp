package com.example.android.musicplayer;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
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
    ImageView musicImg;
    TextView title;
    TextView currentTime;
    TextView remainTime;


    int songIndex;
    int totalTime;
    String[] musicTitles = new String[5];

    ArrayList<MediaPlayer> songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songIndex = 0;

        // The song list and its implementation
        songList = new ArrayList<MediaPlayer>();
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
            songList.get(i).setVolume(.05f,.05f);
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

        // Music Titles
        musicTitles[0] = "Bazzi feat. Camila Cabello - Beautiful";
        musicTitles[1] = "Barry Manilow - Can't Smile Without You";
        musicTitles[2] = "Bee Gees - How Deep Is Your Love";
        musicTitles[3] = "Carolina Liar - Show me What I'm Looking For";
        musicTitles[4] = "Scott McKenzie - San Francisco";


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
                    songIndex--;
                    playBtn.setBackgroundResource(R.drawable.pausebtn);
                    changeImg(songIndex);
                    changeTitle(songIndex);
                    if(!songList.get(songIndex).isPlaying()){
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
                    songIndex++;
                    playBtn.setBackgroundResource(R.drawable.pausebtn);
                    changeImg(songIndex);
                    changeTitle(songIndex);
                    if(!songList.get(songIndex).isPlaying()){
                        songList.get(songIndex).start();
                    }
                }
            }
        });
    }

    // my Methods/functions

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
