import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import bean.account.AccountBean;

@WebServlet("/test")
public class TestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        AccountBean account = new AccountBean();
        account.setUserId(1); // test user ID
        account.setFirstName("Test");
        account.setLastName("User");
        account.setEmail("test@test.com");

        req.getSession().setAttribute("account", account);

        resp.sendRedirect(req.getContextPath() + "/");
    
    }
}
