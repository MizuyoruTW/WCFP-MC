package com.example.wcfp_mc.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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
    private String url = "";
    private String event = "";
    private String name = "";
    private String time = "";
    private String deadline = "";
    private String content = "";
    private String link = "";
    private String cookie;
    private boolean isinmylist = false;
    private Handler handler;
    private final ArrayList<CFP> related = new ArrayList<>();
    private final ArrayList<Category> categories = new ArrayList<>();
    public static final String PREFS_NAME = "MyPrefsFile";
    private boolean isloading = false;

    private String copyownerid="";
    private String eventid="";

    public CurrentCFPFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString("url", "");
        }
        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        cookie = settings.getString("cookie_value", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_current_c_f_p, container, false);
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                final AppCompatActivity act = (AppCompatActivity) getActivity();
                if (act != null && act.getSupportActionBar() != null) {
                    ProgressBar progressBar = (ProgressBar) act.findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.INVISIBLE);
                }
                isloading = false;
                switch (msg.what) {
                    case 2:
                        Toast.makeText(getContext(), "操作完成", Toast.LENGTH_LONG).show();
                        getContent();
                        break;
                    case 1:
                        showResult(view);
                        break;
                    case -1:
                        Toast.makeText(getContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        System.err.println(msg.obj.toString());
                        break;
                    case -2:
                        Toast.makeText(getContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        System.err.println(msg.obj.toString());
                        CheckBox checkBox = view.findViewById(R.id.ismylist);
                        checkBox.setChecked(!checkBox.isChecked());
                        break;
                }
            }
        };
        CheckBox checkBox = view.findViewById(R.id.ismylist);
        checkBox.setOnClickListener(view1 -> {
            if (isloading) {
                Toast.makeText(getContext(), "操作太快囉", Toast.LENGTH_LONG).show();
                checkBox.setChecked(!checkBox.isChecked());
                return;
            }
            if (checkBox.isChecked()) {
                if (cookie.isEmpty()) {
                    Toast.makeText(getContext(), "請先登入", Toast.LENGTH_LONG).show();
                    checkBox.setChecked(false);
                } else {
                    addtoMyList();
                }
            } else {
                delfromMyList();
            }
        });
        getContent();
    }

    private void getContent() {
        final AppCompatActivity act = (AppCompatActivity) getActivity();
        if (act.getSupportActionBar() != null) {
            ProgressBar progressBar = (ProgressBar) act.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }
        getContentBackground();
    }

    private void getContentBackground() {
        isloading = true;
        new Thread(() -> {
            try {
                related.clear();
                categories.clear();
                Document data = Jsoup.connect(url).cookie("accountkey", cookie).timeout(5000).get();
                //get event and name
                String tmp = data.select("span[property=\"v:description\"]").text();
                event = tmp.split(" : ")[0];
                name = tmp.replace(event + " : ", "");
                //get time and deadline
                Elements table = data.select("tbody").get(8).select("tr");
                time = table.get(0).selectFirst("td").text();
                deadline = table.get(2).selectFirst("td").text();
                //get content
                content = data.selectFirst("div.cfp").html();
                //get link
                link = data.selectFirst("a[target='_newtab']").text();
                //get categories
                table = data.select("tbody").get(9).select("tr").get(1).select("a");
                for (int i = 1; i < table.size(); ++i) {
                    Category newcat = new Category();
                    newcat.setName(table.get(i).text());
                    newcat.setUrl(table.get(i).attr("href").replace("..", "http://wikicfp.com/cfp"));
                    categories.add(newcat);
                }
                //get is in mylist
                isinmylist = (data.selectFirst("input[value='Delete']") != null);
                //get related CFPs
                Elements tbody = data.select("tbody");
                table = tbody.get(tbody.size() - 3).select("tr");
                for (int i = 0; i < table.size(); ++i) {
                    Element row = table.get(i);
                    CFP newCFP = new CFP();
                    newCFP.setEvent(row.selectFirst("a").text());
                    newCFP.setURL("http://wikicfp.com" + row.selectFirst("a").attr("href"));
                    newCFP.setName(row.selectFirst("td").text().replace(newCFP.getEvent(), ""));
                    related.add(newCFP);
                }
                copyownerid=data.selectFirst("input[name='copyownerid']").attr("value");
                eventid=data.selectFirst("input[name='eventid']").attr("value");
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

    private void addtoMyList() {
        isloading = true;
        new Thread(() -> {
            try {
                String requesturl="http://wikicfp.com/cfp/servlet/event.showcfp?eventid="+eventid+ "&copyownerid="+copyownerid;
                Document doc = Jsoup.connect(requesturl.replace("event.showcfp?", "event.copycfp?getaddress=event.showcfp&")).cookie("accountkey", cookie).timeout(5000).get();
                System.out.println(doc.toString());
                Message msg = new Message();
                msg.what = 2;
                handler.sendMessage(msg);
            } catch (Exception e) {
                Message msg = new Message();
                msg.what = -2;
                msg.obj = e.toString();
                handler.sendMessage(msg);
            }
        }).start();
    }

    private void delfromMyList() {
        new Thread(() -> {
            try {
                String requesturl="http://wikicfp.com/cfp/servlet/event.showcfp?eventid="+eventid+ "&copyownerid="+copyownerid;
                Jsoup.connect(requesturl.replace("event.showcfp?", "event.delcfp?getaddress=event.showcfp&")).cookie("accountkey", cookie).timeout(5000).get();
                Message msg = new Message();
                msg.what = 2;
                handler.sendMessage(msg);
            } catch (Exception e) {
                Message msg = new Message();
                msg.what = -2;
                msg.obj = e.toString();
                handler.sendMessage(msg);
            }
        }).start();
    }

    private void showResult(View view) {
        if(getActivity()!=null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(event);
        }else{
            return;
        }
        TextView textView = view.findViewById(R.id.CFPevent);
        CheckBox checkBox = view.findViewById(R.id.ismylist);
        LinearLayout linearLayout = view.findViewById(R.id.CFPrelated);
        textView.setText(event);
        textView = view.findViewById(R.id.CFPname);
        textView.setText(name);
        textView = view.findViewById(R.id.CFPtime);
        textView.setText(time);
        textView = view.findViewById(R.id.CFPdeadline);
        textView.setText(deadline);
        textView = view.findViewById(R.id.CFPlink);
        textView.setText(link);
        textView = view.findViewById(R.id.CFPcontent);
        textView.setText(HtmlCompat.fromHtml(content, HtmlCompat.FROM_HTML_MODE_LEGACY));
        checkBox.setChecked(isinmylist);
        //add related CFPs
        linearLayout.removeAllViews();
        for (int i = 0; i < related.size(); ++i) {
            textView = new TextView(getContext());
            textView.setTextSize(20);
            textView.setPadding(0, 15, 0, 0);
            String tmp = "<a href=''>" + related.get(i).getEvent() + "</a>" + related.get(i).getName();
            textView.setText(HtmlCompat.fromHtml(tmp, HtmlCompat.FROM_HTML_MODE_LEGACY));
            textView.setTag(related.get(i).getURL());
            textView.setOnClickListener(view12 -> {
                MainActivity act = (MainActivity) view12.getContext();
                NavController navController = Navigation.findNavController(act, R.id.nav_host_fragment);
                Bundle bundle = new Bundle();
                bundle.putString("url", view12.getTag().toString());
                navController.navigate(R.id.action_to_cfp, bundle);
            });
            linearLayout.addView(textView);
        }
        //add CFP categories
        linearLayout = view.findViewById(R.id.CFPcategory);
        linearLayout.removeAllViews();
        for (int i = 0; i < categories.size(); ++i) {
            textView = new TextView(getContext());
            textView.setTextSize(20);
            textView.setPadding(0, 15, 0, 0);
            String tmp = "<a href=''>" + categories.get(i).getName() + "</a>";
            textView.setText(HtmlCompat.fromHtml(tmp, HtmlCompat.FROM_HTML_MODE_LEGACY));
            textView.setTag(categories.get(i).getUrl());
            textView.setOnClickListener(view1 -> {
                MainActivity act = (MainActivity) view1.getContext();
                NavController navController = Navigation.findNavController(act, R.id.nav_host_fragment);
                Bundle bundle = new Bundle();
                bundle.putString("name", ((TextView) view1).getText().toString());
                bundle.putString("url", view1.getTag().toString());
                navController.navigate(R.id.action_to_cfps, bundle);
            });
            linearLayout.addView(textView);
        }
    }
}