package pl.gov.stat.tapthemap.game_ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.gov.stat.tapthemap.BuildConfig;
import pl.gov.stat.tapthemap.R;

/**
 * Class responsible for displaying about marker fragment.
 */
public class AboutMarkerFragment extends Fragment {

    private InteractionListener mListener;

    @BindView(R.id.about_marker_desc) TextView mAboutMarkerDesc;

    public AboutMarkerFragment() {
        // Required empty public constructor
    }

    /**
     * When create view.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_marker, container, false);
        ButterKnife.bind(this, view);

        mAboutMarkerDesc.setText(getString(R.string.about_using_markers, BuildConfig.LANDING_URI));

        return view;
    }

    /**
     * When OK button clicked.
     */
    @OnClick(R.id.button_ok)
    public void onOkButtonClick(View view) {
        mListener.onAboutRead();
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
         * When about read.
         */
        void onAboutRead();
    }
}
