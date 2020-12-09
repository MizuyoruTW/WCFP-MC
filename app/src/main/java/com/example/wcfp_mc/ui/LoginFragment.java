package com.example.wcfp_mc.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.wcfp_mc.R;
import com.google.android.material.navigation.NavigationView;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    CookieManager cookieManager = new CookieManager();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Handler handler;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String cookies = "";

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CookieHandler.setDefault(cookieManager);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root;
        root = inflater.inflate(R.layout.fragment_login, container, false);
        TextView forgot = (TextView) root.findViewById(R.id.forgot);
        TextView account = (TextView) root.findViewById(R.id.account);
        TextView password = (TextView) root.findViewById(R.id.password);
        Button submit = (Button) root.findViewById(R.id.submitlogin);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(account.getText().toString(), password.getText().toString());
            }
        });
        forgot.setMovementMethod(LinkMovementMethod.getInstance());

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                final AppCompatActivity act = (AppCompatActivity) getActivity();
                if (act != null && act.getSupportActionBar() != null) {
                    ProgressBar progressBar = (ProgressBar) act.findViewById(R.id.progressBar);
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
                                TextView textView=(TextView)navigationView.getHeaderView(0).findViewById(R.id.nav_header_title);
                                textView.setText(String.format(Locale.getDefault(),"歡迎，%1$s", account.getText().toString()));
                                login = true;
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
        return root;
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