package com.example.album.story_view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.album.MainActivity;
import com.example.album.R;

import omari.hamza.storyview.StoryView;

public class StoryViewFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.story_view, container, false).getRootView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new StoryView.Builder(requireActivity().getSupportFragmentManager())
                .setStoriesList(((MainActivity)requireActivity()).onThisDayImages().first)
                .setHeadingInfoList(((MainActivity)requireActivity()).onThisDayImages().second)
                .setStoryDuration(5000)
                .build().show();

    }
}
