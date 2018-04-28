package edu.temple.stockviewer;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements StockListFragment.OnFragmentInteractionListener{
    boolean twoPanes;
    static final String STATE_SYMBOL = "symbol";
    String mSymbol;
    ArrayList<String> stocks = new ArrayList<>();
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(), "received", Toast.LENGTH_SHORT).show();
            stocks.add(intent.getStringExtra("symbol"));
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSearchRequested();
            }
        });


        
        if(savedInstanceState != null) {
            mSymbol = savedInstanceState.getString(STATE_SYMBOL);
        }
        else{
            mSymbol = "";
        }
        //  Determine if only one or two panes are visible
        twoPanes = (findViewById(R.id.stockDetails) != null);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.stockList, new StockListFragment()).commit();

        if (twoPanes){
            StockDetailsFragment newFragment = new StockDetailsFragment();
            Bundle args = new Bundle();
            args.putString(StockDetailsFragment.ARG_SYMBOL, mSymbol);
            newFragment.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.stockDetails, newFragment);
            // Commit the transaction
            transaction.commit();

        }

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_SYMBOL, mSymbol);
    }

    @Override
    public void onStockSelected(String symbol) {
        mSymbol = symbol;
        if(twoPanes) {
            //In the single frag view, just update it
            StockDetailsFragment stockDetails = (StockDetailsFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.stockDetails);
            stockDetails.updateStockInfo(symbol);
        }
        else{
            // We're in the one-pane layout and must swap frags...

            StockDetailsFragment newFragment = new StockDetailsFragment();
            Bundle args = new Bundle();
            args.putString(StockDetailsFragment.ARG_SYMBOL, symbol);
            newFragment.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.stockList, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }
    }


}
