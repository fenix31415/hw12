package client;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import client.stock.StockClientConfig;
import client.stock.StockClient;

import java.util.List;
import java.util.Map;

public class BaseTest {
    protected static final StockClient stockClient = new StockClientConfig().stockClient();

    protected static final List<String> companyNames = List.of("com1", "com2", "com3");

    protected static class TestStock {
        final String name;
        final String companyName;
        final double price;

        private TestStock(String name, String companyName, double price) {
            this.name = name;
            this.companyName = companyName;
            this.price = price;
        }
    }

    protected static final Map<String, List<TestStock>> stocks = Map.of(
            "com1", List.of(new TestStock("s1", "com1", 200.0), new TestStock("s2", "com1", 1000.0)),
            "com2", List.of(new TestStock("s2", "com2", 20.0), new TestStock("s3", "com2", 100.0)),
            "com3", List.of(new TestStock("s3", "com3", 1500.0), new TestStock("s4", "com3", 50.0))
    );

    @BeforeClass
    public static void init() {
        for (final String companyName : companyNames) {
            stockClient.POST("new-company", Map.of("name", companyName));
        }
        stocks.forEach((companyName, stocksList) -> stocksList.forEach(stock -> stockClient.POST("new-stock", Map.of(
                "name", stock.name,
                "company", stock.companyName,
                "quantity", String.valueOf(10),
                "price", String.valueOf(stock.price)))));
    }

    @ClassRule
    public static GenericContainer<?> stockWebServer
            = new FixedHostPortGenericContainer<>("stock:1.0-SNAPSHOT")
            .withFixedExposedPort(8080, 8080)
            .withExposedPorts(8080);
}
