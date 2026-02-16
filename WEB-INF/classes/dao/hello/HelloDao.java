package dao.hello;

import java.util.List;
import java.util.Arrays;

import dao.AbstractDao;
import bean.hello.HelloBean;

public class HelloDao extends AbstractDao<HelloBean> {
    public List<HelloBean> selectAll() {
        HelloBean hello = new HelloBean();
        hello.setMess("Hello");
        return Arrays.asList(hello);
    }
}