<%@ page pageEncoding="utf-8" contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="ja">
<head>
    <title>NEOcinema - NEWS</title>
    <meta charset="utf-8">
    <link rel="stylesheet" href="css/common.css">
    <link rel="stylesheet" href="css/news.css">
</head>
<body>
    <jsp:include page="header.jsp" />

    <main class="main-container">
        <h2 class="page-title">ニュース</h2>

        <div class="sub-info-container">
            <a href="https://news.yahoo.co.jp" target="_blank" rel="noopener noreferrer" class="sub-info-link">
                引用元：Yahoo!ニュース（映画.com提供）
            </a>
        </div>

        <c:forEach var="news" items="${result}">
            <article class="news-card">
                <a href="${news.link}" target="_blank" rel="noopener noreferrer" class="news-card-overlay-link">記事を読む</a>

                <div class="news-image-wrapper">
                    <img src="${news.imageUrl}" alt="ニュース画像" onerror="this.src='img/noimage.png'">
                </div>
                
                <div class="news-content">
                    <span class="news-date">
                        <fmt:formatDate value="${news.date}" pattern="yyyy.MM.dd HH:mm" />
                    </span>
                    <h3 class="news-item-title">${news.title}</h3>
                    <p class="news-description">${news.description}</p>
                </div>
            </article>
        </c:forEach>
    </main>
    <jsp:include page="footer.jsp" />
</body>
</html>