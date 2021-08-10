package eu.diamondcoding.javaPages.loader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.diamondcoding.javaPage.JavaPage;
import eu.diamondcoding.javaPages.JavaPages;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JavaPagesLoader {

    private final File pagesFolder;
    private final List<JavaPage> loadedPages;

    public JavaPagesLoader(File pagesFolder) {
        this.pagesFolder = pagesFolder;
        this.loadedPages = new ArrayList<>();
    }

    public void loadJavaPagesFromFolder() {
        File[] files = pagesFolder.listFiles();
        for (File file : Objects.requireNonNull(files)) {
            if(!file.getName().endsWith(".jar")) {
                continue;
            }
            LoadClassResult result = loadClass(file);
            JavaPage javaPage = initAsJavaPage(result.getClazz(), result.getPageJson());
            if(javaPage != null) {
                loadJavaPage(javaPage);
            }
        }
        System.out.printf("Loaded %s pages from %s!%n", files.length, pagesFolder.getAbsolutePath());
    }

    public void loadJavaPage(JavaPage javaPage) {
        try {
            javaPage.onLoad();
            loadedPages.add(javaPage);
            System.out.println("Loaded " + javaPage + "!");
        } catch (Exception exception) {
            System.err.println("Error loading JavaPage: " + javaPage);
            exception.printStackTrace();
        }
    }

    private LoadClassResult loadClass(final File dir) {
        try {
            JarFile jarFile = new JarFile(dir);
            JarEntry jarEntry = jarFile.getJarEntry("page.json");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(jarFile.getInputStream(jarEntry)));
            //read page.json to rawPageJson String
            String rawPageJson = "";
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                rawPageJson += line;
            }
            jarFile.close();
            //get as JsonObject
            JsonObject pageJson;
            try {
                pageJson = JsonParser.parseString(rawPageJson).getAsJsonObject();
            } catch (Exception exception) {
                throw new IllegalStateException("Invalid page.json: '"+rawPageJson+"'");
            }
            //get Main Class
            String main;
            try {
                main = pageJson.get("main").getAsString();
            } catch (Exception exception) {
                throw new IllegalStateException("Invalid page.json: '"+rawPageJson+"'. No valid Main Attribute!");
            }
            final URL url = dir.toURI().toURL();
            final URLClassLoader child = new URLClassLoader(new URL[] { url }, JavaPages.getInstance().getClass().getClassLoader());
            System.out.println("Loading... " + url + " (Main: " + main + ")");
            //Save Config somehow?
            Class<?> clazz = Class.forName(main, true, child);
            return new LoadClassResult(clazz, pageJson);
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    private JavaPage initAsJavaPage(Class<?> clazz, JsonObject pageJson) {
        try {
            Constructor<?> javaPageConstructor = clazz.getConstructor();
            Object javaPageObject = javaPageConstructor.newInstance();
            JavaPage javaPage = (JavaPage) javaPageObject;
            javaPage.setPageJson(pageJson);
            return javaPage;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void unloadAllJavaPages() {
        for (JavaPage javaPage : loadedPages) {
            javaPage.onUnload();
            System.out.println("Unloaded " + javaPage + "!");
        }
        loadedPages.clear();
    }

}
