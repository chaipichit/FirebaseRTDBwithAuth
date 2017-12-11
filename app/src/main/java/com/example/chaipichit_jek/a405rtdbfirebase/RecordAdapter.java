package com.example.chaipichit_jek.a405rtdbfirebase;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FLIPFLOP on 12/10/2017.
 */

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {

    private int status;
    private List<RecordModel> recordModelList ;

    public RecordAdapter(int status, List<RecordModel> recordModelList) {
        this.status = status;

        this.recordModelList = new ArrayList<RecordModel>();
        this.recordModelList = recordModelList;

        //Log.d("FLIPFLOP" , "Record model list adapter : " + recordModelList.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_name_and_qoute, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.tvName.setText(recordModelList.get(position).getName());
        holder.tvQuote.setText(recordModelList.get(position).getQuote());

      /*  if (status == 1) {
            holder.tvQuote.setVisibility(View.GONE);
        } else {
            holder.tvQuote.setVisibility(View.VISIBLE);
        }*/
    }

    @Override
    public int getItemCount() {
        Log.d("WalksMan",recordModelList.size()+"");
        return recordModelList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvQuote;

        public ViewHolder(final View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.name);
            tvQuote = (TextView) itemView.findViewById(R.id.qoute);

        }
    }
}
