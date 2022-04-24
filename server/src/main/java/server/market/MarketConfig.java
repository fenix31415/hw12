package server.market;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import server.model.IStocksDao;

@Configuration
public class MarketConfig {
    @Bean
    public IMarket marketState(final IStocksDao stockDao) {
        return new Market(stockDao);
    }
}
