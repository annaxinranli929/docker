<%@ page pageEncoding="utf-8" contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>映画管理</title>
</head>
<body>
  <h2>映画管理ページ</h2>

  <c:if test="${not empty result.message}">
    <p>${result.message}</p>
  </c:if>

  <h3>映画を追加</h3>
  <form action="adminSaveMovie" method="post">
    <input type="hidden" name="movieId" value="">
    <div>movieName: <input type="text" name="movieName"></div>
    <div>posterUrl: <input type="text" name="posterUrl"></div>
    <div>evaluation: <input type="text" name="evaluation" placeholder="0.0～5.0"></div>
    <div>summary: <input type="text" name="summary"></div>
    <div>apiId: <input type="text" name="apiId"></div>
    <div>genres: <input type="text" name="genres" placeholder="例) アクション,ドラマ"></div>
    <button type="submit">追加</button>
  </form>

  <hr>

  <h3>映画一覧（更新）</h3>

  <c:if test="${empty result.data}">
    <p>映画データがありません</p>
  </c:if>

  <c:forEach var="movie" items="${result.data}">
    <form action="adminSaveMovie" method="post" style="border:1px solid #ccc; padding:10px; margin:10px 0;">
      <div>
        movieId:
        <input type="text" name="movieId" value="${movie.movieId}" readonly>
      </div>
      <div>movieName: <input type="text" name="movieName" value="${movie.movieName}"></div>
      <div>posterUrl: <input type="text" name="posterUrl" value="${movie.posterUrl}"></div>
      <div>evaluation: <input type="text" name="evaluation" value="${movie.evaluation}"></div>
      <div>summary: <input type="text" name="summary" value="${movie.summary}"></div>
      <div>apiId: <input type="text" name="apiId" value="${movie.apiId}"></div>
      <div>
        genres:
        <input type="text" name="genres"
          value="<c:forEach var='g' items='${movie.genres}' varStatus='s'>${g}<c:if test='${!s.last}'>,</c:if></c:forEach>">
      </div>
      <button type="submit">更新</button>
    </form>
  </c:forEach>

  <p><a href="adminMovies">更新後の一覧を再表示</a></p>
</body>
</html>
