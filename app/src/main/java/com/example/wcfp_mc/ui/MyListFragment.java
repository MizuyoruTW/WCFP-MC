package com.example.wcfp_mc.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wcfp_mc.CFP;
import com.example.wcfp_mc.CFPListAdapter;
import com.example.wcfp_mc.R;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class MyListFragment extends Fragment {
    public static final String PREFS_NAME = "MyPrefsFile";
    private final ArrayList<CFP> CFPList = new ArrayList<>();
    private  String MyListURL="";
    private CFPListAdapter CLA;
    private Handler handler;
    private int page=0;
    private boolean backfromresume=false;

    public MyListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getActivity()!=null) {
            SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
            String cookie = settings.getString("cookie_value", "");
            if (cookie.isEmpty()) {
                Toast.makeText(getContext(), "請先登入", Toast.LENGTH_LONG).show();
            } else {
                String ownerid = cookie.split("%")[0];
                System.out.println("owner id: " + ownerid);
                MyListURL = String.format("http://wikicfp.com/cfp/servlet/event.showlist?lownerid=%1$s&ltype=w&page=", ownerid);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_list, container, false);
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState){
        RecyclerView recyclerView = view.findViewById(R.id.mylist);
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
                        CLA.notifyDataSetChanged();
                        break;
                    case -1:
                        Toast.makeText(getContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        System.err.println(msg.obj.toString());
                }
            }
        };

        // 設置RecyclerView為列表型態
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // 設置格線
        if(getContext()!=null) {
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        }
        CLA = new CFPListAdapter(CFPList,getContext());
        recyclerView.setAdapter(CLA);
        recyclerView.setOnScrollChangeListener((view1, i, i1, i2, i3) -> {
            if (!recyclerView.canScrollVertically(1)) {
                getList();
            }
        });
        if(!MyListURL.isEmpty() && !backfromresume){
            getList();
        }
    }

    @Override
    public void onResume(){
        backfromresume=true;
        super.onResume();
    }

    private void getList(){
        final AppCompatActivity act = (AppCompatActivity) getActivity();
        if (act!=null && act.getSupportActionBar() != null) {
            ProgressBar progressBar= act.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }
        getListBackground();
    }

    private void getListBackground(){
        new Thread(() -> {
            try {
                ++page;
                Document data = Jsoup.connect(MyListURL  + page).timeout(5000).get();
                Elements table = data.select("tbody").get(8).select("tr");
                for (int i = 1; i < table.size(); i+=2) {
                    Elements first_row = table.get(i).select("td");
                    Elements second_row = table.get(i+1).select("td");
                    if(first_row.first().text().equals("Expired CFPs")){
                        --i;
                        continue;
                    }
                    CFP newCFP=new CFP();
                    newCFP.setEvent(first_row.first().selectFirst("a").text());
                    newCFP.setURL("http://wikicfp.com"+first_row.first().selectFirst("a").attr("href"));
                    newCFP.setName(first_row.get(1).text());
                    newCFP.setTime(second_row.get(0).text());
                    newCFP.setDeadline(second_row.get(2).text());
                        if(!CFPList.contains(newCFP)) {
                            CFPList.add(newCFP);
                        }
                }
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            } catch (Exception e) {
                --page;
                Message msg = new Message();
                msg.what = -1;
                msg.obj = e.toString();
                handler.sendMessage(msg);
            }
        }).start();
    }
}