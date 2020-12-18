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

public class CFPListAdapter extends RecyclerView.Adapter<CFPListAdapter.ViewHolder> {

    private final ArrayList<CFP> localDataSet;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView eventTV;
        private final TextView timeTV;
        private final TextView nameTV;
        private final TextView deadlineTV;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            eventTV =  view.findViewById(R.id.eventname);
            timeTV = view.findViewById(R.id.time);
            nameTV =  view.findViewById(R.id.name);
            deadlineTV =  view.findViewById(R.id.deadline);
        }

        public TextView getEventTextView() {
            return eventTV;
        }
        public TextView getNameTextView() {
            return nameTV;
        }
        public TextView getTimeTextView() {
            return timeTV;
        }
        public TextView getDeadlineTextView() {
            return deadlineTV;
        }
    }
    public CFPListAdapter(ArrayList<CFP>  dataSet) {
        localDataSet = dataSet;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cfp_list_layout, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getEventTextView().setText(localDataSet.get(position).getEvent());
        viewHolder.getNameTextView().setText(localDataSet.get(position).getName());
        viewHolder.getTimeTextView().setText(localDataSet.get(position).getTime());
        viewHolder.getDeadlineTextView().setText(localDataSet.get(position).getDeadline());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity act=(MainActivity) view.getContext();
                NavController navController = Navigation.findNavController(act, R.id.nav_host_fragment);
                Bundle bundle = new Bundle();
                bundle.putString("url", localDataSet.get(position).getURL());
                navController.navigate(R.id.action_to_cfp,bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

}


