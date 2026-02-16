package filter;

import jakarta.servlet.Filter;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.FilterChain;
import java.io.IOException;

public class AuthenticateFilter implements Filter{
    public void init(ServletConfig config)
        throws ServletException
    {}
    public void destory()
        throws ServletException
    {}
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException{
        HttpServletRequest req = (HttpServletRequest) request;
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("account") == null){
            req.getRequestDispatcher("WEB-INF/jsp/login.jsp").forward(request, response);
        }
        chain.doFilter(request, response); 
    }   
}
