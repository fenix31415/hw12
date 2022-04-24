package client.stock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StockClientConfig {
    @Bean
    public StockClient stockClient() {
        return new StockClient("http://127.0.0.1:8080");
    }
}
