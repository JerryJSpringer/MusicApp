package com.jerryjspringer.musicapp.ui.artist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jerryjspringer.musicapp.R;
import com.jerryjspringer.musicapp.audio.AudioUtil;
import com.jerryjspringer.musicapp.databinding.FragmentArtistBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistFragment extends Fragment {

    private String mArtist;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArtist = ArtistFragmentArgs.fromBundle(getArguments()).getArtist();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_artist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentArtistBinding binding = FragmentArtistBinding.bind(view);

        binding.listRoot.setAdapter(
                new ArtistRecyclerViewAdapter(AudioUtil.getArtistSongs(getContext(), mArtist)));

        binding.listRoot.addItemDecoration(
                new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));
    }
}