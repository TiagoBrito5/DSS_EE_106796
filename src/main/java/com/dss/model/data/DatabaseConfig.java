package com.dss.model.data;

public final class DatabaseConfig {

    public static final String HOST     = "127.0.0.1";
    public static final int    PORT     = 3306;
    public static final String DATABASE = "dss_ee";
    public static final String USER     = "root";
    public static final String PASSWORD = "Britos2005";

    public static final String URL =
        "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
        + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";

    private DatabaseConfig() {}
}
