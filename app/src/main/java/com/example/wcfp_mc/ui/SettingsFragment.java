package com.example.wcfp_mc.ui;

        import android.content.SharedPreferences;
        import android.os.Bundle;

        import androidx.fragment.app.Fragment;

        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Switch;

        import com.example.wcfp_mc.R;

        import org.jetbrains.annotations.NotNull;

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
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState){
        if(getActivity()!=null) {
            SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            Switch DKmodeTG = (Switch) view.findViewById(R.id.dark_mode_switch);
            DKmodeTG.setChecked(settings.getBoolean("DarkMode", false));
            DKmodeTG.setOnClickListener(view1 -> {
                editor.putBoolean("DarkMode", DKmodeTG.isChecked());
                editor.apply();
                getActivity().recreate();
            });
        }
    }
}