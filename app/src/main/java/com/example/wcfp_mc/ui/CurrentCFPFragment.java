package com.example.wcfp_mc.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wcfp_mc.CFP;
import com.example.wcfp_mc.Category;
import com.example.wcfp_mc.MainActivity;
import com.example.wcfp_mc.R;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class CurrentCFPFragment extends Fragment {
    private String url="";
    private String event="";
    private String name="";
    private String time="";
    private String deadline="";
    private String content="";
    private Handler handler;
    private ArrayList<CFP> related=new ArrayList<CFP>();
    private ArrayList<Category> categories=new ArrayList<Category>();

    public CurrentCFPFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url=getArguments().getString("url","");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_current_c_f_p, container, false);
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState){
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
                        TextView textView = view.findViewById(R.id.CFPevent);
                        textView.setText(event);
                        textView = view.findViewById(R.id.CFPname);
                        textView.setText(name);
                        textView = view.findViewById(R.id.CFPtime);
                        textView.setText(time);
                        textView = view.findViewById(R.id.CFPdeadline);
                        textView.setText(deadline);
                        textView = view.findViewById(R.id.CFPcontent);
                        textView.setText(HtmlCompat.fromHtml(content,HtmlCompat.FROM_HTML_MODE_LEGACY));
                        LinearLayout linearLayout=view.findViewById(R.id.CFPrelated);
                        linearLayout.removeAllViews();
                        for(int i=0;i<related.size();++i) {
                            textView = new TextView(getContext());
                            textView.setTextSize(20);
                            textView.setPadding(0,15,0,0);
                            String tmp="<a href=''>"+related.get(i).getEvent()+"</a>" +related.get(i).getName();
                            textView.setText(HtmlCompat.fromHtml(tmp,HtmlCompat.FROM_HTML_MODE_LEGACY));
                            textView.setTag(related.get(i).getURL());
                            textView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    MainActivity act=(MainActivity) view.getContext();
                                    NavController navController = Navigation.findNavController(act, R.id.nav_host_fragment);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("url", view.getTag().toString());
                                    navController.navigate(R.id.action_to_cfp,bundle);
                                }
                            });
                            linearLayout.addView(textView);
                        }
                        linearLayout=view.findViewById(R.id.CFPcategory);
                        linearLayout.removeAllViews();
                        for(int i=0;i<categories.size();++i) {
                            textView = new TextView(getContext());
                            textView.setTextSize(20);
                            textView.setPadding(0,15,0,0);
                            String tmp="<a href=''>"+categories.get(i).getName()+"</a>";
                            textView.setText(HtmlCompat.fromHtml(tmp,HtmlCompat.FROM_HTML_MODE_LEGACY));
                            textView.setTag(categories.get(i).getUrl());
                            textView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    MainActivity act=(MainActivity) view.getContext();
                                    NavController navController = Navigation.findNavController(act, R.id.nav_host_fragment);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("name", ((TextView)view).getText().toString());
                                    bundle.putString("url", view.getTag().toString());
                                    navController.navigate(R.id.action_to_cfps,bundle);
                                }
                            });
                            linearLayout.addView(textView);
                        }
                        break;
                    case -1:
                        Toast.makeText(getContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        System.err.println(msg.obj.toString());
                        break;
                }
            }
        };
        getContent();
    }

    private void getContent(){
        final AppCompatActivity act = (AppCompatActivity) getActivity();
        if (act.getSupportActionBar() != null) {
            ProgressBar progressBar=(ProgressBar) act.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }
        getContentBackground();
    }

    private void getContentBackground(){
        new Thread(() -> {
            try {
                related.clear();
                categories.clear();
                Document data = Jsoup.connect(url).timeout(5000).get();
                String tmp=data.select("span[property=\"v:description\"]").text();
                event=tmp.split(" : ")[0];
                name=tmp.replace(event+" : ","");
                Elements table = data.select("tbody").get(8).select("tr");
                time=table.get(0).selectFirst("td").text();
                deadline=table.get(2).selectFirst("td").text();
                content=data.selectFirst("div.cfp").html();
                table = data.select("tbody").get(9).select("tr").get(1).select("a");
                for(int i=1;i<table.size();++i){
                    Category newcat=new Category();
                    newcat.setName(table.get(i).text());
                    newcat.setUrl(table.get(i).attr("href").replace("..","http://wikicfp.com/cfp"));
                    categories.add(newcat);
                }
                Elements tbody=data.select("tbody");
                table = tbody.get(tbody.size()-3).select("tr");
                for(int i=0;i<table.size();++i){
                    Element row=table.get(i);
                    CFP newCFP=new CFP();
                    newCFP.setEvent(row.selectFirst("a").text());
                    newCFP.setURL("http://wikicfp.com"+row.selectFirst("a").attr("href"));
                    newCFP.setName(row.selectFirst("td").text().replace(newCFP.getEvent(),""));
                    related.add(newCFP);
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