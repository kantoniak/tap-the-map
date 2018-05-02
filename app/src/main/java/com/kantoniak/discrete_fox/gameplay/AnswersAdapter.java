package com.kantoniak.discrete_fox.gameplay;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kantoniak.discrete_fox.R;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.answer_item_color) public View color;
        @BindView(R.id.answer_item_country) public TextView country;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private final Resources resources;
    private final List<Answer> dataset = new LinkedList<>();

    public AnswersAdapter(Resources resources) {
        this.resources = resources;
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

        String countryName = answer.getCountry().getLocalizedName(resources);
        holder.country.setText(resources.getString(R.string.answer_country_value, countryName, answer.getValue()));
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