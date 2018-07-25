package com.subratgupta.rentoo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class InterestedProfileListRecyclerViewAdapter extends RecyclerView.Adapter<InterestedProfileListRecyclerViewAdapter.ViewHolder> {
    private List<TenantDataType> mData;
    private LayoutInflater mInflater;
    private InterestedProfileListRecyclerViewAdapter.ItemClickListener mClickListener;

    // data is passed into the constructor
    InterestedProfileListRecyclerViewAdapter(Context context, List<TenantDataType> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public InterestedProfileListRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.interested_list_row, parent, false);
        return new InterestedProfileListRecyclerViewAdapter.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(InterestedProfileListRecyclerViewAdapter.ViewHolder holder, int position) {
        TenantDataType tenant = mData.get(position);
        holder.myNameView.setText("Name: "+tenant.getName());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myNameView;

        ViewHolder(View itemView) {
            super(itemView);
            myNameView = itemView.findViewById(R.id.tenant_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    TenantDataType getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(InterestedProfileListRecyclerViewAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
