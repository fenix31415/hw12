package client.control;

import org.springframework.web.bind.annotation.*;
import client.clients.IClientsDao;
import client.clients.Client;

import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@RestController
public class ClientControl {
    private final IClientsDao clients;

    private String runCommand(final Callable<String> callable) {
        try {
            return callable.call() + System.lineSeparator();
        } catch (final Throwable t) {
            return "An error occurred: " + t.getMessage() + System.lineSeparator();
        }
    }

    private String runWithRes(final Runnable f, final String res) {
        try {
            f.run();
            return res + System.lineSeparator();
        } catch (final Throwable t) {
            return "An error occurred: " + t.getMessage() + System.lineSeparator();
        }
    }

    public ClientControl(final IClientsDao clients) {
        this.clients = clients;
    }

    @RequestMapping("/add-user")
    public String newUser(@RequestParam("name") final String name,
                          @RequestParam(name = "funds", required = false, defaultValue = "0") final double funds) {
        return runWithRes(() -> clients.addClient(new Client(name, funds)), "Client '" + name + "' added");
    }

    @RequestMapping("/add-funds")
    public String addFunds(@RequestParam("name") final String name, @RequestParam("delta") final double delta) {
        return runWithRes(() -> clients.addFunds(name, delta), "Funds added to '" + name + "'");
    }

    @RequestMapping("/get-stocks")
    public String getStocksList(@RequestParam("name") final String name) {
        return runCommand(() ->
                clients.getClient(name).getStocks().stream()
                        .map(stock -> stock.getName() + ": " + stock.getQuantity() + " x " + clients.queryPrice(stock.getQualifiedName()))
                        .collect(Collectors.joining(System.lineSeparator()))
        );
    }

    @RequestMapping("/get-total")
    public String getTotalValue(@RequestParam("name") final String name) {
        return runCommand(() -> name + "'s value is " + clients.totalValue(name));
    }

    @RequestMapping("/buy-sell")
    public String buyOrSell(@RequestParam("name") final String name,
                            @RequestParam("stock-name") final String stockName,
                            @RequestParam("company-name") final String companyName,
                            @RequestParam("delta") final long delta) {
        return runWithRes(() -> clients.operation(name, stockName, companyName, delta),
                name + " successfully bought or sold " + delta + " units of '" + stockName + "'");
    }
}
