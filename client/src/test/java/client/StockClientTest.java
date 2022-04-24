package client;

import org.junit.Assert;
import org.junit.Test;

public class StockClientTest extends BaseTest {

    @Test
    public void testModify() {
        stockClient.modifyStock("s1", "com1", -2, 100.0);
        Assert.assertEquals(300.0, stockClient.getPrice("s1:com1"), 0.0);
    }

    @Test
    public void testNoSuchStock() {
        Assert.assertThrows(IllegalArgumentException.class, () -> stockClient.getPrice("no-such-stock"));
        Assert.assertThrows(RuntimeException.class, () -> stockClient.modifyStock("no-stock", "no-company", 0, 0));
    }

    @Test
    public void testQueryPrice() {
        Assert.assertEquals(200.0, stockClient.getPrice("s1:com1"), 0.0);
        Assert.assertEquals(20.0, stockClient.getPrice("s2:com2"), 0.0);
        Assert.assertEquals(1500.0, stockClient.getPrice("s3:com3"), 0.0);
        Assert.assertThrows(IllegalArgumentException.class, () -> stockClient.getPrice("s4:com1"));
    }

    @Test
    public void testModifyLowQuantity() {
        Assert.assertThrows(RuntimeException.class, () -> stockClient.modifyStock("s3", "com2", -1000, 0.0));
        Assert.assertThrows(RuntimeException.class, () -> stockClient.modifyStock("s3", "com2", 0, -1000.0));
    }
}
