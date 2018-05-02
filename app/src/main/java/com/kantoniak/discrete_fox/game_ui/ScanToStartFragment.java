package com.kantoniak.discrete_fox.game_ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kantoniak.discrete_fox.R;
import com.kantoniak.discrete_fox.ar.EasyARController;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.easyar.Target;

public class ScanToStartFragment extends Fragment implements EasyARController.OnScanListener {

    private EasyARController easyARController;
    private InteractionListener mListener;

    public ScanToStartFragment() {
        // Required empty public constructor
    }

    public void init(EasyARController easyARController) {
        this.easyARController = easyARController;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan_to_start, container, false);
        ButterKnife.bind(this, view);

        easyARController.setOnScanListener(this);

        return view;
    }

    @OnClick
    public void onHelpButtionClick(View view) {
        mListener.onHelpClick();
    }

    @Override
    public void onTracked(Target target) {
        easyARController.setOnScanListener(null);
        mListener.onScanned();
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
        void onScanned();
        void onHelpClick();
    }
}
