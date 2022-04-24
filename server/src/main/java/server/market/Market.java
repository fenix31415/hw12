package server.market;

import server.model.IStocksDao;

import java.util.Random;

public class Market implements IMarket {
    private final static double MIN_PRICE = 0.1;
    private final static double WINDOW = 1.0;

    private final IStocksDao stocks;
    private final Random random = new Random();

    public Market(final IStocksDao stocks) {
        this.stocks = stocks;
    }

    @Override
    public void updateState() {
        stocks.getAllStocks().forEach(stock -> {
            double delta = -WINDOW / 2.0 + WINDOW * random.nextDouble();
            if (stock.getPrice() + delta < MIN_PRICE)
                delta = 0;
            stocks.modifyStock(stock.getName(), stock.getCompanyName(),0, delta);
        });
    }
}
