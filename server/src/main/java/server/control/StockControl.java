package server.control;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.model.ICompaniesDao;
import server.model.IStocksDao;
import server.model.Company;
import server.model.Stock;
import server.market.IMarket;
import server.market.MarketUpdater;

import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@RestController
public class StockControl {
    private final IStocksDao stockDao;
    private final ICompaniesDao companyDao;

    public StockControl(final IStocksDao stockDao, final ICompaniesDao companyDao, final IMarket marketState) {
        this.stockDao = stockDao;
        this.companyDao = companyDao;

        new MarketUpdater(marketState).start();
    }

    private ResponseEntity<?> runCommand(final Callable<String> f) {
        try {
            return new ResponseEntity<>(f.call() + System.lineSeparator(), HttpStatus.OK);
        } catch (final IllegalArgumentException e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage() + System.lineSeparator(), HttpStatus.BAD_REQUEST);
        } catch (final Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<?> runWithRes(final Runnable f, final String res) {
        try {
            f.run();
            return new ResponseEntity<>(res + System.lineSeparator(), HttpStatus.OK);
        } catch (final IllegalArgumentException e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage() + System.lineSeparator(), HttpStatus.BAD_REQUEST);
        } catch (final Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/new-company")
    public ResponseEntity<?> addCompany(@RequestParam("name") final String name) {
        return runWithRes(() -> companyDao.addCompany(new Company(name)),
                "Company '" + name + "' has been successfully added.");
    }

    @RequestMapping("/new-stock")
    public ResponseEntity<?> addStock(@RequestParam("name") final String name,
                                      @RequestParam("company") final String companyName,
                                      @RequestParam("price") final double price,
                                      @RequestParam("quantity") final long quantity) {
        return runWithRes(() -> stockDao.addStock(new Stock(name, companyName, quantity, price)),
                "New stock '" + name + "' by '" + companyName + "' has been successfully added.");
    }

    @RequestMapping("/stock-info")
    public ResponseEntity<?> stockInfo() {
        return runCommand(() ->
                stockDao.getAllStocks().stream()
                        .map(stock -> "'" + stock.getName() + ":" + stock.getCompanyName()
                                + "', quantity: " + stock.getQuantity() + ", price: " + stock.getPrice())
                        .collect(Collectors.joining(System.lineSeparator()))
        );
    }

    @RequestMapping("/modify-stock")
    public ResponseEntity<?> modifyStock(@RequestParam("name") final String name,
                                         @RequestParam("company") final String companyName,
                                         @RequestParam(name = "qdelta", required = false, defaultValue = "0") final long quantityDelta,
                                         @RequestParam(name = "pdelta", required = false, defaultValue = "0") final double priceDelta) {
        return runWithRes(() -> stockDao.modifyStock(name, companyName, quantityDelta, priceDelta),
                "Successfully modified stock '" + name + "' by '" + companyName);
    }
}
