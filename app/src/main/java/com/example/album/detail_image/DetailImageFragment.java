package com.example.album.detail_image;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.album.MainActivity;
import com.example.album.R;
import com.example.album.SplitToolbar;
import com.github.chrisbanes.photoview.PhotoView;


public class DetailImageFragment extends Fragment {
    SplitToolbar toolbar;
    PopupMenu popup;
    ImageButton navigateUpButton;
    ImageButton overflowButton;
    ActionBar app_bar;
    SplitToolbar navigationBar;
    //com.github.chrisbanes.photoview.PhotoView img;
    int isChecked;
    int resourceId;
    String[] details;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(getArguments()!=null){
            resourceId = getArguments().getInt("image");
        }
        app_bar = ((MainActivity)getActivity()).getSupportActionBar();
        if (app_bar != null) {
            app_bar.hide();
        }
        Window window = getActivity().getWindow();
        if(window != null) {
            window.setStatusBarColor(getResources()
                    .getColor(R.color.dark_grey, getActivity().getTheme()));
            window.getDecorView().setSystemUiVisibility(0);
        }

        navigationBar = getActivity().findViewById(R.id.navigation_bar);
        navigationBar.setVisibility(View.GONE);
//        Window window = null;
//        getActivity().findViewById(R.id.app_bar).setVisibility(View.GONE);
//        if(getActivity() != null) {
//            app_bar = ((MainActivity)getActivity()).getSupportActionBar();
//            if(app_bar!=null){
//                app_bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#121212")));
//                app_bar.setTitle("");
//
//                @SuppressLint("ResourceType") @StyleableRes
//                app_bar.setHomeAsUpIndicator(R.drawable.ic_back);
////                Toolbar toolbar = getActivity().findViewById(R.id.app_bar);
////                toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_more, getActivity().getTheme()));
//                getActivity().closeOptionsMenu();
//                getActivity().addMenuProvider(new MenuProvider() {
//                @Override
//                public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
//                    menuInflater.inflate(R.menu.ic_more_submenu, menu);
//                }
//
//                @Override
//                public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
//                    return false;
//                }
//            }, getViewLifecycleOwner(), Lifecycle.State.CREATED);
//            }
//            window = getActivity().getWindow();
//        }
//        if(window != null) {
//            window.setStatusBarColor(getResources()
//                    .getColor(R.color.dark_grey, getActivity().getTheme()));
//            window.getDecorView().setSystemUiVisibility(0);
//        }

        return inflater.inflate(R.layout.detail_image_fragment,container,false).getRootView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        app_bar.show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navigateUpButton = view.findViewById(R.id.back_button);
        overflowButton = view.findViewById(R.id.overflow_button);
        PhotoView imageView = view.findViewById(R.id.photo_view);
        imageView.setImageResource(resourceId);
        //img = (com.github.chrisbanes.photoview.PhotoView) findViewById(R.id.photo_view);
        toolbar = view.findViewById(R.id.bottom_nav);

        Resources resources = getActivity().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            int androidBarHeight = resources.getDimensionPixelSize(resourceId);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
            params.bottomMargin = androidBarHeight;
            toolbar.setLayoutParams(params);


            params = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
            params.bottomMargin = androidBarHeight;
            imageView.setLayoutParams(params);
        }
//        toolbar.setItemIconTintList(null);
        isChecked=0;
        toolbar.setOnMenuItemClickListener(item -> itemNavigationBottomSelected(item));
//        bottomNav.setOnItemSelectedListener(item -> itemNavigationBottomSelected(item));
//        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                return itemNavigationBottomSelected(item);
//            }
//        });

        navigateUpButton.setOnClickListener( v -> {

            Window window = getActivity().getWindow();
            TypedValue outValue = new TypedValue();
            getActivity().getTheme()
                    .resolveAttribute(androidx.appcompat.R.attr.colorPrimaryDark,outValue,true);
            window.setStatusBarColor(outValue.data);

            window.getDecorView().setSystemUiVisibility(0);

            NavHostFragment hostFragment = (NavHostFragment) getActivity()
                    .getSupportFragmentManager()
                    .findFragmentById(R.id.nav_host_fragment);
            NavController navController = hostFragment != null ? hostFragment.getNavController() : null;

            if(navController!= null){
                navController.navigateUp();
            }
        });
        overflowButton.setOnClickListener(this::showPopup);

        // details chứa name, date, location, size, description
        // theo thứ tự index 0 -> 4
        // name, date, location, size, description phải được lấy từ database
        // demo chưa có database nên chỉ gán cứng chạy thử
        details = new String[5];
        details[0] = "Image";
        details[1] = "29/10/2022";
        details[2] = "Ho Chi Minh";
        details[3] = "5KB";
        details[4] = "Flexing at Circle K with my bros";
        //Drawable drawable = img.getDrawable();
    }
    public void showPopup(View v) {
        popup = new PopupMenu(requireContext(), v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return onPopUpMenuClick(menuItem);
            }
        });
        popup.inflate(R.menu.image_menu);
        popup.show();
    }
    public boolean onPopUpMenuClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.details:
                handleDetails();
                Toast.makeText(requireContext(), "details", Toast.LENGTH_SHORT).show();
                break;
            case R.id.add:
                Toast.makeText(requireContext(), "add", Toast.LENGTH_SHORT).show();
                break;
            case R.id.set:
                Toast.makeText(requireContext(), "set", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rename:
                handleRename();
                Toast.makeText(requireContext(), "rename", Toast.LENGTH_SHORT).show();
                break;
            default:
                return false;
        }
        return true;
    }

    public void handleRename() {
        final AlertDialog.Builder renameDialog = new AlertDialog.Builder(requireContext());
        renameDialog.setTitle("Rename to:");
        final EditText input = new EditText(requireContext());
        input.setText(details[0]);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        renameDialog.setView(input);
        renameDialog.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                details[0] = input.getText().toString();
            }
        });
        renameDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        renameDialog.show();
    }

    public void handleDetails() {
        final AlertDialog.Builder detailsDialog = new AlertDialog.Builder(requireContext());
        detailsDialog.setTitle("Details");
        final EditText input = new EditText(requireContext());
        String info = details[0] + "\n" + details[1] + "\n" + details[2] + "\n" + details[3];
        detailsDialog.setMessage(info);
        input.setHint("Enter the description of this image");
        input.setText(details[4]);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setSingleLine(false);
        detailsDialog.setView(input);
        detailsDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                details[4] = input.getText().toString();
            }
        });
        detailsDialog.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        detailsDialog.show();
    }


    public boolean itemNavigationBottomSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.edit){
            Toast.makeText(requireContext(), "edit", Toast.LENGTH_SHORT).show();
        }
        if(id == R.id.favorite){
            if(isChecked==0){
                toolbar.getMenu().findItem(R.id.favorite).setIcon(R.drawable.ic_favorite_colored);
                isChecked=1;
            }else {
                toolbar.getMenu().findItem(R.id.favorite).setIcon(R.drawable.ic_favorite);
                isChecked=0;
            }
            Toast.makeText(requireContext(), "favorite", Toast.LENGTH_SHORT).show();
        }
        else if(id == R.id.delete) {
            Toast.makeText(requireContext(), "delete", Toast.LENGTH_SHORT).show();
        }
        else if(id == R.id.share){
            Toast.makeText(requireContext(), "share", Toast.LENGTH_SHORT).show();
        }
        else {
            return false;
        }
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////////

}