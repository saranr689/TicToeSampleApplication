package com.saran.tictoesampleapplication.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.saran.tictoesampleapplication.R;

import java.util.List;

/**
 * Created by saran on 14/7/17.
 */

public class PopUpAdapter extends RecyclerView.Adapter<PopUpAdapter.PopUpViewHolder>{

    Context context;
    List<String> gameResultList;
    public PopUpAdapter(Context context, List<String> gameResultList) {

        this.context = context;
        this.gameResultList = gameResultList;
    }

    @Override
    public PopUpViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.pop_up_item_layout,parent,false);
        PopUpAdapter.PopUpViewHolder vh = new PopUpViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(PopUpViewHolder holder, int position) {
        holder.gameResultTetview.setText(gameResultList.get(position));

    }

    @Override
    public int getItemCount() {
        return gameResultList.size();
    }

    public class PopUpViewHolder extends RecyclerView.ViewHolder {

        TextView gameResultTetview;
        public PopUpViewHolder(View itemView) {
            super(itemView);
            gameResultTetview = (TextView)itemView.findViewById(R.id.popup_text);
        }
    }
}
