package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private static final String DATABASE_URL = "jdbc:mysql://database-1.cg5kovhtozew.us-east-1.rds.amazonaws.com/cinema";
    private static final String USERNAME = "NEOcinema";
    private static final String PASSWORD = "destroy";
    
    private static ConnectionManager manager = null;
    private Connection cn = null;

    // 外部からのインスタンス化を防止
    private ConnectionManager() {}

    // Singletonパターンの適用
    public static ConnectionManager getInstance() {
        if (manager == null) {
            manager = new ConnectionManager();
        }
        return manager;
    }

    public Connection getConnection() {
        if (cn == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                cn = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e.getMessage(), e);
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return cn;
    }

    public void closeConnection() {
        try {
            if (cn != null && !cn.isClosed()) {
                cn.close();
            }
            cn=null;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void beginTransaction() {
        if (cn == null) {
            getConnection();
        }
        try {
            cn.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void commit() {
        try {
            cn.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void rollback() {
        try {
            cn.rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}