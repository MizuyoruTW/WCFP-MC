package com.example.wcfp_mc.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.wcfp_mc.CategoryListAdapter;
import com.example.wcfp_mc.R;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private CategoryListAdapter CLA;
    private ArrayList<String> CategoryList=new ArrayList<String> ();
    private Handler handler;

    public CategoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoryFragment newInstance(String param1, String param2) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_category, container, false);

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what ) {
                    case 1:
                        CLA.notifyDataSetChanged();
                        break;
                    case -1:
                        Toast.makeText(getContext(),msg.obj.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        };
        recyclerView = (RecyclerView)root.findViewById(R.id.catagory_list);
        // 設置RecyclerView為列表型態
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // 設置格線
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        CLA =new CategoryListAdapter(CategoryList);
        recyclerView.setAdapter(CLA);
        getCategoryList();
        return root;
    }

    private void getCategoryList(){
        String url ="http://www.wikicfp.com/cfp/allcat?sortby=0";
            new Thread(new Runnable(){
                @Override
                public void run() {
                    try
                    {
                        Document data  = Jsoup.connect(url).get();
                        Elements table=data.select("tbody").get(3).select("tr");
                        for(int i=2;i<table.size();++i){
                            for(Element el :table.get(i).select("a")){
                                CategoryList.add(el.text());
                            }
                        }
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                    catch(Exception e)
                    {
                        Message msg = new Message();
                        msg.what = -1;
                        msg.obj=e.toString();
                        handler.sendMessage(msg);
                    }
                }
            }).start();

    }
}