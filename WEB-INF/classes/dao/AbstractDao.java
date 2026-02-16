package dao;

import java.util.List;
import java.sql.Connection;
import bean.Bean;
import bean.SearchDataBean;

public abstract class AbstractDao<T extends Bean> {
    private Connection cn;

    public Connection getConnection() {
        return cn;
    }
    public void setConnection(Connection cn) {
        this.cn = cn;
    }

    public T selectById(int id) {
        throw new RuntimeException("selectByIdは実装してません");
    }
    public List<T> selectSearch(SearchDataBean searchData) {
        throw new RuntimeException("selectSearchは実装してません");
    }
    public List<T> selectAll() {
        throw new RuntimeException("selectAllは実装してません");
    }
    public boolean insert(T bean) {
        throw new RuntimeException("insertは実装してません");
    }
    public boolean update(T bean) {
        throw new RuntimeException("updateは実装してません");
    }
}
