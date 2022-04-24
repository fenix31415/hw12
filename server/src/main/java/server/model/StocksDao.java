package server.model;

import java.util.*;
import java.util.stream.Collectors;

public class StocksDao implements IStocksDao {
    private final Map<String, List<Stock>> stocks = new HashMap<>();

    private Optional<Stock> getStock(final String companyName, final String stockName) {
        final List<Stock> _stocks = stocks.get(companyName);
        return _stocks == null ? Optional.empty() : _stocks.stream().filter(s -> s.getName().equals(stockName)).findAny();
    }

    @Override
    public void addStock(final Stock stock) {
        final String name = stock.getCompanyName();
        if (getStock(name, stock.getName()).isPresent()) {
            throw new IllegalArgumentException("Stock '" + stock.getName() + "' is already present.");
        }

        stocks.putIfAbsent(name, new ArrayList<>());
        stocks.get(name).add(stock);
    }

    @Override
    public void modifyStock(final String stockName, final String companyName,
                              final long quantityDelta, final double priceDelta) {
        final Optional<Stock> mbStock = getStock(companyName, stockName);
        if (mbStock.isEmpty()) {
            throw new IllegalArgumentException("Stock (" + stockName + ", " + companyName + ") is not exists");
        }

        final Stock stock = mbStock.get();

        final double newPrice = stock.getPrice() + priceDelta;
        if (newPrice < 0.0) {
            throw new IllegalArgumentException("Illegal price: " + newPrice);
        }

        final long newQuantity = stock.getQuantity() + quantityDelta;
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Illegal quantity: " + newQuantity);
        }

        mbStock.get().setQuantity(newQuantity);
        mbStock.get().setPrice(newPrice);
    }

    @Override
    public List<Stock> getAllStocks() {
        return stocks.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }
}
