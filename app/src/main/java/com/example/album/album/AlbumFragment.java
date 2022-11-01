package com.example.album.album;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.album.R;
import com.example.album.item_decoration.GridSpacingItemDecoration;

public class AlbumFragment extends Fragment{

//    private GridView gridView;
    private RecyclerView recyclerView;

    String[] albumName = {"Camera","Videos", "Favorites","Screens","Locations","Download", "Collages",
            "Picnic", "Friends", "Selfie", "Memes"};
    int[] albumImages = {R.drawable.photo1, R.drawable.photo2, R.drawable.photo10,
            R.drawable.photo4, R.drawable.cat1, R.drawable.photo6, R.drawable.photo3,
            R.drawable.photo5, R.drawable.photo8, R.drawable.photo7, R.drawable.photo9};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.album_layout,container, false).getRootView();

        setUpToolBar(view);

        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.album_option,menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_newAlbum:
                        Toast.makeText(requireContext(), "create new album",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.menu_setting:
                        Toast.makeText(requireContext(), "setting",Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }

        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        GridAdapter gridAdapter = new GridAdapter(requireContext(), albumName, albumImages);

        AlbumAdapter albumAdapter = new AlbumAdapter();
        recyclerView = view.findViewById(R.id.album_list);
        recyclerView.setAdapter(albumAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(),3));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3,8,false));
//        gridView = (GridView) view.findViewById(R.id.album_list);
//        gridView.setAdapter(gridAdapter);

//        ImageButton camera_btn = view.findViewById(R.id.btn_camera);
//        camera_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(requireContext(), "You're opening camera", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void setUpToolBar(View view){
        Toolbar toolbar = view.findViewById(R.id.album_app_bar);
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        if(activity != null){
            activity.setSupportActionBar(toolbar);
        }
    }

    //    public void showAlbumOption(View v){
//          PopupMenu popupMenu = new PopupMenu(requireContext(), v);
//          popupMenu.setOnMenuItemClickListener(requireContext());
//          popupMenu.inflate(R.menu.album_option);
//          popupMenu.show();
//    }
}