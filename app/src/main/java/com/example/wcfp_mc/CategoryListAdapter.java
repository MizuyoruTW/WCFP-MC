package com.example.wcfp_mc;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wcfp_mc.ui.CFPsFragment;

import java.util.ArrayList;
import java.util.Locale;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> {

    private final ArrayList<Category> localDataSet;
    private final SQLiteDatabase db;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final TextView textView2;
        private final CheckBox checkBox;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            textView = (TextView) view.findViewById(R.id.textView);
            textView2 = (TextView) view.findViewById(R.id.textView2);
            checkBox=(CheckBox)view.findViewById(R.id.checkBox);
        }

        public TextView getNameTextView() {
            return textView;
        }
        public TextView getCFPTextView() {
            return textView2;
        }
        public CheckBox getCheckBox(){return checkBox;}
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public CategoryListAdapter(ArrayList<Category>  dataSet, Context context) {
        localDataSet = dataSet;
        CategoryDBHelper dbHelper=new CategoryDBHelper(context);
        db=dbHelper.getReadableDatabase();
    }

    // Create new views (invoked by the layout manager)
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.category_list_layout, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        CheckBox checkBox=viewHolder.getCheckBox();
        viewHolder.getNameTextView().setText(localDataSet.get(position).getName());
        viewHolder.getCFPTextView().setText(String.format(Locale.getDefault(),"%1$d CFPs",localDataSet.get(position).getCFPs()));
        String sql=String.format(Locale.getDefault(),"SELECT * FROM favorates WHERE name='%1$s'",localDataSet.get(position).getName());
        Cursor c=db.rawQuery(sql,null);
        checkBox.setChecked(c.getCount()>0);
        c.close();
        checkBox.setOnClickListener(view -> {
            if(((CheckBox)view).isChecked()){
                ContentValues cv = new ContentValues();
                cv.put("name",localDataSet.get(position).getName());
                db.insert("favorates", null, cv);
            }else{
                db.delete("favorates", "name='"+localDataSet.get(position).getName()+"'",null);
            }
        });
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity act=(MainActivity) view.getContext();
                NavController navController = Navigation.findNavController(act, R.id.nav_host_fragment);
                Bundle bundle = new Bundle();
                bundle.putString("name", localDataSet.get(position).getName());
                bundle.putString("url", localDataSet.get(position).getUrl());

                navController.navigate(R.id.action_to_cfps,bundle);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

}


