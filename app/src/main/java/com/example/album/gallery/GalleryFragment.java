package com.example.album.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.album.R;

public class GalleryFragment extends Fragment {

    View view;
    RecyclerView recyclerView;
    GalleryAdapter adapter;

    boolean isLinearLayout = true;

    private ScaleGestureDetector scaleGestureDetector;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        scaleGestureDetector = new ScaleGestureDetector(requireContext(), new PinchZoomListener());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.gallery_fragment, container, false).getRootView();

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
        adapter = new GalleryAdapter();
        recyclerView.setAdapter(adapter);
        chooseLayout();
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

    private void chooseLayout(){
        RecyclerView.LayoutManager layoutManager;
        if(isLinearLayout){
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
//            while (recyclerView.getItemDecorationCount() > 0) {
//                recyclerView.removeItemDecorationAt(0);
//            }
//            recyclerView.addItemDecoration(
//                    new LinearSpacingItemDecoration(8,false)
//            );
        }
        else{
            layoutManager = new GridLayoutManager(getContext(), 4);
            recyclerView.setLayoutManager(layoutManager);
//            while (recyclerView.getItemDecorationCount() > 0) {
//                recyclerView.removeItemDecorationAt(0);
//            }
//            recyclerView.addItemDecoration(
//                    new GridSpacingItemDecoration(4,8,false)
//            );
        }
    }


//
//    public class PinchZoomListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
//        @Override
//        public boolean onScale(ScaleGestureDetector detector) {
//            float gestureFactor = detector.getScaleFactor();
//            if(gestureFactor > 1){
//                isLinearLayout = !isLinearLayout;
//                adapter.setLinearLayout(isLinearLayout);
//                chooseLayout();
//            }
//            else{
//
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
