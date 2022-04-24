package client;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import client.clients.Client;
import client.clients.IClientsDao;
import client.clients.ClientsDao;

import java.util.List;
import java.util.Random;

public class ClientTest extends BaseTest {
    private static IClientsDao clientModel;
    private static final Random random = new Random();

    @BeforeClass
    public static void prepareClientModel() {
        clientModel = new ClientsDao(stockClient);
    }

    private static String genName() {
        return "c_" + random.nextInt(1000);
    }

    private Client addClient(final String name, final double funds) {
        clientModel.addClient(new Client(name, funds));
        final Client client = clientModel.getClient(name);
        Assert.assertEquals(client.getName(), name);
        Assert.assertEquals(client.getFunds(), funds, 0.0);
        Assert.assertEquals(client.getStocks(), List.of());
        return client;
    }

    @Test
    public void testNewClientAlreadyExists() {
        final String name = genName();
        addClient(name, 0.0);
        Assert.assertThrows(IllegalArgumentException.class, () -> addClient(name, 1.0));
    }

    @Test
    public void testAddClient() {
        addClient(genName(), 10.0);
    }

    void operate(final Client client, final String sName, final String cName, final long q, final int sz, final int newQ, final double newFunds, final double total) {
        final String name = client.getName();
        clientModel.operation(name, sName, cName, q);
        Assert.assertEquals(sz, client.getStocks().size());
        if (sz == 1) Assert.assertEquals(newQ, client.getStocks().get(0).getQuantity());
        if (sz > 1) {
            Assert.assertEquals(newQ, client.getStocks().stream().filter(s ->
                    s.getName().equals(sName) && s.getCompanyName().equals(cName)).findFirst().get().getQuantity());
        }
        Assert.assertEquals(newFunds, client.getFunds(), 0.0);
        Assert.assertEquals(total, clientModel.totalValue(name), 0.0);
    }

    @Test
    public void testBuyOrSellStocks() {
        final String name = genName();
        final Client client = addClient(name, 4000.0);

        operate(client, "s1", "com1", 10, 1, 10, 2000.0, 4000.0);
        operate(client, "s1", "com1", -3, 1, 7, 2600.0, 4000.0);
        operate(client, "s1", "com1", -7, 0, 0, 4000.0, 4000.0);

        operate(client, "s1", "com1", 5, 1, 5, 3000.0, 4000.0);
        operate(client, "s3", "com2", 10, 2, 10, 2000.0, 4000.0);
        operate(client, "s3", "com3", 1, 3, 1, 500.0, 4000.0);
        operate(client, "s1", "com1", 2, 3, 7, 100.0, 4000.0);
        operate(client, "s4", "com3", 2, 4, 2, 0.0, 4000.0);
    }

    @Test
    public void testLowFunds() {
        final String name = genName();
        final Client client = addClient(name, 99.0);
        Assert.assertThrows(RuntimeException.class, () -> clientModel.operation(name, "s1", "com1", 1));
        Assert.assertEquals(99.0, client.getFunds(), 0.0);
    }

    @Test
    public void testTooManyStocks() {
        final String name = genName();
        final Client client = addClient(name, 100.0);
        Assert.assertThrows(RuntimeException.class, () -> clientModel.operation(name, "s1", "com1", 1000));
        Assert.assertEquals(100.0, client.getFunds(), 0.0);
    }

    @Test
    public void testTooManyStocks2() {
        final String name = genName();
        final Client client = addClient(name, 100.0);
        Assert.assertThrows(RuntimeException.class, () -> clientModel.operation(name, "s1", "com1", -1000));
        Assert.assertEquals(100.0, client.getFunds(), 0.0);
    }
}
