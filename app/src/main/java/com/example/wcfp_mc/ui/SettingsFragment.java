package com.example.wcfp_mc.ui;

        import android.content.SharedPreferences;
        import android.os.Bundle;

        import androidx.fragment.app.Fragment;

        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Switch;

        import com.example.wcfp_mc.R;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    public static final String PREFS_NAME = "MyPrefsFile";

    public SettingsFragment() {
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
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        Switch DKmodeTG=(Switch) root.findViewById(R.id.dark_mode_switch);
        DKmodeTG.setChecked(settings.getBoolean("DarkMode",false));
        DKmodeTG.setOnClickListener(view -> {
            editor.putBoolean("DarkMode",DKmodeTG.isChecked());
            editor.apply();
            getActivity().recreate();
        });
        return root;
    }
}