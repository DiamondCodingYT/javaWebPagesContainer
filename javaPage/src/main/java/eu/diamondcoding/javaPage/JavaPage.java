package eu.diamondcoding.javaPage;

import com.google.gson.JsonObject;

public class JavaPage {

    //please don't touch this variable lol
    private JsonObject pageJson;
    public void setPageJson(JsonObject pageJson) {
        this.pageJson = pageJson;
    }
    public JsonObject getPageJson() {
        return pageJson;
    }

    public void onLoad() {}

    public void onUnload() {}

    private void registerApiHandler() {

    }

}
