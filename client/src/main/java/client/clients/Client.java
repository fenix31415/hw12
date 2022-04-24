package client.clients;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {
    private final String name;
    private double funds;
    private final Map<String, ClientStock> stocks = new HashMap<>();

    public Client(final String name, final double funds) {
        this.name = name;
        this.funds = funds;
    }

    private void addClientStock(final ClientStock stock) {
        final String name = stock.getQualifiedName();
        final ClientStock curStock = stocks.putIfAbsent(name, stock);
        if (curStock != null) {
            curStock.changeQuantity(stock.getQuantity());
        }
    }

    private void removeClientStocks(final String stockName, final String companyName, final long quantity) {
        final String name = ClientStock.getQualifiedName(stockName, companyName);

        final ClientStock stock = stocks.get(name);
        stock.changeQuantity(quantity);

        if (stock.getQuantity() == 0) {
            stocks.remove(name);
        }
    }

    public String getName() {
        return name;
    }

    public double getFunds() {
        return funds;
    }

    public void changeFunds(final double delta) {
        funds += delta;
    }

    public List<ClientStock> getStocks() {
        return new ArrayList<>(stocks.values());
    }

    public void changeStockCount(final String stockName, final String companyName, final long quantityDelta) {
        if (quantityDelta > 0) {
            addClientStock(new ClientStock(stockName, companyName, quantityDelta));
        } else if (quantityDelta < 0) {
            removeClientStocks(stockName, companyName, quantityDelta);
        }
    }
}
