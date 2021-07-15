package com.example.notice.notification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.notice.R;

import java.util.ArrayList;

public class user_adapter extends RecyclerView.Adapter<user_adapter.ExampleViewHolder> {
    private ArrayList<Item> List;
    private OnItemClickListener mListener;
    public interface OnItemClickListener {
        void onItemClick(int position);

    }
    public user_adapter(ArrayList<Item> exampleList, OnItemClickListener onItemClickListener) {
        List = exampleList;
        this.mListener = onItemClickListener;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    public static class ExampleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mTextView1;
        public TextView mTextView2;
        public TextView mTextView3;
        public TextView mTextView4;
        public ImageButton editButton;
        OnItemClickListener onItemClickListener;

        public ExampleViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);

            mTextView1 = itemView.findViewById(R.id.subject);
            mTextView2 = itemView.findViewById(R.id.message);
            mTextView3 = itemView.findViewById(R.id.date);
            mTextView4 = itemView.findViewById(R.id.postId);

            this.onItemClickListener = listener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition());

        }
    }


    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_items, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v, mListener);
        return evh;
    }
    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        Item currentItem = List.get(position);

        holder.mTextView1.setText(currentItem.getText1());
        holder.mTextView2.setText(currentItem.getText2());
        holder.mTextView3.setText(currentItem.getText3());
        holder.mTextView4.setText(currentItem.getText4());
    }
    @Override
    public int getItemCount() {
        return List.size();
    }
}