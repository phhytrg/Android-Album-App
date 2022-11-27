package com.example.album.gallery;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.album.R;
import com.example.album.data.Image;
import com.example.album.data.ImagesViewModel;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class PhotosFragment extends Fragment {
    List<ListItem> listItems;
    NavController navController;
    PhotosAdapter adapter;
    RecyclerView recyclerView;
    ImagesViewModel imagesViewModel;

    private boolean isLinearLayout = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        NavHostFragment navHostFragment = (NavHostFragment) requireActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        navController = navHostFragment == null ? null : navHostFragment.getNavController();
        imagesViewModel = new ViewModelProvider(requireActivity()).get(ImagesViewModel.class);
        return inflater.inflate(R.layout.gallery_fragment, container, false).getRootView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imagesViewModel.getImages().observe(
                getViewLifecycleOwner(),
                images -> {
                    adapter.notifyDataSetChanged();
                }
        );
        listItems = new ArrayList<>();
        TreeMap<LocalDateTime, List<Image>> images = toMap(imagesViewModel.getImages().getValue());

        for (LocalDateTime date : images.descendingKeySet()) {
            HeaderItem header = new HeaderItem(date);
            listItems.add(header);
            for (Image image : images.get(date)) {
                ImageItem item = new ImageItem(image);
                listItems.add(item);
            }
        }

        recyclerView = view.findViewById(R.id.gallery_recyclerview);
        PhotosAdapter.AdapterCallback listener = new PhotosAdapter.AdapterCallback() {
            @Override
            public void onItemClick(@NonNull ImageItem item) {
                Uri imageUri = item.getImage().getImageUri();
                NavDirections action = PhotosFragmentDirections
                        .actionPhotosFragmentToDetailFragment(imageUri);
                navController.navigate(action);
            }

            @Override
            public void linearItemDecoration(@NonNull ImageView imageView) {
                imageView.setBackground(ResourcesCompat.getDrawable(
                        getResources(),
                        R.drawable.image_border,
                        requireActivity().getTheme())
                );
            }
        };

        adapter = new PhotosAdapter(listItems, listener);
        recyclerView.setAdapter(adapter);
        chooseLayout();

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                MenuItem layoutMenu = menu.findItem(R.id.action_switch_layout);
                setIcon(layoutMenu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if(id == R.id.action_switch_layout){
                    isLinearLayout = !isLinearLayout;
                    chooseLayout();
                    setIcon(menuItem);
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.CREATED);
    }

//    private LocalDate buildRandomDateInCurrentMonth() {
//        Random random = new Random();
//        return DateUtils.buildDate(random.nextInt(31) + 1);
//    }


    @NonNull
    private TreeMap<LocalDateTime, List<Image>> toMap(@NonNull List<Image> images){
        TreeMap<LocalDateTime, List<Image>> map = new TreeMap<>();
        for(Image image: images){
            List<Image> value = map.get(image.getDate().truncatedTo(ChronoUnit.DAYS));
            if(value == null){
                value = new ArrayList<>();
                map.put(image.getDate().truncatedTo(ChronoUnit.DAYS), value);
            }
            value.add(image);
        }
        return map;
    }

    private void chooseLayout(){
        if(isLinearLayout){
            LinearLayoutManager manager = new LinearLayoutManager(requireContext());
            recyclerView.setLayoutManager(manager);
            adapter.setLinearLayout(isLinearLayout);
        }
        else{
            GridLayoutManager manager = new GridLayoutManager(requireContext(),4);
            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    switch (adapter.getItemViewType(position)){
                        case ListItem.TYPE_HEADER:{
                            return 4;
                        }
                        case ListItem.TYPE_EVENT:{
                            return 1;
                        }
                        default: throw new IllegalStateException("Unsupported Type!");
                    }
                };
            });
            recyclerView.setLayoutManager(manager);
            adapter.setLinearLayout(isLinearLayout);
        }
        for (int childCount = adapter.getItemCount(), i = 0; i < childCount; ++i) {
            if(listItems.get(i).getType() == ListItem.TYPE_EVENT)
            {
                adapter.notifyItemChanged(i);
            }
        }
    }

    private void setIcon(MenuItem menuItem){
        if(menuItem == null)
            return;
        if(isLinearLayout){
            menuItem.setIcon(ContextCompat.getDrawable(this.requireContext(),R.drawable.ic_grid_layout));
        }
        else {
            menuItem.setIcon(ContextCompat.getDrawable(this.requireContext(),R.drawable.ic_linear_layout));
        }
    }
}

