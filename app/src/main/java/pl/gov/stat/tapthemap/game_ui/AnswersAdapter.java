package pl.gov.stat.tapthemap.game_ui;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.gov.stat.tapthemap.R;
import pl.gov.stat.tapthemap.gameplay.Answer;
import pl.gov.stat.tapthemap.scene.MapRenderer;

/**
 * Class responsible for displaying the answers.
 */
public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.ViewHolder> {

    /**
     * Class responsible for view holder.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View itemView;
        @BindView(R.id.answer_item_color)
        public View color;
        @BindView(R.id.answer_item_country)
        public TextView country;
        @BindView(R.id.answer_tick)
        public ImageView tick;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.itemView = itemView;
        }
    }

    private final Resources resources;
    private final MapRenderer renderer;
    private final List<Answer> dataset = new LinkedList<>();

    public AnswersAdapter(Resources resources, final MapRenderer renderer) {
        this.resources = resources;
        this.renderer = renderer;
    }

    /**
     * When create view holder.
     */
    @Override
    public AnswersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_item_row, parent, false);
        v.setOnClickListener(new CenterOnCountryFromTagTouchListener(renderer));
        return new ViewHolder(v);
    }

    /**
     * When bind view holder.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Answer answer = dataset.get(position);
        holder.itemView.setTag(answer.getCountry());

        holder.color.setBackgroundColor(answer.getColor());

        String countryName = answer.getCountry().getLocalizedName(resources);
        holder.country.setText(resources.getString(R.string.answer_country_value, countryName, answer.getValue()));

        boolean correct = answer.isCorrect();
        if (correct) {
            holder.tick.setImageDrawable(resources.getDrawable(R.drawable.ic_check_green_24dp));
        } else {
            holder.tick.setImageDrawable(resources.getDrawable(R.drawable.ic_close_red_24dp));
        }
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