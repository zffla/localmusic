package com.example.localmusic;

public class LocalMusicBean {
    private String song;//歌曲名
    private String singer;//歌手名
    private String album;//专辑名
    private String time;//时长
    private String path;//存储路径

    public LocalMusicBean(){
    }
    public LocalMusicBean(String song, String singer, String album, String time, String path) {
        this.song = song;
        this.singer = singer;
        this.album = album;
        this.time = time;
        this.path = path;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
