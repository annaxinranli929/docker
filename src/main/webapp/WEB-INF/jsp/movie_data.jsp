<%@ page pageEncoding="utf-8" contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="ja">
<head>
    <title>NEOcinema - 映画一覧</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="css/common.css">
    <link rel="stylesheet" href="css/movie_data.css">
</head>
<body>
    <jsp:include page="header.jsp" />

    <%-- 共通のメインコンテナを使用 --%>
    <main class="main-container">
        
        <%-- タイトル：common.cssの.page-titleをそのまま使用 --%>
        <c:choose>
            <c:when test="${result.isSearchResult}">
                <h2 class="page-title">検索結果：${param.param}</h2>
            </c:when>
            <c:otherwise>
                <h2 class="page-title">映画一覧</h2>
            </c:otherwise>
        </c:choose>
        
        <%-- 件数表示：タイトルのすぐ下に配置 --%>
        <p class="result-count">全 ${not empty result.data ? result.data.size() : 0} 件の作品</p>

        <c:choose>
            <c:when test="${empty result.data}">
                <div class="no-result">
                    <p>該当する映画が見つかりませんでした。</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="movie-list-grid">
                    <c:forEach var="movie" items="${result.data}">
                        <div class="movie-card">
                            <%-- 詳細へのリンク --%>
                            <c:url var="detailUrl" value="movieDetail">
                                <c:param name="id" value="${movie.movieId}" />
                                <c:if test="${param.category == 'summary'}">
                                    <c:param name="summary" value="${param.param}" />
                                </c:if>
                            </c:url>
                            <a href="${detailUrl}" class="card-overlay-link">詳細を見る</a>
                            
                            <%-- ポスター --%>
                            <div class="poster-wrapper">
                                <img src="${movie.posterUrl}" alt="${movie.movieName}" onerror="this.src='img/noimage.svg'">
                            </div>
                            
                            <%-- 映画情報 --%>
                            <div class="movie-info">
                                <div class="info-top">
                                    <div class="status-badge-container">
                                        <c:choose>
                                            <c:when test="${movie.active}">
                                                <span class="status-badge active">上映中</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="status-badge inactive">上映終了 / 近日公開</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>

                                    <div class="movie-header">
                                        <h3 class="movie-name">${movie.movieName}</h3>
                                        <span class="movie-rating">★ ${movie.evaluation != 0 ? movie.evaluation : '集計中'}</span>
                                    </div>
                                    
                                    <div class="genre-tags">
                                        <c:forEach var="genre" items="${movie.genres}">
                                            <span class="genre-tag">
                                                <a href="searchMovie?category=genre&param=${genre}">${genre}</a>
                                            </span>
                                        </c:forEach>
                                    </div>
                                </div>
                                
                                <div class="movie-action-hint">
                                    詳細を見る <span>&rsaquo;</span>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </main>
    <jsp:include page="footer.jsp" />
</body>
</html>