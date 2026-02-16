<%@ page pageEncoding="utf-8" contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<head>
    <script src="https://kit.fontawesome.com/a28f648b35.js" crossorigin="anonymous"></script>
    <link rel="stylesheet" href="css/header.css">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>

<header class="neocinema-header-main">
    <div class="neocinema-header-tops">
        <div class="neocinema-header-menu" id="neocinema-header-menu-open-btn">
        </div>
        <div class="neocinema-header-logo">
            <h1><a href=".">NEOcinema</a></h1>
        </div>
        <div class="dummy">
        </div>
    </div>

    <div class="neocinema-header-search-wrap neocinema-header-hidden">
        <p class="phone neocinema-header-search-mess">検索<i class="fa-solid fa-magnifying-glass phone icon" id="neocinema-header-search-icon"></i></p>
        <form action="searchMovie" method="get" class="neocinema-header-search-form">
            <select id="neocinema-header-search-category" name="category">
                <option value="movieName">タイトル</option>
                <option value="genre">ジャンル</option>
                <option value="summary">あらすじ</option>
            </select>
            
            <input type="text" name="param" placeholder="例）タイタニック" class="neocinema-header-category-movieName" autocomplete="off">
            
            <select class="neocinema-header-category-genre" name="param">
                <option disabled selected value="">選択してください</option>
                <option value="アクション">アクション</option>
                <option value="冒険">冒険</option>
                <option value="アニメーション">アニメーション</option>
                <option value="コメディ">コメディ</option>
                <option value="犯罪">犯罪</option>
                <option value="ドキュメンタリー">ドキュメンタリー</option>
                <option value="ドラマ">ドラマ</option>
                <option value="ファミリー">ファミリー</option>
                <option value="ファンタジー">ファンタジー</option>
                <option value="歴史">歴史</option>
                <option value="ホラー">ホラー</option>
                <option value="音楽">音楽</option>
                <option value="ミステリー">ミステリー</option>
                <option value="恋愛">恋愛</option>
                <option value="SF">SF</option>
                <option value="テレビ映画">テレビ映画</option>
                <option value="スリラー">スリラー</option>
                <option value="戦争">戦争</option>
                <option value="西部劇">西部劇</option>
            </select>

            <input type="text" name="param" placeholder="例）楽しい" class="neocinema-header-category-summary" autocomplete="off">
            
            <button id="neocinema-header-search-submit" type="submit">検索</button>
        </form>
    </div>

    <nav class="neocinema-header-nav neocinema-header-hidden">
        <a href="." class="phone">上映スケジュール<i class="fa-solid fa-calendar-days phone icon"></i></a>
        <a href="movieData">映画一覧<i class="fa-solid fa-clapperboard phone icon"></i></a>
        <a href="price">料金案内<i class="fa-solid fa-dollar-sign phone icon"></i></a>
        <a href="news">ニュース<i class="fa-solid fa-rss phone icon"></i></a>
        <c:choose>
            <c:when test="${empty sessionScope.account}">
                <a href="account" class="neocinema-header-btn-register">新規登録<i class="fa-solid fa-person-circle-plus phone icon"></i></a>
                <a href="loginPage" class="neocinema-header-btn-register">ログイン<i class="fa-solid fa-arrow-right-to-bracket phone icon"></i></a>
            </c:when>
            <c:otherwise>
                <a href="myPage" class="neocinema-header-btn-register">マイページ<i class="fa-solid fa-circle-user phone icon"></i></a>
            </c:otherwise>
        </c:choose>
        <p class="phone neocinema-header-nav-close-btn" id="neocinema-header-nav-close-btn"><i class="fa-solid fa-angles-left phone icon"></i>close</p>
    </nav>

    <div id="neocinema-header-overlay" class="neocinema-header-hidden"></div>

    <script src="js/header.js"></script>
</header>
