package pl.gov.stat.tapthemap.game_ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.easyar.Target;
import pl.gov.stat.tapthemap.R;
import pl.gov.stat.tapthemap.ar.EasyARController;

/**
 * Class that is responsible for displaying the fragment about scan.
 */
public class ScanToStartFragment extends Fragment implements EasyARController.OnScanListener {

    private EasyARController easyARController;
    private InteractionListener mListener;

    public ScanToStartFragment() {
        // Required empty public constructor
    }

    /**
     * Initialize.
     *
     * @param easyARController Easy AR controller
     */
    public void init(EasyARController easyARController) {
        this.easyARController = easyARController;
    }

    /**
     * When create view.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan_to_start, container, false);
        ButterKnife.bind(this, view);

        easyARController.setOnScanListener(this);

        return view;
    }

    /**
     * When help button clicked.
     */
    @OnClick
    public void onHelpButtonClick(View view) {
        mListener.onHelpClick();
    }

    /**
     * When tracked.
     */
    @Override
    public void onTracked(Target target) {
        easyARController.setOnScanListener(null);
        mListener.onScanned();
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
         * When scanned.
         */
        void onScanned();

        /**
         * When help clicked.
         */
        void onHelpClick();
    }
}
