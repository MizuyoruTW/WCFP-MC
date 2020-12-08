package com.example.wcfp_mc.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.wcfp_mc.R;

public class HomeFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ImageButton FB = (ImageButton)root.findViewById(R.id.FBlogo);
        ImageButton Twitter = (ImageButton) root.findViewById(R.id.TTlogo);
        ImageButton Linkedin=(ImageButton)root.findViewById(R.id.LIlogo);

        FB.setOnClickListener(view -> {
            Intent intent=new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse( "http://www.facebook.com/sharer.php?u=http://www.wikicfp.com"));
                            startActivity(intent);
        });
        Twitter.setOnClickListener(view -> {
            Intent intent=new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse( "http://twitter.com/share?url=http://www.wikicfp.com&text=WikiCFP%20:%20Call%20For%20Papers%20of%20Conferences%20,%20Workshops%20and%20Jounals"));
           startActivity(intent);
        });
        Linkedin.setOnClickListener(view -> {
            Intent intent=new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse( "http://www.linkedin.com/shareArticle?mini=true&url=http://www.wikicfp.com"));
            startActivity(intent);
        });
        return root;
    }


}