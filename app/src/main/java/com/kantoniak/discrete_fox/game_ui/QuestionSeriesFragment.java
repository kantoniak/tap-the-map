package com.kantoniak.discrete_fox.game_ui;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kantoniak.discrete_fox.R;
import com.kantoniak.discrete_fox.ar.ViewMatrixOverrideCamera;
import com.kantoniak.discrete_fox.ask.Answer;
import com.kantoniak.discrete_fox.ask.AnswersAdapter;
import com.kantoniak.discrete_fox.ask.Question;
import com.kantoniak.discrete_fox.ask.QuestionChest;
import com.kantoniak.discrete_fox.country.Country;
import com.kantoniak.discrete_fox.game_mechanics.Gameplay;
import com.kantoniak.discrete_fox.scene.CountryInstance;
import com.kantoniak.discrete_fox.scene.Map;
import com.kantoniak.discrete_fox.scene.MapRenderer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class QuestionSeriesFragment extends Fragment implements View.OnTouchListener {

    private InteractionListener mListener;

    @BindView(R.id.button_close) View mCloseView;
    @BindView(R.id.question) TextView mQuestionTextView;
    @BindView(R.id.round_progress) TextView mRoundProgress;
    @BindView(R.id.button_zoom_in) View mZoomIn;
    @BindView(R.id.button_zoom_out) View mZoomOut;

    @BindView(R.id.legend_high_text) TextView mHighTextView;
    @BindView(R.id.legend_mid_text) TextView mMidTextView;
    @BindView(R.id.legend_low_text) TextView mLowTextView;
    @BindView(R.id.legend_high_color) View mHighColorView;
    @BindView(R.id.legend_mid_color) View mMidColorView;
    @BindView(R.id.legend_low_color) View mLowColorView;
    @BindView(R.id.next_button_icon) ImageView mNextIcon;

    @BindView(R.id.answers_recycler)
    RecyclerView mAnswersRecycler;
    @BindView(R.id.answers_container)
    LinearLayout mAnswersContainer;

    private Gameplay gameplay;
    boolean showingAnswers = false;
    private AnswersAdapter answersAdapter;

    private ViewMatrixOverrideCamera camera;
    private Map map;
    private MapRenderer renderer;

    public QuestionSeriesFragment() {
        // Required empty public constructor
    }

    public void init(MapRenderer renderer, ViewMatrixOverrideCamera camera) {
        this.renderer = renderer;
        this.map = renderer.getMap();
        this.camera = camera;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_series, container, false);
        ButterKnife.bind(this, view);

        setupAnswersRecycler();
        startGame();

        return view;
    }

    public void setupAnswersRecycler() {
        mAnswersRecycler.setHasFixedSize(true);
        mAnswersRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        answersAdapter = new AnswersAdapter();
        mAnswersRecycler.setAdapter(answersAdapter);
    }

    public void startGame() {
        showingAnswers = false;
        mAnswersContainer.setVisibility(View.INVISIBLE);
        map.reset();
        //TODO Call generateQuestions on application start.
        gameplay = new Gameplay(generateQuestions(), Gameplay.Settings.COUNTRIES_PER_QUESTION);
        Question question = gameplay.getCurrentQuestion();
        showQuestion(question);

        showingAnswers = false;
    }

    private ArrayList<Question> generateQuestions() {
        QuestionChest qc = new QuestionChest(getResources(), getActivity(), Gameplay.Settings.COUNTRIES_PER_QUESTION, Gameplay.Settings.QUESTIONS_PER_SERIES);
        ArrayList<Question> questions = new ArrayList<>();
        for (int i = 0; i < qc.numberOfQuestions(); i++) {
            questions.add(qc.getQuestion(i));
        }
        return questions;
    }

    private void showQuestion(Question question) {
        map.reset();

        mQuestionTextView.setText(question.getDesc());
        mRoundProgress.setText("" + (gameplay.getCurrentQuestionInt() + 1) + "/" + gameplay.NUMBEROFQUESTIONS);

        mHighTextView.setText(question.getMaxLabel());
        mMidTextView.setText(question.getMidLabel());
        mLowTextView.setText(question.getMinLabel());

        int maxColor = question.getCategory().getMaxColor();
        int minColor = question.getCategory().getMinColor();
        int midColor = ColorUtils.blendARGB(minColor, maxColor, 0.5f);

        map.setColors(minColor, maxColor);

        mHighColorView.setBackgroundColor(maxColor);
        mMidColorView.setBackgroundColor(midColor);
        mLowColorView.setBackgroundColor(minColor);
        // TODO mp3 question
        try {
            int objFileId = getResources().getIdentifier("q" + String.valueOf(gameplay.getCurrentQuestionInt() + 1), "raw", getActivity().getPackageName());
            MediaPlayer mp = MediaPlayer.create(getActivity(), objFileId);
            mp.start();
        } catch (Exception e) {

        }
    }

    @OnClick(R.id.button_close)
    public void buttonClose() {
        mListener.onCloseTriggered();
    }

    @OnClick(R.id.button_zoom_in)
    public void zoomIn() {
        camera.zoomIn();
    }

    @OnClick(R.id.button_zoom_out)
    public void zoomOut() {
        camera.zoomOut();
    }

    @OnClick(R.id.next_button)
    public void onNextButtonClick(View view) {
        if (showingAnswers) {
            mNextIcon.setImageResource(R.drawable.ic_done_24dp);
            showingAnswers = false;
            mAnswersContainer.setVisibility(View.INVISIBLE);

            Question nextQuestion = gameplay.finishQuestion(getActivity());
            if (nextQuestion == null) {
                showingAnswers = false;
                mListener.onSeriesDone(gameplay.getResult(), gameplay.getMaxResult());
                return;
            }
            showQuestion(nextQuestion);
        } else {
            gameplay.updateScore(map);
            Question currentQuestion = gameplay.getCurrentQuestion();
            List<Country> countries = currentQuestion.getCountries();
            List<Answer> answers = currentQuestion.getAnswers();
            answers.sort(Comparator.comparing(Answer::getValueRaw).reversed());
            answersAdapter.updateAnswers(answers);

            mAnswersContainer.setVisibility(View.VISIBLE);
            mNextIcon.setImageResource(R.drawable.ic_trending_flat_24dp);
            showingAnswers = true;
            for (Country country : countries) {
                map.enableCountry(country);
                CountryInstance countryInstance = map.getCountry(country);
                int targetHeight = currentQuestion.getCorrectAnswer(country);
                countryInstance.setHeight(targetHeight);
            }
        }
    }

    public boolean onTouch(View view, MotionEvent event) {
        if (!showingAnswers) {
            renderer.onTouchEvent(event);
        }
        return false;
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
        void onCloseTriggered();
        void onSeriesDone(int score, int outOf);
    }
}
