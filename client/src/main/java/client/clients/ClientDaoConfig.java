package client.clients;

import client.stock.StockClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientDaoConfig {
    @Bean
    public IClientsDao clientDao(final StockClient stockClient) {
        return new ClientsDao(stockClient);
    }
}
