package io.github.aoemerson.crimesmvp.view;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import aoemerson.github.io.crimesmvp.R;
import io.github.aoemerson.crimesmvp.application.ApplicationModule;
import io.github.aoemerson.crimesmvp.model.data.Crime;
import io.github.aoemerson.crimesmvp.presenter.CrimeListPresenterImpl;
import io.github.aoemerson.crimesmvp.presenter.DaggerLocalCrimesComponent;

public class ListCrimesActivity extends BaseActivity {

    @Inject CrimeListPresenterImpl crimeListPresenter;
    private ProgressBar progressBar;
    private ListView listView;
    private CrimeListViewAdapter crimeListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_crimes);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        listView = (ListView) findViewById(R.id.list);
        crimeListViewAdapter = new CrimeListViewAdapter(this);
        listView.setAdapter(crimeListViewAdapter);
        DaggerLocalCrimesComponent.builder()
                                  .applicationModule(new ApplicationModule(getApplication()))
                                  .build()
                                  .inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        crimeListPresenter.attach(this);
        crimeListPresenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        crimeListPresenter.onStop();
        crimeListPresenter.detach();

    }

    @Override
    protected void onResume() {
        super.onResume();
        crimeListPresenter.onRequestLocalCrimes();

    }

    @Override
    public void setCrimes(List<Crime> crimes) {
        crimeListViewAdapter.addAll(crimes);
        crimeListViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void showNoCrimesMessage() {
        showError(R.string.msg_no_crimes_here);
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

    @Override
    public void showCrimesLoadingError() {
        showError(R.string.err_could_not_load_crimes);
    }

    @Override
    public void showLocationPermissionDeniedError() {
        showError(R.string.err_location_permission_denied);
    }

    @Override
    public void showLocationUnavailableError() {
        showError(R.string.err_location_not_obtained);
    }

    @Override
    public void showCurrentLocation(double lat, double lng) {

    }

    private void showError(@StringRes int errResId) {
        Toast.makeText(this, getString(errResId), Toast.LENGTH_SHORT).show();
    }

}
