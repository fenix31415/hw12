package client.clients;

import client.stock.StockClient;

import java.util.HashMap;
import java.util.Map;

public class ClientsDao implements IClientsDao {
    private final Map<String, Client> clients = new HashMap<>();
    private final StockClient stockClient;

    public ClientsDao(final StockClient stockClient) {
        this.stockClient = stockClient;
    }

    @Override
    public double totalValue(final String name) {
        final Client client = getClient(name);
        return client.getFunds() + client.getStocks().stream().mapToDouble(stock ->
                stock.getQuantity() * stockClient.getPrice(stock.getQualifiedName())).sum();
    }

    @Override
    public double queryPrice(final String stockName) {
        return stockClient.getPrice(stockName);
    }

    @Override
    synchronized public Client getClient(final String name) {
        final Client client = clients.get(name);
        if (client == null) {
            throw new IllegalArgumentException("Client '" + name + "' not found");
        }
        return client;
    }

    @Override
    synchronized public void addClient(final Client client) {
        if (clients.containsKey(client.getName())) {
            throw new IllegalArgumentException("Client '" + client.getName() + "' already exists");
        }

        clients.put(client.getName(), client);
    }

    @Override
    synchronized public void addFunds(final String name, final double delta) {
        getClient(name).changeFunds(delta);
    }

    private boolean hasStockQuantity(Client client, final String stockName, final String companyName, final long quantity) {
        return client.getStocks().stream()
                .filter(s -> s.getCompanyName().equals(companyName) && s.getName().equals(stockName))
                .mapToLong(ClientStock::getQuantity).sum() >= quantity;
    }

    @Override
    synchronized public void operation(final String clientName, final String stockName, final String companyName, final long quantityDelta) {
        final Client client = getClient(clientName);

        if (quantityDelta < 0 && !hasStockQuantity(client, stockName, companyName, -quantityDelta)) {
            throw new IllegalArgumentException("Not enough stocks for selling");
        }

        final double cost = queryPrice(ClientStock.getQualifiedName(stockName, companyName));
        if (client.getFunds() < cost * quantityDelta) {
            throw new IllegalArgumentException(client.getFunds() + " < " + cost * quantityDelta);
        }

        stockClient.modifyStock(stockName, companyName, -quantityDelta, 0.0);
        client.changeFunds(-cost * quantityDelta);
        client.changeStockCount(stockName, companyName, quantityDelta);
    }
}
