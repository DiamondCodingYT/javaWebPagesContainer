package eu.diamondcoding.javaPages.loader;

import com.google.gson.JsonObject;

public class LoadClassResult {
    
    private final Class<?> clazz;
    private final JsonObject pageJson;

    public LoadClassResult(Class<?> clazz, JsonObject pageJson) {
        this.clazz = clazz;
        this.pageJson = pageJson;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public JsonObject getPageJson() {
        return pageJson;
    }

}