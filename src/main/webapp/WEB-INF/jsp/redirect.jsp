<%@ page pageEncoding="utf-8" contentType="text/html;charset=utf-8" %>
<%
    String url = (String) request.getAttribute("redirectUrl");

    if (url == null || url.isBlank()) {
        url = request.getContextPath() + "/";
    }

    response.sendRedirect(url);
%>
