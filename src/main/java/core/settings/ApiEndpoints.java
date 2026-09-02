package core.settings;

import core.models.Booking;

public enum ApiEndpoints {
    PING("/ping"),
    BOOKING("/booking"); //Новый эндпоинт /booking

    private final String path;


    ApiEndpoints(String path){
        this.path=path;
    }

    public  String getPath() {
        return path;
    }
    public  String getPathByID(String id) {
        return path+"/"+id;
    }
}
