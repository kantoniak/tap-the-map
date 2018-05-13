package pl.gov.stat.tapthemap.game_ui;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.gov.stat.tapthemap.R;

/**
 * Class responsible for displaying the rules board fragment.
 */
public class RulesBoardFragment extends Fragment {

    private InteractionListener mListener;

    @BindView(R.id.image)
    ImageView mLeftImage;
    @BindView(R.id.checkbox_dont_show_again)
    CheckBox mDontShowAgainCheckbox;

    public RulesBoardFragment() {
        // Required empty public constructor
    }

    /**
     * When create view.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rules_board, container, false);
        ButterKnife.bind(this, view);

        // Start tap animation
        ((AnimationDrawable) mLeftImage.getDrawable()).start();

        return view;
    }

    /**
     * When OK button clicked.
     */
    @OnClick(R.id.button_ok)
    public void onOkButtonClick(View view) {
        mListener.onRulesRead(!mDontShowAgainCheckbox.isChecked());
    }

    /**
     * When attached.
     *
     * @param context Context of the application
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof InteractionListener) {
            mListener = (InteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement InteractionListener");
        }
    }

    /**
     * When detached.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Interaction listener.
     */
    public interface InteractionListener {
        /**
         * When rules read.
         *
         * @param showRulesAgain Whether to show rules
         */
        void onRulesRead(boolean showRulesAgain);
    }
}
