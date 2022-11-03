package com.example.album.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.album.R;

public class GalleryFragment extends Fragment {

    View view;
    RecyclerView recyclerView;
    GalleryAdapter adapter;

    boolean isLinearLayout = true;

//    private ScaleGestureDetector scaleGestureDetector;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        scaleGestureDetector = new ScaleGestureDetector(requireContext(), new PinchZoomListener());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.gallery_fragment, container, false).getRootView();
        if(getActivity()!=null){
            ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            if(actionBar!=null) {
                actionBar.setTitle(getString(R.string.photos));
            }
        }

//        MenuHost menuHost = requireActivity();
//        MenuProvider menuProvider = new MenuProvider() {
//            @Override
//            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
////                menuInflater.inflate(R.menu.switch_layout_menu, menu);
//                MenuItem layoutMenu = menu.findItem(R.id.action_switch_layout);
//                setIcon(layoutMenu);
//            }
//
//            @Override
//            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
//                if(menuItem.getItemId() == R.id.action_switch_layout) {
//                    isLinearLayout = !isLinearLayout;
//                    adapter.setLinearLayout(isLinearLayout);
//                    chooseLayout();
//                    setIcon(menuItem);
//                }
//                return true;
//            }
//
//        };
//        menuHost.addMenuProvider(menuProvider, getViewLifecycleOwner(), Lifecycle.State.CREATED);


//        SplitToolbar toolbar = ((AppCompatActivity)getActivity()).findViewById(R.id.navigation_bar);
//        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                if(item.getItemId() == R.id.action_switch_layout){
//                    isLinearLayout = !isLinearLayout;
//                    adapter.setLinearLayout(isLinearLayout);
//                    chooseLayout();
//                    setIcon(item);
//                }
//                return true;
//            }
//        });
//        toolbar.addMenuProvider(new MenuProvider() {
//            @Override
//            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
//            }
//
//            @Override
//            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
//                return false;
//            }
//        });

//        toolbar.setVisibility(View.GONE);

//        MenuHost menuHost = requireActivity();
//        menuHost.addMenuProvider(new MenuProvider() {
//            @Override
//            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
//                menuInflater.inflate(R.menu.layout_option, menu);
//                MenuItem layoutMenu = menu.findItem(R.id.action_switch_layout);
////                setIcon(layoutMenu);
//            }
//
//            @Override
//            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
//                isLinearLayout = !isLinearLayout;
//                adapter.setLinearLayout(isLinearLayout);
//                chooseLayout();
//                setIcon(menuItem);
//                return true;
//            }
//        });
//
//        view.findViewById(R.id.gallery_recyclerview).setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.d("aaaa","ACTION DETECTED");
//                scaleGestureDetector.onTouchEvent(event);
//                return true;
//            }
//        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.gallery_recyclerview);
        adapter = new GalleryAdapter(isLinearLayout);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        MenuHost menuHost = (MenuHost) getActivity();
        MenuProvider menuProvider = new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
//                menuInflater.inflate(R.menu.switch_layout_menu, menu);
                MenuItem layoutMenu = menu.findItem(R.id.action_switch_layout);
                setIcon(layoutMenu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.action_switch_layout) {
                    isLinearLayout = !isLinearLayout;
//                    adapter.setLinearLayout(isLinearLayout);
//                    chooseLayout();
                    setIcon(menuItem);
                    if(isLinearLayout){
                        for (int childCount = adapter.getItemCount(), i = 0; i < childCount; ++i) {
//                            GalleryAdapter.GalleryViewHolder viewHolder =
//                                    (GalleryAdapter.GalleryViewHolder) recyclerView
//                                            .findViewHolderForAdapterPosition(i);
                            adapter.setLinearLayout(isLinearLayout);
                            adapter.notifyItemChanged(i);
                        }
                    }
                    else{
                        for (int childCount = adapter.getItemCount(), i = 0; i < childCount; ++i) {
//                            GalleryAdapter.GalleryViewHolder viewHolder =
//                                    (GalleryAdapter.GalleryViewHolder) recyclerView
//                                            .findViewHolderForAdapterPosition(i);

                            adapter.setLinearLayout(isLinearLayout);
                            adapter.notifyItemChanged(i);
                        }
                    }
                }
                return true;
            }

        };
        if(menuHost != null) {
            menuHost.addMenuProvider(menuProvider, getViewLifecycleOwner(), Lifecycle.State.CREATED);
        }
    }

    private void setIcon(MenuItem menuItem){
        if(menuItem == null)
            return;
        if(isLinearLayout){
            menuItem.setIcon(ContextCompat.getDrawable(this.requireContext(),R.drawable.ic_grid_layout));
        }
        else{
            menuItem.setIcon(ContextCompat.getDrawable(this.requireContext(),R.drawable.ic_linear_layout));
        }
    }

//    public class PinchZoomListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
//        @Override
//        public boolean onScale(ScaleGestureDetector detector) {
//            float gestureFactor = detector.getScaleFactor();
//            if(gestureFactor > 1){
//                isLinearLayout = !isLinearLayout;
//                for (int childCount = adapter.getItemCount(), i = 0; i < childCount; ++i) {
////                            GalleryAdapter.GalleryViewHolder viewHolder =
////                                    (GalleryAdapter.GalleryViewHolder) recyclerView
////                                            .findViewHolderForAdapterPosition(i);
//                    adapter.setLinearLayout(isLinearLayout);
//                    adapter.notifyItemChanged(i);
//                }
//            }
//            return true;
//        }
//
//        @Override
//        public boolean onScaleBegin(ScaleGestureDetector detector) {
//            return super.onScaleBegin(detector);
//        }
//
//        @Override
//        public void onScaleEnd(ScaleGestureDetector detector) {
//            super.onScaleEnd(detector);
//        }
//    }
}
