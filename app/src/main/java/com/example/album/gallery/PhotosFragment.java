package com.example.album.gallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.album.ImageUri;
import com.example.album.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

public class PhotosFragment extends Fragment {
    List<ListItem> listItems;
    NavController navController;
    PhotosAdapter adapter;
    RecyclerView recyclerView;

    private boolean isLinearLayout = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        NavHostFragment navHostFragment = (NavHostFragment) requireActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        navController = navHostFragment == null ? null : navHostFragment.getNavController();
        return inflater.inflate(R.layout.gallery_fragment, container, false).getRootView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listItems = new ArrayList<>();
        TreeMap<Date, List<Event>> events = toMap(loadEvents());

        for (Date date : events.keySet()) {
            HeaderItem header = new HeaderItem(date);
            listItems.add(header);
            for (Event event : events.get(date)) {
                EventItem item = new EventItem(event);
                listItems.add(item);
            }
        }

        recyclerView = view.findViewById(R.id.gallery_recyclerview);
        PhotosAdapter.AdapterCallback listener = new PhotosAdapter.AdapterCallback() {
            @Override
            public void OnItemClick(EventItem item) {
                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), item.getEvent().getImageId());
                Uri imageUri = ImageUri.getImageUri(requireContext(), largeIcon);
                NavDirections action = PhotosFragmentDirections
                        .actionPhotosFragmentToDetailFragment(imageUri);
                navController.navigate(action);
            }

            @Override
            public void LinearItemDecoration(ImageView imageView) {
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

    private List<Event> loadEvents(){
        List<Event> events = new ArrayList<>();
        List <Integer>images = new ArrayList<>(Arrays.asList(R.drawable.photo1, R.drawable.photo2,
                R.drawable.photo10, R.drawable.photo4, R.drawable.cat1, R.drawable.photo6,
                R.drawable.photo3, R.drawable.photo5, R.drawable.photo8, R.drawable.photo7,
                R.drawable.photo9, R.drawable.image2, R.drawable.photo2,
                R.drawable.photo10, R.drawable.photo4, R.drawable.cat1, R.drawable.photo6,
                R.drawable.photo3, R.drawable.image2, R.drawable.photo8, R.drawable.photo7,
                R.drawable.photo9));
        for(int i = 0; i< images.size(); i++){
            events.add(new Event(images.get(i),buildRandomDateInCurrentMonth()));
        }
        return events;
    }


    private Date buildRandomDateInCurrentMonth() {
        Random random = new Random();
        return DateUtils.buildDate(random.nextInt(31) + 1);
    }

    private TreeMap<Date, List<Event>> toMap(List<Event> events){
        TreeMap<Date, List<Event>> map = new TreeMap<>();
        for(Event event: events){
            List<Event> value = map.get(event.getDate());
            if(value == null){
                value = new ArrayList<>();
                map.put(event.getDate(), value);
            }
            value.add(event);
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

