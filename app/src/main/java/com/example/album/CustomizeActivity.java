//package com.example.album;
//
//import android.app.ActionBar;
//import android.os.Bundle;
//import android.preference.PreferenceFragment;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.Fragment;
//
//public class CustomizeActivity extends Fragment {
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        getFragmentManager().beginTransaction().replace(android.R.id.content,
//                new CustomizeFragment()).commit();
//
//        ActionBar actionBar = getActionBar();
//        if (actionBar != null) {
//            // Show the back button in the action bar.
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
//    }
//
//    public static class CustomizeFragment extends PreferenceFragment {
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            addPreferencesFromResource(R.xml.custom_preferences);
//        }
//    }
//}
