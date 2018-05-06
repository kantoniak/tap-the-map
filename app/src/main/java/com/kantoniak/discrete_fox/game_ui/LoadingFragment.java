package com.kantoniak.discrete_fox.game_ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kantoniak.discrete_fox.Country;
import com.kantoniak.discrete_fox.R;
import com.kantoniak.discrete_fox.gameplay.Gameplay;
import com.kantoniak.discrete_fox.gameplay.Question;
import com.kantoniak.discrete_fox.gameplay.QuestionChest;
import com.kantoniak.discrete_fox.scene.AssetLoader;
import com.kantoniak.discrete_fox.scene.CountryInstance;
import com.kantoniak.discrete_fox.scene.MapRenderer;

import org.rajawali3d.Object3D;
import org.rajawali3d.materials.textures.Texture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoadingFragment extends Fragment {

    @SuppressLint("StaticFieldLeak")
    private class LoadObjectsTask extends AsyncTask<Void, Integer, List<LoadObjectsTask.LoadedForCountry>> {

        class LoadedForCountry {
            Country country;
            Object3D countryBase;
            Object3D countryTop;
            Texture countryNameTexture;
        }

        private final MapRenderer renderer;
        private final AssetLoader loader;
        private final Collection<Country> countriesToLoad;

        /**
         * @param renderer        Map renderer
         * @param countriesToLoad List of countries to load. Constructor will make a copy.
         */
        LoadObjectsTask(MapRenderer renderer, Collection<Country> countriesToLoad) {
            this.renderer = renderer;
            this.loader = renderer.getLoader();
            this.countriesToLoad = new HashSet<>(countriesToLoad);
        }

        @Override
        protected void onPreExecute() {
            mDescTextView.setText(R.string.loading_objects);
            mProgressBar.setProgress(0);
            mProgressBar.setMax(countriesToLoad.size());
        }

        protected List<LoadObjectsTask.LoadedForCountry> doInBackground(Void... params) {
            List<LoadObjectsTask.LoadedForCountry> result = new LinkedList<>();

            for (Country country : countriesToLoad) {
                LoadedForCountry loaded = new LoadedForCountry();
                loaded.country = country;
                loaded.countryBase = loader.loadCountryBase(country);
                loaded.countryTop = loader.loadCountryTop(country);
                loaded.countryNameTexture = loader.loadCountryNameTexture(country);
                result.add(loaded);
                publishProgress(result.size());
                if (isCancelled()) break;
            }

            return result;
        }

        protected void onProgressUpdate(Integer... progress) {
            mProgressBar.setProgress(progress[0]);
        }

        protected void onPostExecute(List<LoadObjectsTask.LoadedForCountry> loaded) {
            loaded.forEach(result -> {
                CountryInstance countryInstance = new CountryInstance(result.country);
                countryInstance.initMeshes(result.countryBase, result.countryTop, result.countryNameTexture);
                renderer.addCountryInstance(countryInstance);
            });

            questionsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadQuestionsTask extends AsyncTask<Void, Void, List<Question>> {
        private List<Question> mQuestionList;

        /**
         * @param questionList
         */
        LoadQuestionsTask(List<Question> questionList) {
            this.mQuestionList = questionList;
        }

        @Override
        protected void onPreExecute() {
            mDescTextView.setText(R.string.loading_questions);
            mProgressBar.setIndeterminate(true);
        }

        protected List<Question> doInBackground(Void... params) {
            QuestionChest qc = new QuestionChest(getContext(), Gameplay.Settings.COUNTRIES_PER_QUESTION, Gameplay.Settings.QUESTIONS_PER_SERIES);
            ArrayList<Question> questions = new ArrayList<>();
            for (int i = 0; i < qc.numberOfQuestions(); i++) {
                questions.add(qc.getQuestion(i));
            }
            return questions;
        }

        protected void onPostExecute(List<Question> loaded) {
            mQuestionList.addAll(loaded);
            mListener.onLoaded();
        }
    }

    private InteractionListener mListener;
    private LoadObjectsTask loadingTask;
    private LoadQuestionsTask questionsTask;

    @BindView(R.id.progress_desc)
    TextView mDescTextView;
    @BindView(R.id.progress)
    ProgressBar mProgressBar;

    public LoadingFragment() {
        // Required empty public constructor
    }

    public void init(MapRenderer renderer, List<Question> questionList) {
        this.loadingTask = new LoadObjectsTask(renderer, Gameplay.Settings.ENABLED_COUNTRIES);
        this.questionsTask = new LoadQuestionsTask(questionList);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading, container, false);
        ButterKnife.bind(this, view);
        this.loadingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return view;
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
        void onLoaded();
    }
}
