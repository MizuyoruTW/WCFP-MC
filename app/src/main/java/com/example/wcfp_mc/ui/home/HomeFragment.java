package com.example.wcfp_mc.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.wcfp_mc.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    public static final String PREFS_NAME = "MyPrefsFile";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ImageButton FB = (ImageButton)root.findViewById(R.id.FBlogo);
        ImageButton Twitter = (ImageButton) root.findViewById(R.id.TTlogo);
        ImageButton Linkedin=(ImageButton)root.findViewById(R.id.LIlogo);
        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        FB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse( "http://www.facebook.com/sharer.php?u=http://www.wikicfp.com"));
                                startActivity(intent);
            }
        });
        Twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse( "http://twitter.com/share?url=http://www.wikicfp.com&text=WikiCFP%20:%20Call%20For%20Papers%20of%20Conferences%20,%20Workshops%20and%20Jounals"));
               startActivity(intent);
            }
        });
        Linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse( "http://www.linkedin.com/shareArticle?mini=true&url=http://www.wikicfp.com"));
                startActivity(intent);
            }
        });
        return root;
    }


}