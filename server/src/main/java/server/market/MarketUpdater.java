package server.market;

public class MarketUpdater {
    private final static long WAIT_PERIOD = 1000;
    private final IMarket market;

    public MarketUpdater(final IMarket market) {
        this.market = market;
    }

    public void start() {
        Thread thread = new Thread(() -> {
            try {
                while (!Thread.interrupted()) {
                    Thread.sleep(WAIT_PERIOD);
                    market.updateState();
                }
            } catch (final InterruptedException ignored) { }
        });
        thread.start();
    }
}
