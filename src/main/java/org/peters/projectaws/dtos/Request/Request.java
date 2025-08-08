package org.peters.projectaws.dtos.Request;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;


public class Request {
    String method;
    String path;
    String data;    
    int port;
    HashMap<String, String> headers;


    public Request(String method, String path, String data) throws URISyntaxException {
        this.data = data;
        this.method = method;
        this.path = path;
        headers = new HashMap<>();
        port = extractPort(path);
    }

    public Request(String method, String path, String data, HashMap<String,String> headers) throws URISyntaxException {
        this.data = data;
        this.method = method;
        this.path = path;
        this.headers = headers;
        port = extractPort(path);
    }


    /**
     * Extracts the port number from a URL.
     * @param url The URL string to extract port from
     * @return The port number as an integer, or -1 if no port is specified
     * @throws URISyntaxException if the URL string could not be parsed as a URI reference
     */
    public static int extractPortFromUrl(String url) throws URISyntaxException {
        if (url == null || url.isEmpty()) {
            return -1;
        }
        
        URI uri = new URI(url);
        int port = uri.getPort();
        
        // If port is not explicitly specified in the URL
        if (port == -1) {
            String scheme = uri.getScheme();
            if ("http".equalsIgnoreCase(scheme)) {
                return 80;
            } else if ("https".equalsIgnoreCase(scheme)) {
                return 443;
            }
        }
        
        return port;
    }

    private int extractPort(String url) {
        if (url == null || url.isEmpty()) {
            return 80;
        }

        if (url.split(":").length < 2) {
            return 80;
        }
        String port = url.split(":")[1];
        return Integer.parseInt(port);
    }

    public String getData() {
        return data;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public int getPort() {
        return port;
    }
}
