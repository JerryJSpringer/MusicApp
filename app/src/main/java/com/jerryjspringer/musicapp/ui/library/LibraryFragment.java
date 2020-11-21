package com.jerryjspringer.musicapp.ui.library;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jerryjspringer.musicapp.MainActivity;
import com.jerryjspringer.musicapp.R;
import com.jerryjspringer.musicapp.audio.AudioUtil;
import com.jerryjspringer.musicapp.databinding.FragmentLibraryBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class LibraryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentLibraryBinding binding = FragmentLibraryBinding.bind(view);

        binding.listRoot.setAdapter(
                new ArtistRecyclerViewAdapter(AudioUtil.getArtists(getContext())));
        binding.listRoot.addItemDecoration(
                new DividerItemDecoration(getActivity(), LinearLayout.VERTICAL));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.toolbar_library, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}