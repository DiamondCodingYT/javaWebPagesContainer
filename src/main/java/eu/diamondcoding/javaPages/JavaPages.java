package eu.diamondcoding.javaPages;

import eu.diamondcoding.javaPages.webserver.WebServer;

import java.io.File;

public class JavaPages {

    public static void main(String[] args) {
        File pk12File = new File("certificate.pk12");
        WebServer webServer = new WebServer(4443, pk12File, Secrets.storePassword, Secrets.keyPassword);
        webServer.start();
        System.out.println("DONE");
    }

}
