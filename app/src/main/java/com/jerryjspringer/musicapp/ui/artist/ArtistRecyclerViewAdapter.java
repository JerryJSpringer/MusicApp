package com.jerryjspringer.musicapp.ui.artist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.jerryjspringer.musicapp.R;
import com.jerryjspringer.musicapp.audio.model.AudioModel;
import com.jerryjspringer.musicapp.audio.util.StorageUtil;
import com.jerryjspringer.musicapp.databinding.FragmentArtistSongBinding;

import java.util.List;

public class ArtistRecyclerViewAdapter extends RecyclerView.Adapter<ArtistRecyclerViewAdapter.ArtistViewHolder> {

    private List<AudioModel> mSongs;
    private StorageUtil mStorageUtil;
    private RecyclerView mRecyclerView;

    public ArtistRecyclerViewAdapter(List<AudioModel> songs, StorageUtil storageUtil) {
        mSongs = songs;
        mStorageUtil = storageUtil;
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_artist_song, parent, false);
        view.setOnClickListener(this::onClick);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        holder.setSong(mSongs.get(position));
    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    private void onClick(View view) {
        int position = mRecyclerView.getChildAdapterPosition(view);

        mStorageUtil.storeCurrentPlaylist(mSongs);
        mStorageUtil.storeAudioIndex(position);

        Navigation.findNavController(view).navigate(
                ArtistFragmentDirections.actionArtistFragmentToSongFragment()
        );
    }

    class ArtistViewHolder extends RecyclerView.ViewHolder {

        private final View mView;
        private FragmentArtistSongBinding mBinding;
        private AudioModel mAudioModel;

        public ArtistViewHolder(@NonNull View view) {
            super(view);
            mView = view;
            mBinding = FragmentArtistSongBinding.bind(view);
        }

        void setSong(final AudioModel audioModel) {
            mAudioModel = audioModel;

            mBinding.textSongTitle.setText(audioModel.getTitle());
            mBinding.textSongArtist.setText(audioModel.getArtist());
            mBinding.textSongAlbum.setText(audioModel.getAlbum());
        }
    }
}
