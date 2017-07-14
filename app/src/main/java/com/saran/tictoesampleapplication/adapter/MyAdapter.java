package com.saran.tictoesampleapplication.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.saran.tictoesampleapplication.R;
import com.saran.tictoesampleapplication.activity.TictoeGameActivity;

import java.util.ArrayList;

/**
 * Created by saran on 13/7/17.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolde> {


    private final TictoeGameActivity context;
    I_Reset i_reset;

    int i = 0;
    ArrayList<Integer> store_position = new ArrayList();
    Integer player_mark_count[] = new Integer[]{0, 0, 0, 0, 0, 0, 0, 0, 0};


    public MyAdapter(TictoeGameActivity tictoeGameActivity, I_Reset i_reset) {

        context = tictoeGameActivity;
        this.i_reset = i_reset;
    }

    @Override
    public MyViewHolde onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.tictoe_griditem, parent, false);
        MyViewHolde vh = new MyViewHolde(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(final MyViewHolde holder, final int position) {


        holder.imgview_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("i_D", "before: " + i);
                if (checkAlredyPlot(position)) {
                    store_position.add(position);

                    if (i % 2 == 0) {
                        Log.d("i_D", "if_onClick: " + i);
                        i_reset.i_player_turn("PLAYER 2");
                        holder.imgview_i.setVisibility(View.VISIBLE);
                        holder.imgview_i.setBackgroundColor(Color.WHITE);
                        i++;
                        player_mark_count[position] = 1;
                    } else {
                        Log.d("i_D", "else_onClick: " + i);
                        i_reset.i_player_turn("PLAYER 1");
                        holder.imgview_i.setVisibility(View.VISIBLE);
                        i++;
                        player_mark_count[position] = 2;
                    }

                    if (i >= 3) {
                        int player_result = checkMatch(player_mark_count);
                        Log.d("_D", "onClick: game over");
                        for (int j = 0; j < player_mark_count.length; j++) {
                            Log.d("_D", "onClick: game over" + j + "  ..." + player_mark_count[j]);
                        }
                        if (player_result != 0) {
                            if (player_result == 3 && i == 9) {
                                i_reset.i_resetGame("Matchc Draw");

                            } else if (player_result != 3) {
                                Log.d("Result_D", "onClick: " + player_result);
                                i_reset.i_resetGame("PLAYER " + player_result);
                            }
                        }
                    }
                } else {
                    Log.d("checkalredy_D", "onClick:else ");
                }
            }
        });


    }

    private boolean checkAlredyPlot(int mposition) {

        if (store_position.size() > 0) {
            for (int k = 0; k < store_position.size(); k++) {
                Log.d("store_postion_D", "checkAlredyPlot:sp :" + store_position.get(k) + "position : " + mposition + "Sp_size " + store_position.size());

                if (store_position.get(k) == mposition)
                    return false;
            }
            return true;
        }
        return true;
    }

    private int checkMatch(Integer[] player_mark_count) {

        int p = 3;
        int p1 = player_mark_count[0];
        int p2 = player_mark_count[1];
        int p3 = player_mark_count[2];
        int p4 = player_mark_count[3];
        int p5 = player_mark_count[4];
        int p6 = player_mark_count[5];
        int p7 = player_mark_count[6];
        int p8 = player_mark_count[7];
        int p9 = player_mark_count[8];

        Log.d("P_D", "checkMatch:p1 " + p1 + "  p2:" + p2 + "p3:" + p3 + " p4:" + p4 + " p5:" + p5 + " p6:" + p6 + " p7:" + p7 + " p8:" + p8 + " p9 :" + p9);

        if (p1 == p2 && p2 == p3 || p1 == p4 && p4 == p7 || p1 == p5 && p5 == p9) {
            if (p1 == 0) {
                if (p3 == p6 && p6 == p9 || p3 == p5 && p5 == p7) {
                    return p3;
                } else if (p7 == p8 && p8 == p9) {
                    return p7;
                }
            }
            p = p1;

        } else if (p2 == p5 && p5 == p8) {
            p = p2;

        } else if (p3 == p6 && p6 == p9 || p3 == p5 && p5 == p7) {
            p = p3;

        } else if (p4 == p5 && p5 == p6) {
            p = p4;
        } else if (p7 == p8 && p8 == p9) {

            p = p7;
        }


        return p;
    }

    @Override
    public int getItemCount() {
        return 9;
    }


    public class MyViewHolde extends RecyclerView.ViewHolder {
        ImageView imgview_b, imgview_i;

        public MyViewHolde(View itemView) {
            super(itemView);
            imgview_b = (ImageView) itemView.findViewById(R.id.tic_toe_image);
            imgview_i = (ImageView) itemView.findViewById(R.id.imgview_t);

        }


    }

    public interface I_Reset {

        void i_resetGame(String player);

        void i_player_turn(String player_turn);
    }
}
