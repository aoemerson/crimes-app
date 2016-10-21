package io.github.aoemerson.crimesmvp.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import aoemerson.github.io.crimesmvp.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.aoemerson.crimesmvp.model.data.Crime;

public class CrimesRecyclerViewAdapter extends RecyclerView.Adapter<CrimesRecyclerViewAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.crime_category) TextView categoryView;
        @BindView(R.id.crime_street) TextView streetNameView;
        @BindView(R.id.crime_date) TextView dateView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Crime crime) {
            categoryView.setText(crime.getCategory());
            streetNameView.setText(crime.getLocation().getStreet().getName());
            dateView.setText(crime.getMonthString());
        }
    }

    private List<Crime> crimes = new ArrayList<>();

    public void setCrimes(List<Crime> crimes) {
        this.crimes = crimes;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.crime_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(crimes.get(position));
    }

    @Override
    public int getItemCount() {
        return crimes.size();
    }
}
