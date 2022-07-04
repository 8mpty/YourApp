package com.example.emptyapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProgramAdapter extends RecyclerView.Adapter<ProgramAdapter.ViewHolder> {

    Context context;
    String[] webTitle, webLink;

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView rowWebTitle;
        TextView rowWebLink;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rowWebTitle = itemView.findViewById(R.id.txt_webTitle);
            rowWebLink = itemView.findViewById(R.id.txt_webLink);
        }
    }

    public ProgramAdapter (Context context, String[] webTitle, String[] webLink){
        this.context = context;
        this.webTitle = webTitle;
        this.webLink = webLink;
    }

    @NonNull
    @Override
    public ProgramAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.single_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgramAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.rowWebTitle.setText(webTitle[position]);
        holder.rowWebLink.setText(webLink[position]);

        holder.itemView.setOnClickListener(v -> {
            Nitter.url = "https://" + webLink[position] + "/";
            Intent intent = new Intent(context, Nitter.class);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return webTitle.length;
    }
}
