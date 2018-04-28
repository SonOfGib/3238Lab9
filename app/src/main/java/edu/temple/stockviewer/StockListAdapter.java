package edu.temple.stockviewer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Sean Gibson on 4/28/2018.
 */

class StockListAdapter extends ArrayAdapter<String> implements ListAdapter {
    private List<String> mObjects;
    StockListAdapter(Context context, ArrayList<String> stocks) {
        super(context, android.R.layout.simple_list_item_1, stocks);
        mObjects = stocks;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(view == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        final String stockSymbol = getItem(position);
        TextView tv = (TextView) view.findViewById(android.R.id.text1);
        tv.setText(stockSymbol);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StockListFragment.OnFragmentInteractionListener mListener;
                if (getContext() instanceof StockListFragment.OnFragmentInteractionListener) {
                    mListener = (StockListFragment.OnFragmentInteractionListener) getContext();
                } else {
                    throw new RuntimeException(getContext().toString()
                            + " must implement OnFragmentInteractionListener");
                }
                mListener.onStockSelected(stockSymbol);
                Log.d("stockSymbolSelected", ""+stockSymbol);
            }
        });
        return view;
    }

    public int getCount() {
        return mObjects.size();
    }

    public String getItem(int position) {
        return mObjects.get(position);
    }
}
