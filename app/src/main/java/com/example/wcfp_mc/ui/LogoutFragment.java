package com.example.wcfp_mc.ui;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.wcfp_mc.R;
import com.google.android.material.navigation.NavigationView;

public class LogoutFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String PREFS_NAME = "MyPrefsFile";


    public LogoutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        Button button=view.findViewById(R.id.logout);
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(PREFS_NAME, 0).edit();
                    editor.putString("user_name","");
                    editor.putString("cookie_value","");
                    editor.apply();
                    NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
                    NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                    TextView textView= navigationView.getHeaderView(0).findViewById(R.id.nav_header_title);
                    textView.setText(getActivity().getResources().getString(R.string.nav_header_title));
                    navController.navigateUp();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        };

        button.setOnClickListener(view1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        });
    }
}