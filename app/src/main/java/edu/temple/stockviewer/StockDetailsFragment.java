package edu.temple.stockviewer;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StockDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StockDetailsFragment extends Fragment {
    final static String ARG_SYMBOL = "symbol";
    String mSymbol = "";
    private BroadcastReceiver smsBroadcastReceiver;
    IntentFilter filter = new IntentFilter(StockService.ACTION_STOCK_UPDATED);

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
        smsBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("onReceive", mSymbol);
                updateStockInfo(mSymbol);
                Toast.makeText(context, "Recieved update.",Toast.LENGTH_LONG).show();
            }
        };

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
        TextView stock = rootView.findViewById(R.id.stockSymbol);
        stock.setText(mSymbol);
        ImageView image = rootView.findViewById(R.id.stock1dChart);
        String imagePath = getContext().getFilesDir() + File.separator + mSymbol + "/chart.gif";
        File imgFile = new  File(imagePath);
        image.setImageURI(null);
        image.setImageURI(Uri.fromFile(imgFile));
        image.invalidate();
        String detailsPath = getContext().getFilesDir() + File.separator + mSymbol + "/details.txt";
        File detailsFile = new  File(detailsPath);
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(detailsFile))) {
            String line ="";
            while( (line = reader.readLine()) != null){
                builder.append(line);
            }
            JSONObject stockInfo = new JSONObject(builder.toString());
            TextView companyName = rootView.findViewById(R.id.stockName);
            companyName.setText(stockInfo.getString("Name"));
            TextView stockPrice = rootView.findViewById(R.id.stockPrice);
            String price = "$"+ stockInfo.getString("LastPrice");
            stockPrice.setText(price);
        }catch( IOException | JSONException e){
            e.printStackTrace();
        }
        return rootView;
    }
    
    @Override
    public void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);
        outstate.putString(ARG_SYMBOL, mSymbol);
    }

    public void updateStockInfo(String symbol) {
        mSymbol = symbol;
        View root =  this.getView();
        Log.d("updateStockInfo", symbol);
        TextView stock = root.findViewById(R.id.stockSymbol);
        stock.setText(symbol);
        ImageView image = root.findViewById(R.id.stock1dChart);
        String imagePath = getContext().getFilesDir() + File.separator + symbol + "/chart.gif";
        File imgFile = new  File(imagePath);
        image.setImageURI(null);
        image.setImageURI(Uri.fromFile(imgFile));
        //read from deatils.txt and get the stock price and company name
        String detailsPath = getContext().getFilesDir() + File.separator + symbol + "/details.txt";
        File detailsFile = new  File(detailsPath);
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(detailsFile))) {
            String line ="";
            while( (line = reader.readLine()) != null){
                builder.append(line);
            }
            JSONObject stockInfo = new JSONObject(builder.toString());
            TextView companyName = root.findViewById(R.id.stockName);
            companyName.setText(stockInfo.getString("Name"));
            TextView stockPrice = root.findViewById(R.id.stockPrice);
            String price = "$"+ stockInfo.getString("LastPrice");
            stockPrice.setText(price);
        }catch( IOException | JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(smsBroadcastReceiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(smsBroadcastReceiver);
    }
}
