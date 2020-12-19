package com.example.wcfp_mc.ui;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.wcfp_mc.Category;
import com.example.wcfp_mc.CategoryListAdapter;
import com.example.wcfp_mc.R;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class CategoryFragment extends Fragment {
    private final ArrayList<Category> originalCL = new ArrayList<>();
    private CategoryListAdapter CLA;
    private final ArrayList<Category> CategoryList = new ArrayList<>();
    private Handler handler;
    private String filter = "";
    private int sortby = 0;
    private boolean backfromresume=false;

    public CategoryFragment() {
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
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState){
        Activity activity=getActivity();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.catagory_list);
        EditText editText = (EditText) view.findViewById(R.id.editText);
        ImageButton button = (ImageButton) view.findViewById(R.id.imageButton);
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        ListFilter();
                        CLA.notifyDataSetChanged();
                        break;
                    case -1:
                        Toast.makeText(getContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        };
        // 設置RecyclerView為列表型態
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // 設置格線
        if(getContext()!=null) {
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        }
        CLA = new CategoryListAdapter(CategoryList,getContext());
        recyclerView.setAdapter(CLA);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filter = editText.getText().toString();
                ListFilter();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        button.setOnClickListener(v -> {
            if(activity!=null) hideSoftKeyboard(activity);
            editText.clearFocus();
            filter = editText.getText().toString();
            ListFilter();
        });
        final AppCompatActivity act = (AppCompatActivity) getActivity();
        if (act!=null && act.getSupportActionBar() != null) {
            ProgressBar progressBar=(ProgressBar) act.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }
        if(!backfromresume) getCategoryList();
        setMenu(view);
    }

    @Override
    public void onResume(){
        backfromresume=true;
        super.onResume();
    }

    private void getCategoryList() {
        originalCL.clear();
        String url = "http://www.wikicfp.com/cfp/allcat?sortby=0";
        new Thread(() -> {
            try {
                Document data = Jsoup.connect(url).timeout(5000).get();
                Elements table = data.select("tbody").get(3).select("tr");
                for (int i = 3; i < table.size(); ++i) {
                    Elements row = table.get(i).select("td");
                    for (int j = 0; j < row.size(); j += 2) {
                        Category ca = new Category();
                        ca.setName(row.get(j).select("a").text());
                        ca.setCFPs(Integer.parseInt(row.get(j + 1).text()));
                        ca.setUrl("http://www.wikicfp.com" + row.get(j).selectFirst("a").attr("href"));
                        originalCL.add(ca);
                    }
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

    private void setMenu(View root) {
        final ImageButton imageButton = root.findViewById(R.id.imageButton2);
        final PopupMenu dropDownMenu = new PopupMenu(getContext(), imageButton);
        dropDownMenu.inflate(R.menu.sort);
        dropDownMenu.setOnMenuItemClickListener(item -> {
            item.setChecked(!item.isChecked());
            sortby = item.getItemId();
            ListFilter();
            return true;
        });
        imageButton.setOnClickListener(v -> dropDownMenu.show());
    }

    private void ListFilter() {
        final AppCompatActivity act = (AppCompatActivity) getActivity();
        if (act!=null && act.getSupportActionBar() != null) {
            ProgressBar progressBar=(ProgressBar) act.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }
        this.CategoryList.clear();
        for (int i = 0; i < originalCL.size(); ++i) {
            if (originalCL.get(i).getName().startsWith(filter)) {
                this.CategoryList.add(originalCL.get(i));
            }
        }
        Comparator<Category> comp = new CategoryComparatorCFP().reversed();
        if (sortby == R.id.sort_atoz) {
            comp = new CategoryComparatorAtoZ();
        } else if (sortby == R.id.sort_cfp_reverse) {
            comp = new CategoryComparatorCFP();
        } else if (sortby == R.id.sort_ztoa) {
            comp = new CategoryComparatorAtoZ().reversed();
        }
        CategoryList.sort(comp);
        CLA.notifyDataSetChanged();
        if (act!=null && act.getSupportActionBar() != null) {
            ProgressBar progressBar=(ProgressBar) act.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private static void hideSoftKeyboard(@NonNull Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}

class CategoryComparatorAtoZ implements Comparator<Category> {
    public int compare(Category c1, Category c2) {
        return c1.getName().compareTo(c2.getName());
    }
}

class CategoryComparatorCFP implements Comparator<Category> {
    public int compare(Category c1, Category c2) {
        return Integer.compare(c1.getCFPs(), c2.getCFPs());
    }
}