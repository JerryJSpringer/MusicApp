package com.jerryjspringer.musicapp.ui.library;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jerryjspringer.musicapp.R;
import com.jerryjspringer.musicapp.audio.model.ArtistModel;
import com.jerryjspringer.musicapp.databinding.FragmentLibraryArtistBinding;

import java.util.List;

public class ArtistRecyclerViewAdapter extends RecyclerView.Adapter<ArtistRecyclerViewAdapter.ArtistViewHolder> {

    private final List<ArtistModel> mArtists;
    private RecyclerView mRecyclerView;

    public ArtistRecyclerViewAdapter(List<ArtistModel> artists) {
        mArtists = artists;
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_library_artist, parent, false);

        // Set onClick listener
        view.setOnClickListener(this::onClick);

        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        holder.setArtist(mArtists.get(position));
    }

    @Override
    public int getItemCount() {
        return mArtists.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    private void onClick(View view) {
        int position = mRecyclerView.getChildAdapterPosition(view);
    }

    class ArtistViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private FragmentLibraryArtistBinding binding;
        private ArtistModel mArtist;

        public ArtistViewHolder(@NonNull View view) {
            super(view);
            mView = view;
            binding = FragmentLibraryArtistBinding.bind(view);
        }

        void setArtist(final ArtistModel artist) {
            mArtist = artist;

            binding.textArtist.setText(artist.getArtist());

            String tracks = artist.getTracks()  + " " + ((artist.getTracks() == 1) ? "track" : "tracks");
            String albums = artist.getAlbums() + " " + ((artist.getAlbums() == 1) ? "album" : "albums");
            binding.textTracks.setText(tracks);
            binding.textAlbums.setText(albums);
        }
    }
}
