package com.kantoniak.discrete_fox.ask;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kantoniak.discrete_fox.R;

import java.util.LinkedList;
import java.util.List;

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.ViewHolder> {
    private List<Answer> dataset = new LinkedList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View color;
        public TextView country;

        public ViewHolder(View itemView) {
            super(itemView);
            color = itemView.findViewById(R.id.answer_item_color);
            country = itemView.findViewById(R.id.answer_item_country);
        }
    }

    @Override
    public AnswersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_item_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Answer answer = dataset.get(position);
        holder.color.setBackgroundColor(answer.getColor());
        holder.country.setText(answer.getCountry() + " (" + answer.getValue() + ")");

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void updateAnswers(List<Answer> runs) {
        this.dataset.clear();
        this.dataset.addAll(runs);
        this.notifyDataSetChanged();
    }
}