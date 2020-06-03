package com.pedropalma.examapp.view;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pedropalma.examapp.R;

public class ViewHolder extends RecyclerView.ViewHolder {
    public TextView mTitleTv, mStartDateTv;
    public View mView;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;

        //item click
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        });
        //initialize views with project_item.xml
        mTitleTv = itemView.findViewById(R.id.tView_title);
        mStartDateTv = itemView.findViewById(R.id.tView_startDate);
    }

    private ViewHolder.ClickListener mClickListener;

    //interface for click listener
    public interface ClickListener {
        //void onItemLongClick(View view, int position);
        void onItemClick(View view, int position);
    }

    public void setOnClickListener(ViewHolder.ClickListener clickListener) {
        mClickListener = clickListener;
    }

}
