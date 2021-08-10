package eu.diamondcoding.javaPages.webserver;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import eu.diamondcoding.javaPages.webserver.handlers.RootHandler;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.*;
import java.security.cert.CertificateException;

public class WebServer {

    private final int port;
    private final boolean httpsEnabled;
    private File pk12File;
    private String storePassword;
    private String keyPassword;

    private HttpServer httpServer;

    public WebServer(int port) {
        this.port = port;
        this.httpsEnabled = false;
    }

    public WebServer(int port, File pk12File, String storePassword, String keyPassword) {
        this.port = port;
        this.httpsEnabled = true;
        this.pk12File = pk12File;
        this.storePassword = storePassword;
        this.keyPassword = keyPassword;
    }

    public void start() {
        //get sslContext
        SSLContext sslContext = null;
        if(httpsEnabled) {
            try {
                sslContext = createSSLContext(pk12File, storePassword, keyPassword);
            } catch (KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException | KeyManagementException | UnrecoverableKeyException e) {
                e.printStackTrace();
                return;
            }
        }
        //create http(s)Server
        try {
            if(httpsEnabled) {
                httpServer = HttpsServer.create(new InetSocketAddress(port), 0);
            } else {
                httpServer = HttpServer.create(new InetSocketAddress(port), 0);
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        // setup the HTTPS context and parameters
        if(httpsEnabled) {
            getHttpsServer().setHttpsConfigurator(getHttpsConfigurator(sslContext));
        }
        //create rootHandler
        RootHandler rootHandler = new RootHandler();
        //create contexts & start
        httpServer.createContext("/", rootHandler);
        //either more contexts will go here, or the root handler gets that logic. let's see
        httpServer.setExecutor(null); //maybe create a thread pool or smthn for this later
        httpServer.start();
        System.out.printf("Started WebServer on %s (httpsEnabled = %s)%n", port, httpsEnabled);
    }

    private SSLContext createSSLContext(File pk12File, String storePassString, String keyPassString) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {
        char[] storePassChars = storePassString.toCharArray();
        char[] keyPassChars = keyPassString.toCharArray();

        // load certificate
        System.out.println("Loading Keystore File from: " + pk12File.getAbsoluteFile());
        FileInputStream pk12FileInputStream = new FileInputStream(pk12File);
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        keystore.load(pk12FileInputStream, storePassChars);

        // setup the key manager factory
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keystore, keyPassChars);

        // setup the trust manager factory
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(keystore);

        // create ssl context
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
        return sslContext;
    }

    private HttpsConfigurator getHttpsConfigurator(SSLContext sslContext) {
        return new HttpsConfigurator(sslContext) {
            public void configure(HttpsParameters params) {
                try {
                    // initialise the SSL context
                    SSLContext c = SSLContext.getDefault();
                    SSLEngine engine = c.createSSLEngine();
                    params.setNeedClientAuth(false);
                    params.setCipherSuites(engine.getEnabledCipherSuites());
                    params.setProtocols(engine.getEnabledProtocols());
                    // Get the default parameters
                    SSLParameters defaultSSLParameters = c.getDefaultSSLParameters();
                    params.setSSLParameters(defaultSSLParameters);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.err.println("Failed to create HTTPS server");
                }
            }
        };
    }

    private HttpServer getHttpServer() {
        return httpServer;
    }

    private HttpsServer getHttpsServer() {
        return (HttpsServer) httpServer;
    }

}
