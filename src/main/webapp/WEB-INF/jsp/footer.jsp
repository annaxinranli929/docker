<%@ page pageEncoding="utf-8" contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" href="css/footer.css">
<footer class="neocinema-footer-main">
    <div class="neocinema-footer-container">
        <div class="neocinema-footer-top">
            <div class="neocinema-footer-logo">
                <a href=".">NEOcinema</a>
            </div>
            <div class="neocinema-footer-sns">
                <a href="https://x.com/?lang=ja" target="_blank"><i class="fa-brands fa-x-twitter"></i></a>
                <a href="https://www.instagram.com/" target="_blank"><i class="fa-brands fa-instagram"></i></a>
                <a href="https://www.facebook.com/?locale=ja_JP" target="_blank"><i class="fa-brands fa-facebook"></i></a>
            </div>
        </div>

        <hr class="neocinema-footer-divider">

        <div class="neocinema-footer-links">
            <div class="neocinema-footer-nav-group">
                <h4>サービス</h4>
                <ul>
                    <li><a href=".">上映スケジュール</a></li>
                    <li><a href="movieData">映画一覧</a></li>
                    <li><a href="price">料金案内</a></li>
                    <li><a href="news">ニュース</a></li>
                </ul>
            </div>
            <div class="neocinema-footer-nav-group">
                <h4>アカウント</h4>
                <ul>
                    <c:choose>
                        <c:when test="${empty sessionScope.account}">
                            <li><a href="account">新規登録</a></li>
                            <li><a href="loginPage">ログイン</a></li>
                        </c:when>
                        <c:otherwise>
                            <li><a href="myPage">マイページ</a></li>
                            <li><a href="logout">ログアウト</a></li>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </div>
        </div>

        <div class="neocinema-footer-bottom">
            <p class="neocinema-footer-str">&copy; 2026 NEOcinema All Rights Reserved.</p>
        </div>
    </div>
</footer>