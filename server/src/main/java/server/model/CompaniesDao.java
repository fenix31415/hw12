package server.model;

import java.util.ArrayList;
import java.util.List;

public class CompaniesDao implements ICompaniesDao {
    private final List<Company> companies = new ArrayList<>();

    private boolean isCompanyExists(final String name) {
        return companies.stream().anyMatch(c -> c.getName().equals(name));
    }

    @Override
    public void addCompany(final Company c) {
        if (isCompanyExists(c.getName())) {
            throw new IllegalArgumentException("Company " + c.getName() + " exists");
        }

        companies.add(c);
    }
}
