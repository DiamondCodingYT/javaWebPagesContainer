package eu.diamondcoding.javaPages;

import eu.diamondcoding.javaPages.loader.JavaPagesLoader;
import eu.diamondcoding.javaPages.managePage.ManagePage;
import eu.diamondcoding.javaPages.webserver.WebServer;
import lombok.Getter;

import java.io.File;

public class JavaPages {

    @Getter
    private static JavaPages instance;

    public void init(String[] args) {
        instance = this;

        //Start WebServer (Stuff will get moved in a config.json)
        File pk12File = new File("certificate.pk12");
        WebServer webServer = new WebServer(4443, pk12File, Secrets.storePassword, Secrets.keyPassword);
        webServer.start();

        //Load Pages
        File pagesFolder = new File("pages/");
        if(!pagesFolder.exists()) {
            pagesFolder.mkdir();
        }
        JavaPagesLoader javaPagesLoader = new JavaPagesLoader(pagesFolder);
        ManagePage managePage = new ManagePage();
        javaPagesLoader.loadJavaPage(managePage);
        javaPagesLoader.loadJavaPagesFromFolder();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down...");
            javaPagesLoader.unloadAllJavaPages();
        }));

        System.out.println("DONE");
    }

}
