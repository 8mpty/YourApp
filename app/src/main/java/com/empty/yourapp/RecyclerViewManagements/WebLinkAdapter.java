package com.empty.yourapp.RecyclerViewManagements;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.empty.yourapp.WebActivity;
import com.example.emptyapp.R;
import com.google.gson.Gson;

import java.util.ArrayList;

public class WebLinkAdapter extends RecyclerView.Adapter<WebLinkAdapter.ViewHolder> implements Filterable {

    private final ArrayList<LinkModal> linkModalArrayList;
    private final ArrayList<LinkModal> fullList;
    private final Context context;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public WebLinkAdapter(ArrayList<LinkModal> linkModalArrayList, Context context) {
        this.linkModalArrayList = linkModalArrayList;
        this.context = context;
        fullList = new ArrayList<>(linkModalArrayList);

        pref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = pref.edit();
    }

    @NonNull
    @Override
    public WebLinkAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WebLinkAdapter.ViewHolder holder, int position) {

        LinkModal modal = linkModalArrayList.get(position);
        holder.urlNameTV.setText(modal.getUrlName());
        holder.urlLinkTV.setText(modal.getUrlLink());
        //holder.urlIconTV.setImageBitmap(modal.getUrlIcon());

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            WebActivity.url = modal.getUrlLink();
            Intent intent = new Intent(context, WebActivity.class);
            v.getContext().startActivity(intent);

            editor.putBoolean("pref_webSearch",false);
            editor.apply();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void DelItem(int posAct) {
        linkModalArrayList.remove(posAct);
        notifyItemChanged(posAct);
        notifyItemRangeChanged(posAct, linkModalArrayList.size());
        SharedPreferences prefs = context.getSharedPreferences("weblinksshared", Context.MODE_PRIVATE);

        @SuppressLint("CommitPrefEdits")
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("links");

        Gson gson = new Gson();
        String json = gson.toJson(linkModalArrayList);
        editor.putString("links",json).apply();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return linkModalArrayList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView urlNameTV, urlLinkTV;
        //private final ImageView urlIconTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            urlNameTV = itemView.findViewById(R.id.txt_webTitle);
            urlLinkTV = itemView.findViewById(R.id.txt_webLink);
            //urlIconTV = itemView.findViewById(R.id.fav_icon);
        }
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter(){

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<LinkModal> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0) {
                filteredList.addAll(fullList);
            }else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(LinkModal linkModal : fullList){
                    if(linkModal.getUrlLink().toLowerCase().contains(filterPattern)){
                        filteredList.add(linkModal);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            linkModalArrayList.clear();
            linkModalArrayList.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };
}
