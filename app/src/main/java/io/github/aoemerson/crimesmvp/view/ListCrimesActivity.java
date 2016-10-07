package io.github.aoemerson.crimesmvp.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.List;

import aoemeron.github.io.crimesmvp.R;
import io.github.aoemerson.crimesmvp.model.data.Crime;
import io.github.aoemerson.crimesmvp.presenter.CrimeListPresenter;
import io.github.aoemerson.crimesmvp.presenter.CrimeListPresenterImpl;

public class ListCrimesActivity extends AppCompatActivity implements CrimesView{

    private ProgressBar progressBar;
    private ListView listView;
    private CrimeListPresenter crimeListPresenter;
    private CrimeListViewAdapter crimeListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_crimes);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        listView = (ListView) findViewById(R.id.list);
        crimeListPresenter = new CrimeListPresenterImpl(this);
        crimeListViewAdapter = new CrimeListViewAdapter(this);
        listView.setAdapter(crimeListViewAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        crimeListPresenter.onRequestCrimes(52.629729f,-1.131592f);
    }

    @Override
    public void setCrimes(List<Crime> crimes) {
        crimeListViewAdapter.addAll(crimes);
        crimeListViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean isProgressShown() {
        return progressBar.getVisibility() == View.VISIBLE;
    }
}
