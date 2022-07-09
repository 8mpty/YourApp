package com.example.emptyapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class WebLinkAdapter extends RecyclerView.Adapter<WebLinkAdapter.ViewHolder> {

    private final ArrayList<LinkModal> linkModalArrayList;
    private final Context context;
    private AlertDialog alertDialog;

    public WebLinkAdapter(ArrayList<LinkModal> linkModalArrayList, Context context) {
        this.linkModalArrayList = linkModalArrayList;
        this.context = context;
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
        //Picasso.get().load(modal.getUrlIcon()).into(holder.urlIconTV);

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Nitter.url = modal.getUrlLink();
            Intent intent = new Intent(context, Nitter.class);
            v.getContext().startActivity(intent);
        });

//        holder.itemView.setOnLongClickListener(view -> {
//            AlertDialog.Builder builder = new AlertDialog.Builder(context)
//                    .setTitle("Delete Custom Site")
//                    .setPositiveButton("Ok", (dialog, which) -> DelItem(position))
//                    .setNegativeButton("Cancel", (dialog, which) -> alertDialog.dismiss());
//            alertDialog = builder.create();
//            alertDialog.show();
//            return true;
//        });

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
            //urlIconTV = itemView.findViewById(R.id.webIcon);
        }
    }
}
