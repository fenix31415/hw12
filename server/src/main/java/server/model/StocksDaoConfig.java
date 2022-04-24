package server.model;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StocksDaoConfig {
    @Bean
    public IStocksDao stockDao() {
        return new StocksDao();
    }
}
