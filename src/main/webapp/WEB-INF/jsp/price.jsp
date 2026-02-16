<%@ page pageEncoding="utf-8" contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="ja">
<head>
    <title>NEOcinema - 料金案内</title>
    <meta charset="utf-8">
    <link rel="stylesheet" href="css/common.css">
    <link rel="stylesheet" href="css/price.css">
</head>
<body>
    <jsp:include page="header.jsp" />

    <%-- common.cssの共通コンテナを使用 --%>
    <main class="main-container">
        <%-- common.cssの共通タイトルスタイルを使用 --%>
        <h2 class="page-title">料金案内</h2>
        
        <div class="price-table-wrapper">
            <table class="price-display-table">
                <thead>
                    <tr>
                        <th>区分</th>
                        <th>料金</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="price" items="${result}">
                        <tr>
                            <td class="category-name">${price.category}</td>
                            <td class="price-amount">￥${price.price}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </main>
    <jsp:include page="footer.jsp" />
</body>
</html>