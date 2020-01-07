package com.example.localmusic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LocalMusicAdapter extends RecyclerView.Adapter<LocalMusicAdapter.MusicViewHolder> {
    Context context;
    List<LocalMusicBean> musicSet;

    OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        public void OnItemClick(View v,int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }

    public LocalMusicAdapter(Context context, List<LocalMusicBean> musicSet) {
        this.context = context;
        this.musicSet = musicSet;
    }



    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_music,parent,false);
        MusicViewHolder holder = new MusicViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, final int position) {
        LocalMusicBean music=musicSet.get(position);
        holder.song.setText(music.getSong());
        holder.singer.setText(music.getSinger());
        holder.album.setText(music.getAlbum());
        holder.time.setText(music.getTime());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.OnItemClick(view,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicSet.size();
    }

    class MusicViewHolder extends RecyclerView.ViewHolder {
        TextView song,singer,album,time;
        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            song=itemView.findViewById(R.id.item_music_tv_song);
            singer=itemView.findViewById(R.id.item_music_tv_singer);
            album=itemView.findViewById(R.id.item_music_tv_album);
            time=itemView.findViewById(R.id.item_music_tv_time);
        }
    }
}
