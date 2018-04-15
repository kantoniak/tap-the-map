package com.kantoniak.discrete_fox;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.LinkedList;
import java.util.List;

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.ViewHolder> {

    static class Answer {
        public String country;
        public String value;
        public double valueRaw;
        public int color;

        public Answer(String country, String value, double valueRaw, int color) {
            this.country = country;
            this.value = value;
            this.valueRaw = valueRaw;
            this.color = color;
        }

        public double getValueRaw() {
            return valueRaw;
        }
    }

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

    public AnswersAdapter(List<Answer> dataset) {
        this.dataset.addAll(dataset);
    }

    @Override
    public AnswersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_item_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Answer answer = dataset.get(position);
        holder.color.setBackgroundColor(answer.color);
        holder.country.setText(answer.country + " (" + answer.value + ")");

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