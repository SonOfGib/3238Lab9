package edu.temple.stockviewer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Custom array adapter that generates a view for my StockSearchResult object.
 * Created by Sean Gibson on 4/28/2018.
 */
class StockSearchArrayAdapter extends ArrayAdapter<StockSearchResult> implements ListAdapter {
    private List<StockSearchResult> mObjects;

    StockSearchArrayAdapter(@NonNull Context context, @NonNull List<StockSearchResult> objects) {
        super(context,  R.layout.view_stock_result, objects);
        mObjects = objects;

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(view == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.view_stock_result, parent, false);
        }
        StockSearchResult item = getItem(position);
        if(item != null) {
            TextView tv = (TextView) view.findViewById(R.id.stockName);
            tv.setText(item.getName());
            tv = (TextView) view.findViewById(R.id.stockSymbol);
            tv.setText(item.getSymbol());
            tv = (TextView) view.findViewById(R.id.stockExchange);
            tv.setText(item.getExchange());
        }
        return view;
    }

    public int getCount() {
        return mObjects.size();
    }

    public StockSearchResult getItem(int position) {
        return mObjects.get(position);
    }
}
