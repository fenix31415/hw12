package client.stock;

import client.Utils;
import org.springframework.http.HttpStatus;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class StockClient {

    private static class HttpResponse {
        public String response;
        public int code;
    }

    private final String url;

    public StockClient(final String url) {
        this.url = url;
    }

    private HttpResponse request(final String reqMethod, final Map<String, String> parameters, final String method) {
        try {
            final HttpResponse resp = new HttpResponse();
            final HttpURLConnection con = (HttpURLConnection) new URL(url + "/" + reqMethod).openConnection();
            con.setRequestMethod(method);
            con.setDoOutput(true);

            final DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(Utils.getParamsString(parameters));
            out.flush();
            out.close();

            resp.code = con.getResponseCode();
            resp.response = Utils.readStream(resp.code != HttpStatus.OK.value() ? con.getErrorStream() : con.getInputStream());
            con.disconnect();

            if (resp.code != HttpStatus.OK.value()) {
                throw new RuntimeException("Request failed with code " + resp.code + ": " + resp.response);
            }

            return resp;
        } catch (final IOException e) {
            throw new RuntimeException("Request failed: " + e.getMessage());
        }
    }

    public HttpResponse GET(final String reqMethod, final Map<String, String> parameters) {
        return request(reqMethod, parameters, "GET");
    }

    public HttpResponse POST(final String reqMethod, final Map<String, String> parameters) {
        return request(reqMethod, parameters, "POST");
    }

    public void modifyStock(final String stockName, final String companyName, final long quantityDelta, final double priceDelta) {
        GET("modify-stock", Map.of("name", stockName, "company", companyName,
                "qdelta", String.valueOf(quantityDelta),"pdelta", String.valueOf(priceDelta)));
    }

    public double getPrice(final String stockQualifiedName) {
        final String[] split = GET("stock-info", Map.of()).response.split(System.lineSeparator());
        for (final String line : split) {
            if (line.contains("'" + stockQualifiedName + "'")) {
                final String[] splitLine = line.split(" ");
                return Double.parseDouble(splitLine[splitLine.length - 1]);
            }
        }
        throw new IllegalArgumentException("No stock '" + stockQualifiedName + "' found");
    }
}
