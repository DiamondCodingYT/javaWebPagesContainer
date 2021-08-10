package eu.diamondcoding.javaPages.webserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;

public class RootHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        //Very basic path parsing... (I know it doesn't test path traveling yet)
        String path = exchange.getRequestURI().toString();
        path = path.split("\\?")[0]; //remove parmas
        if(path.isEmpty() || path.equals("/")) {
            path = "/index.html";
        }
        if(path.startsWith("/")) {
            path = path.replaceFirst("/", "");
        }

        //get InputStream
        InputStream inputStream = null;
        File file = new File("html/"+path);
        System.out.println(file.getAbsolutePath());
        if(file.exists()) {
            inputStream = new FileInputStream(file);
        }

        if(inputStream == null) {
            textResponds(exchange, 404, "File not found.");
        } else {
            //send the response
            exchange.sendResponseHeaders(200, inputStream.available()); //headers
            //copy the stream
            OutputStream outputStream = exchange.getResponseBody();
            byte[] buffer = new byte[1024];
            int len = inputStream.read(buffer);
            while (len != -1) {
                outputStream.write(buffer, 0, len);
                len = inputStream.read(buffer);
            }
            outputStream.close();
            inputStream.close();
        }

    }

    private void textResponds(HttpExchange exchange, int code, String response) throws IOException {
        exchange.sendResponseHeaders(code, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

}
