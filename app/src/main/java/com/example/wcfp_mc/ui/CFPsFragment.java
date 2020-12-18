package com.example.wcfp_mc.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
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

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class CFPsFragment extends Fragment {
    private final ArrayList<CFP> CFPList = new ArrayList<>();
    private  String CategoryURL="";
    private CFPListAdapter CLA;
    private Handler handler;
    private int page=1;
    private boolean backfromresume=false;



    public CFPsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null) {
            CategoryURL = getArguments().getString("url");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_c_f_ps, container, false);
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState){
        Activity activity=getActivity();
        if(activity!=null  ) {
            ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if (actionBar != null && getArguments()!=null) {
               actionBar.setTitle(getArguments().getString("name"));
            }
        }
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                final AppCompatActivity act = (AppCompatActivity) getActivity();
                if (act!=null && act.getSupportActionBar() != null) {
                    ProgressBar progressBar=(ProgressBar) act.findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.INVISIBLE);
                }
                switch (msg.what) {
                    case 1:
                        CLA.notifyDataSetChanged();
                        break;
                    case -1:
                        Toast.makeText(getContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        };

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.cfp_list);
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
                ++page;
                getCFPList();
            }
        });
        if(!backfromresume) {
            getCFPList();
        }
    }

    @Override
    public void onResume(){
        backfromresume=true;
        super.onResume();
    }

    private void getCFPList(){
        final AppCompatActivity act = (AppCompatActivity) getActivity();
        if (act!=null && act.getSupportActionBar() != null) {
            ProgressBar progressBar=(ProgressBar) act.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }
        getCFPListBackground();
    }

    private void getCFPListBackground() {
        new Thread(() -> {
            try {
                Document data = Jsoup.connect(CategoryURL + "&page=" + page).timeout(5000).get();
                Elements table = data.select("tbody").get(5).select("tr");
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
                    CFPList.add(newCFP);
                }
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            } catch (Exception e) {
                Message msg = new Message();
                msg.what = -1;
                msg.obj = e.toString();
                handler.sendMessage(msg);
            }
        }).start();

    }

}