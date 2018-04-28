package edu.temple.stockviewer;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StockDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StockDetailsFragment extends Fragment {
    final static String ARG_SYMBOL = "symbol";
    String mSymbol = "";

    public StockDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CanvasFragment.
     */
    public static StockDetailsFragment newInstance() {
        return new StockDetailsFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mSymbol = savedInstanceState.getString(ARG_SYMBOL);
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mSymbol =  savedInstanceState.getString(ARG_SYMBOL);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (savedInstanceState != null) {
            mSymbol = savedInstanceState.getString(ARG_SYMBOL);
        }
        if(getArguments() != null) {
            mSymbol = getArguments().getString(ARG_SYMBOL);
        }
        View rootView = inflater.inflate(R.layout.fragment_stock_details, container, false);
        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);
        outstate.putString(ARG_SYMBOL, mSymbol);
    }

    public void updateStockInfo(String symbol) {
        TextView stock = this.getView().findViewById(R.id.stockSymbol);
        stock.setText(symbol);
    }
}
