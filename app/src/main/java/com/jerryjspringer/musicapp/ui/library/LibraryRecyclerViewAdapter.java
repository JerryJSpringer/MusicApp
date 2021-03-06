package com.jerryjspringer.musicapp.ui.library;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.jerryjspringer.musicapp.R;
import com.jerryjspringer.musicapp.audio.model.ArtistModel;
import com.jerryjspringer.musicapp.databinding.FragmentLibraryArtistBinding;

import java.util.List;

public class LibraryRecyclerViewAdapter extends RecyclerView.Adapter<LibraryRecyclerViewAdapter.ArtistViewHolder> {

    private final List<ArtistModel> mArtists;
    private RecyclerView mRecyclerView;

    public LibraryRecyclerViewAdapter(List<ArtistModel> artists) {
        mArtists = artists;
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_library_artist, parent, false);
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

        String artist = mArtists.get(position).getArtist();

        Navigation.findNavController(view).navigate(
                LibraryFragmentDirections
                    .actionNavigationLibraryToArtistFragment(artist));
    }

    class ArtistViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private FragmentLibraryArtistBinding mBinding;
        private ArtistModel mArtist;

        public ArtistViewHolder(@NonNull View view) {
            super(view);
            mView = view;
            mBinding = FragmentLibraryArtistBinding.bind(view);
        }

        void setArtist(final ArtistModel artist) {
            mArtist = artist;

            mBinding.textArtist.setText(artist.getArtist());

            String tracks = artist.getTracks()  + " " + ((artist.getTracks() == 1) ? "track" : "tracks");
            String albums = artist.getAlbums() + " " + ((artist.getAlbums() == 1) ? "album" : "albums");
            mBinding.textTracks.setText(tracks);
            mBinding.textAlbums.setText(albums);
        }
    }
}
