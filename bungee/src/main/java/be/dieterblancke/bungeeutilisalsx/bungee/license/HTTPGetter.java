package be.dieterblancke.bungeeutilisalsx.bungee.license;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class HTTPGetter {
    public static final String ERROR_STRING = "Error while getting data from website!";

    public static String getSiteContent(String link) {
        URL Url;
        try {
            Url = new URL(link);
        } catch (MalformedURLException e1) {
            return ERROR_STRING;
        }
        HttpURLConnection Http;
        try {
            Http = (HttpURLConnection) Url.openConnection();
        } catch (IOException e1) {
            return ERROR_STRING;
        }
        if (Http == null) return ERROR_STRING;
        Map<String, List<String>> Header = Http.getHeaderFields();

        try {
            for (String header : Header.get(null)) {
                if (header.contains(" 302 ") || header.contains(" 301 ")) {
                    link = Header.get("Location").get(0);
                    try {
                        Url = new URL(link);
                    } catch (MalformedURLException e) {
                        return ERROR_STRING;
                    }
                    try {
                        Http = (HttpURLConnection) Url.openConnection();
                    } catch (IOException e) {
                        return ERROR_STRING;
                    }
                    Header = Http.getHeaderFields();
                }
            }
        } catch (Exception ignored) {
            return ERROR_STRING;
        }

        InputStream Stream;
        try {
            Stream = Http.getInputStream();
        } catch (IOException e) {
            return ERROR_STRING;
        }
        String Response;
        try {
            Response = GetStringFromStream(Stream);
        } catch (IOException e) {
            return ERROR_STRING;
        }
        return Response;
    }

    private static String GetStringFromStream(InputStream Stream) throws IOException {
        if (Stream != null) {
            Writer Writer = new StringWriter();

            char[] Buffer = new char[2048];
            try {
                Reader Reader = new BufferedReader(new InputStreamReader(Stream, StandardCharsets.UTF_8));
                int counter;
                while ((counter = Reader.read(Buffer)) != -1) {
                    Writer.write(Buffer, 0, counter);
                }
            } finally {
                Stream.close();
            }
            return Writer.toString();
        } else {
            return ERROR_STRING;
        }
    }
}
