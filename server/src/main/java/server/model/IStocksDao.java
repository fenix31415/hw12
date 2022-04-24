package server.model;

import java.util.List;

public interface IStocksDao {
    void addStock(final Stock stock);

    void modifyStock(final String name, final String companyName, final long quantityDelta, final double priceDelta);

    List<Stock> getAllStocks();
}
