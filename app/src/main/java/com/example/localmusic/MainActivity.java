package com.example.localmusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView next,last,play;
    TextView song,singer;
    RecyclerView rv;
    List<LocalMusicBean> musicSet;
    LocalMusicAdapter musicAdapter;
    //记录当前位置
    int currentPosition=-1;
    //记录暂停播放时的位置
    int pausePlayPosition=0;

    MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        musicSet=new ArrayList<>();
        musicAdapter = new LocalMusicAdapter(this, musicSet);
        rv.setAdapter(musicAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rv.setLayoutManager(layoutManager);
        mediaPlayer=new MediaPlayer();
        //设置点击事件
        setItemClick();
        mediaPlayer.setOnCompletionListener(new LocalMusicOnCompletionListener());
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission
            .WRITE_EXTERNAL_STORAGE},1);
        }else{
            //加载本地音乐
            loadLocalMusic();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    loadLocalMusic();
                }else{
                    Toast.makeText(this,"拒绝权限无法安装应用哦",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    private void setItemClick() {
        /*设置点击事件*/
        musicAdapter.setOnItemClickListener(new LocalMusicAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View v, int position) {
                currentPosition=position;
                pausePlayPosition=0;
                playMusicInPosition(position);
            }
        });
    }

    private void playMusicInPosition(int position) {
        //设置底部显示的歌手名和歌曲名
        song.setText(musicSet.get(position).getSong());
        singer.setText(musicSet.get(position).getSinger());
        //停止播放当前音乐
        stopMusic();
        //重置音乐播放器设置信息播放路径
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(musicSet.get(position).getPath());
            //播放音乐
            playMusic();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playMusic() {
        /* 播放音乐*/
        if (mediaPlayer!=null&&!mediaPlayer.isPlaying()){
            try {
                if(pausePlayPosition==0){
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    play.setImageResource(R.drawable.pause);
                }else{
                    //从暂停位置开始播放
                    mediaPlayer.seekTo(pausePlayPosition);
                    mediaPlayer.start();
                    play.setImageResource(R.drawable.pause);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void pauseMusic() {
        /* 暂停播放音乐*/
        if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
            pausePlayPosition=mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            play.setImageResource(R.drawable.play_button);
        }
    }

    private void stopMusic() {
        /* 停止播放音乐*/
        if (mediaPlayer!=null&&mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            mediaPlayer.stop();
            play.setImageResource(R.drawable.play_button);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMusic();
        mediaPlayer.release();
    }

    private void loadLocalMusic() {
        /* 加载本地音乐*/
        ContentResolver contentResolver=getContentResolver();
        Uri uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor=contentResolver.query(uri,null,null,null,null);
        while(cursor.moveToNext()){
            String song=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String singer=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String album=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            String path=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            Long timeL=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
            String time=dateFormat.format(new Date(timeL));
            LocalMusicBean musicBean = new LocalMusicBean(song, singer, album, time, path);
            musicSet.add(musicBean);
        }
        musicAdapter.notifyDataSetChanged();
    }

    //初始化控件
    private void initView() {
        next=findViewById(R.id.icon_music_iv_next);
        last=findViewById(R.id.icon_music_iv_last);
        play=findViewById(R.id.icon_music_iv_play);
        song=findViewById(R.id.icon_music_tv_song);
        singer=findViewById(R.id.icon_music_tv_singer);
        rv=findViewById(R.id.local_music_rv);
        next.setOnClickListener(this);
        last.setOnClickListener(this);
        play.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.icon_music_iv_next:
                if(currentPosition==-1||currentPosition==musicSet.size()-1){
                    //从第一首歌开始播放
                    currentPosition=0;
                    pausePlayPosition=0;
                    playMusicInPosition(currentPosition);
                }
                currentPosition+=1;
                pausePlayPosition=0;
                playMusicInPosition(currentPosition);
                break;
            case R.id.icon_music_iv_last:
                if(currentPosition==-1||currentPosition==0){
                    //从最后一首歌开始播放
                    currentPosition=musicSet.size()-1;
                    pausePlayPosition=0;
                    playMusicInPosition(currentPosition);
                }
                currentPosition-=1;
                pausePlayPosition=0;
                playMusicInPosition(currentPosition);
                break;
            case R.id.icon_music_iv_play:
                if(currentPosition==-1){
                    //未选中播放歌曲时，从第一首歌开始播放
                    currentPosition=0;
                    pausePlayPosition=0;
                    playMusicInPosition(currentPosition);
                }else{
                    if(mediaPlayer.isPlaying()){
                        //暂停播放
                        pauseMusic();
                    }else{
                        //继续播放音乐
                        playMusic();
                    }
                }
                break;
        }
    }

    private final class LocalMusicOnCompletionListener implements MediaPlayer.OnCompletionListener{
        /* 完成播放时切换下一首*/
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            if(currentPosition==musicSet.size()-1){
                //从第一首歌开始播放
                currentPosition=0;
                pausePlayPosition=0;
                playMusicInPosition(currentPosition);
            }
            currentPosition+=1;
            pausePlayPosition=0;
            playMusicInPosition(currentPosition);
        }
    }
}
