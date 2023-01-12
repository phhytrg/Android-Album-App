package com.example.album.private_session;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.album.R;

public class PrivateLoginFragment extends Fragment {

    private ViewGroup loginLayout;
    private ViewGroup privatePhotosFragment;
    private Button loginButton;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.private_fragment, container, false).getRootView();

        loginLayout = view.findViewById(R.id.login_layout);
        privatePhotosFragment = view.findViewById(R.id.private_photos_layout);
        loginButton = view.findViewById(R.id.login_button);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loginButton.setOnClickListener(v -> {
            EditText password = view.findViewById(R.id.password);
            if(password.getText().toString().equals("0000")){
                loginLayout.setVisibility(View.GONE);
                privatePhotosFragment.setVisibility(View.VISIBLE);
            }
        });
    }
}
