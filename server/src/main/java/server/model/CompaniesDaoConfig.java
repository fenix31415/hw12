package server.model;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CompaniesDaoConfig {
    @Bean
    public ICompaniesDao companyDao() {
        return new CompaniesDao();
    }
}
