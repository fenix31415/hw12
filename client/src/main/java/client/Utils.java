package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Utils {
    public static String getParamsString(Map<String, String> params) {
        final StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            result.append("&");
        }
        final String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }

    public static String readStream(final InputStream reader) throws IOException {
        final StringBuilder content = new StringBuilder();
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(reader))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine).append(System.lineSeparator());
            }
            return content.toString();
        }
    }
}
