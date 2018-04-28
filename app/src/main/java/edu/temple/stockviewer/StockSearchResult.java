package edu.temple.stockviewer;

/**
 * Simple object that contains stock search result information.
 * Created by Sean Gibson on 4/28/2018.
 */

class StockSearchResult {
    private String symbol;
    private String name;
    private String exchange;
    StockSearchResult(String stockSymbol, String stockName, String stockExchange) {
        symbol = stockSymbol;
        name = stockName;
        exchange = stockExchange;
    }

    public String getName() {
        return name;
    }

    public String getExchange() {
        return exchange;
    }

    public String getSymbol() {
        return symbol;
    }
}
