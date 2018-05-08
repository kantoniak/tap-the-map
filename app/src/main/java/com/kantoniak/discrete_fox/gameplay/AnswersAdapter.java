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

/**
 * Class responsible for displaying the answers.
 */
public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.ViewHolder> {

    /**
     * Class responsible for view holder.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.answer_item_color)
        public View color;
        @BindView(R.id.answer_item_country)
        public TextView country;

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

    /**
     * When create view holder.
     */
    @Override
    public AnswersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_item_row, parent, false);
        return new ViewHolder(v);
    }

    /**
     * When bind view holder.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Answer answer = dataset.get(position);
        holder.color.setBackgroundColor(answer.getColor());

        String countryName = answer.getCountry().getLocalizedName(resources);
        holder.country.setText(resources.getString(R.string.answer_country_value, countryName, answer.getValue()));
    }

    /**
     * Get item count.
     *
     * @return Number of items
     */
    @Override
    public int getItemCount() {
        return dataset.size();
    }

    /**
     * Update answers.
     *
     * @param runs List of answers
     */
    public void updateAnswers(List<Answer> runs) {
        this.dataset.clear();
        this.dataset.addAll(runs);
        this.notifyDataSetChanged();
    }
}