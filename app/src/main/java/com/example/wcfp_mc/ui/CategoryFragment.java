package com.example.wcfp_mc.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.wcfp_mc.Category;
import com.example.wcfp_mc.CategoryListAdapter;
import com.example.wcfp_mc.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private ArrayList<Category> originalCL=new ArrayList<Category> ();
    private CategoryListAdapter CLA;
    private ArrayList<Category> CategoryList=new ArrayList<Category> ();
    private Handler handler;
    private String filter="";
    private int sortby=0;

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
                        ListFilter();
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

        EditText editText=(EditText) root.findViewById(R.id.editText);

        ImageButton button=(ImageButton) root.findViewById(R.id.imageButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter=editText.getText().toString();
                ListFilter();
                Toast.makeText(getContext(),String.valueOf(CategoryList.size()),Toast.LENGTH_SHORT).show();
                CLA.notifyDataSetChanged();}
        });
        getCategoryList();
        setMenu(root);
        return root;
    }

    private void getCategoryList(){
        String url ="http://www.wikicfp.com/cfp/allcat?sortby=0" ;
            new Thread(new Runnable(){
                @Override
                public void run() {
                    try
                    {
                        Document data  = Jsoup.connect(url).get();
                        Elements table=data.select("tbody").get(3).select("tr");
                        for(int i=3;i<table.size();++i){
                            Elements row=table.get(i).select("td");
                            for(int j=0;j<row.size();j+=2){
                                Category ca=new Category();
                                ca.setName(row.get(j).select("a").text());
                                ca.setCFPs(Integer.parseInt( row.get(j+1).text()));
                                ca.setUrl("http://www.wikicfp.com" + row.get(j).selectFirst("a").attr("href"));
                                originalCL.add(ca);
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

    private void setMenu(View root){

        final ImageButton imageButton = root.findViewById(R.id.imageButton2);

        final PopupMenu dropDownMenu = new PopupMenu(getContext(), imageButton);

        dropDownMenu.inflate(R.menu.sort);


        dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                item.setChecked(!item.isChecked());
                switch (item.getItemId()) {
                    case R.id.sort_cfp:
                        sortby=0;
                        ListFilter();
                        return true;
                    case R.id.sort_atoz:
                        sortby=1;
                        ListFilter();
                        return true;
                }
                return false;
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dropDownMenu.show();
            }
        });
    }
    private void ListFilter(){
        this.CategoryList.clear();
        for(int i=0;i<originalCL.size();++i){
            if(originalCL.get(i).getName().startsWith(filter)){
                this.CategoryList.add(originalCL.get(i));
            }
        }
        if(sortby==0){
            CategoryComparatorCFP CC = new CategoryComparatorCFP();
            Collections.sort(CategoryList,CC.reversed());
            CLA.notifyDataSetChanged();
        }else{
            CategoryComparatorAtoZ CC = new CategoryComparatorAtoZ();
            Collections.sort(CategoryList,CC.reversed());
            CLA.notifyDataSetChanged();
        }
    }
}

class CategoryComparatorAtoZ implements Comparator<Category>
{
    public int compare(Category c1, Category c2)
    {
        return c1.getName().compareTo(c2.getName());
    }
}
class CategoryComparatorCFP implements Comparator<Category>
{
    public int compare(Category c1, Category c2)
    {
        return Integer.compare( c1.getCFPs(),c2.getCFPs());
    }
}