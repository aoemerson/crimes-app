package io.github.aoemerson.crimesmvp.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import aoemeron.github.io.crimesmvp.R;
import io.github.aoemerson.crimesmvp.model.Crime;

/**
 * Created by Andrew on 17/08/2016.
 */
public class CrimeListViewAdapter extends ArrayAdapter<Crime> {

    private static class ViewHolder {
        TextView category;
        TextView location;
        TextView date;

    }

    public CrimeListViewAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       Crime crime = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.crime_list_item, parent, false);
            viewHolder.category = (TextView) convertView.findViewById(R.id.crime_category);
            viewHolder.location = ((TextView) convertView.findViewById(R.id.crime_location));
            viewHolder.date = ((TextView) convertView.findViewById(R.id.crime_date));
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = ((ViewHolder) convertView.getTag());
        }
        viewHolder.category.setText(crime.getCategory());
        viewHolder.location.setText(crime.getLocation().getStreet().getName());
        viewHolder.date.setText(crime.getMonthString());
        return convertView;
    }
}
