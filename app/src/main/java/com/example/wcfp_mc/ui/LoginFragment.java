package com.example.wcfp_mc.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.wcfp_mc.R;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

public class LoginFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    CookieManager cookieManager = new CookieManager();
    public static final String PREFS_NAME = "MyPrefsFile";
    private Handler handler;
    private String cookies = "";

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CookieHandler.setDefault(cookieManager);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root;
        root = inflater.inflate(R.layout.fragment_login, container, false);

        return root;
    }
    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        TextView forgot = view.findViewById(R.id.forgot);
        TextView account = view.findViewById(R.id.account);
        TextView password = view.findViewById(R.id.password);
        Button submit = view.findViewById(R.id.submitlogin);
        submit.setOnClickListener(view1 -> {
            InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if(getActivity().getCurrentFocus()!=null) {
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            }
            final AppCompatActivity act = (AppCompatActivity) getActivity();
            if (act.getSupportActionBar() != null) {
                ProgressBar progressBar=(ProgressBar) act.findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }
            login(account.getText().toString(), password.getText().toString());
        });
        forgot.setMovementMethod(LinkMovementMethod.getInstance());

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                final AppCompatActivity act = (AppCompatActivity) getActivity();
                if (act != null && act.getSupportActionBar() != null) {
                    ProgressBar progressBar = act.findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.INVISIBLE);
                }
                switch (msg.what) {
                    case 1:
                        List<HttpCookie> cookieslist = cookieManager.getCookieStore().getCookies();
                        boolean login = false;
                        for (HttpCookie cookie : cookieslist) {
                            if (cookie.getName().equals("accountkey")) {
                                cookies = cookie.getValue();
                                Toast.makeText(getContext(), "歡迎，" + account.getText().toString(), Toast.LENGTH_SHORT).show();
                                NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
                                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                                TextView textView= navigationView.getHeaderView(0).findViewById(R.id.nav_header_title);
                                textView.setText(String.format(Locale.getDefault(),"歡迎，%1$s", account.getText().toString()));
                                SharedPreferences settings = getActivity(). getSharedPreferences(PREFS_NAME, 0);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("cookie_value",cookies);
                                editor.putString("user_name",account.getText().toString());
                                editor.apply();
                                login = true;
                                navController.navigateUp();
                            }
                        }
                        if (!login) {
                            Toast.makeText(getContext(), "錯誤的帳號或密碼", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case -1:
                        Toast.makeText(getContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        };
    }
    private void login(String account, String password) {
        new Thread(() -> {
            try {
                String urlParameters = "accountsel=" + account + "&password=" + password + "&keepin=on&mode=login";
                byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
                int postDataLength = postData.length;
                String request = "http://wikicfp.com/cfp/servlet/user.regin";
                URL url = new URL(request);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setDoOutput(true);
                conn.setInstanceFollowRedirects(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("charset", "utf-8");
                conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                conn.setUseCaches(false);
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.write(postData);
                conn.getContent();
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            } catch (IOException e) {
                Message msg = new Message();
                msg.what = -1;
                msg.obj = e.toString();
                handler.sendMessage(msg);
            }
        }).start();
    }
}