package com.tvm.doctorcube.study.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.tvm.doctorcube.study.fragment.NotesFragment;
import com.tvm.doctorcube.study.fragment.VideosFragment;

import java.util.HashMap;
import java.util.Map;

public class StudyMaterialPagerAdapter extends FragmentStateAdapter {

    private final Map<Integer, Fragment> fragmentMap = new HashMap<>();

    public StudyMaterialPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new NotesFragment();
                break;
            case 1:
                fragment = new VideosFragment();
                break;
            default:
                fragment = new NotesFragment();
        }
        fragmentMap.put(position, fragment);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2; // Notes and Videos
    }

    public Fragment getFragmentAt(int position) {
        return fragmentMap.get(position);
    }
}