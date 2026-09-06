package core.settings;

import org.apache.http.auth.AUTH;

public enum ApiEndpoints {
    PING("/ping"),
    BOOKING("/booking"), //Новый эндпоинт /booking
    AUTH ("/auth");

    private final String path;


    ApiEndpoints(String path){
        this.path=path;
    }

    public  String getPath() {
        return path;
    }
    /** Путь к конкретному ресурсу: id приходит параметром в момент вызова, а не хранится в константе. */
    public  String getPathById (int id) {
        return path + "/"+id;
    }
}
