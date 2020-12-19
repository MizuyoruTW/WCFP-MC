package com.example.wcfp_mc.ui;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.example.wcfp_mc.CFPDBHelper;
import com.example.wcfp_mc.CFPListAdapter;
import com.example.wcfp_mc.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    private SQLiteDatabase db;
    private final ArrayList<CFP> CFPList = new ArrayList<>();
    private CFPListAdapter CLA;
    private Handler handler;


    public HistoryFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CFPDBHelper dbHelper = new CFPDBHelper(getContext());
        db = dbHelper.getReadableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }


    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
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
                }
            }
        };
        RecyclerView recyclerView = view.findViewById(R.id.historylist);
        // 設置RecyclerView為列表型態
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // 設置格線
        if (getContext() != null) {
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        }
        CLA = new CFPListAdapter(CFPList, getContext());
        recyclerView.setAdapter(CLA);
        getHistory();
    }

    private void getHistory() {
        new Thread(() -> {
            try {
                CFPList.clear();
                Cursor c = db.rawQuery("select * from history", null);
                if (c.getCount() > 0) {
                    c.moveToLast();
                    do {
                        CFP newcfp = new CFP();
                        newcfp.setEvent(c.getString(1));
                        newcfp.setName(c.getString(2));
                        newcfp.setURL(c.getString(3));
                        newcfp.setTime(c.getString(4));
                        newcfp.setDeadline(c.getString(5));
                        CFPList.add(newcfp);
                    } while (c.moveToPrevious());
                }
                c.close();
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