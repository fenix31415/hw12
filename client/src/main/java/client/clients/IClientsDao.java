package client.clients;

public interface IClientsDao {
    Client getClient(final String name);

    void addClient(final Client client);

    void addFunds(final String name, final double delta);

    void operation(final String name, final String stockName, final String companyName, final long quantity);

    double totalValue(final String name);

    double queryPrice(final String stockName);
}
