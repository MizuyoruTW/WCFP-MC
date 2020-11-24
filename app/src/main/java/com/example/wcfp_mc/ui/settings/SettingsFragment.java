package com.example.wcfp_mc.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class SettingsFragment extends Fragment {

    private SettingsViewModel settingsViewModel;
    public static final String PREFS_NAME = "MyPrefsFile";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);
         View root = inflater.inflate(R.layout.fragment_settings, container, false);
        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        Switch DKmodeTG=(Switch) root.findViewById(R.id.dark_mode_switch);
        DKmodeTG.setChecked(settings.getBoolean("DarkMode",false));
        DKmodeTG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putBoolean("DarkMode",DKmodeTG.isChecked());
                editor.commit();
                getActivity().recreate();
            }
        });
        return root;
    }


}