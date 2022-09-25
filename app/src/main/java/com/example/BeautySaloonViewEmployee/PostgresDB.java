package com.example.BeautySaloonViewEmployee;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresDB {

    public static Connection connection;

    private final String host = "192.168.1.71";
    private final String database = "CourseWorkMobile";
    private final int port = 5432;
    private final String user = "postgres";
    private final String pass = "1234";
    private String url = "jdbc:postgresql://%s:%d/%s";
    private boolean status;

    public PostgresDB() {
        this.url = String.format(this.url, this.host, this.port, this.database);
        connect();
        //this.disconnect();
        System.out.println("connection status:" + status);
    }

    private void connect() {
        Thread thread = new Thread(() -> {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(url, user, pass);
                status = true;
                System.out.println("connected:" + status);
                connection.setAutoCommit(false);
            } catch (Exception e) {
                status = false;
                System.out.print(e.getMessage());
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
            this.status = false;
        }
    }

    public void destroy() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}