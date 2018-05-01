package com.kantoniak.discrete_fox.game_ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.kantoniak.discrete_fox.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RulesBoardFragment extends Fragment {

    private InteractionListener mListener;

    @BindView(R.id.checkbox_dont_show_again) CheckBox mDontShowAgainCheckbox;

    public RulesBoardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rules_board, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.button_ok)
    public void onOkButtonClick(View view) {
        mListener.onRulesRead(!mDontShowAgainCheckbox.isChecked());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof InteractionListener) {
            mListener = (InteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement InteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface InteractionListener {
        void onRulesRead(boolean showRulesAgain);
    }
}
