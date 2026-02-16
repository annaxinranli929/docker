<%@ page pageEncoding="utf-8" contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>TMDb検索（管理人）</title>
  <style>
    body { font-family: sans-serif; margin: 16px; background:#fafafa; }
    .container { max-width: 1100px; margin: 0 auto; padding-top: 10px; }

    .searchbar{ display:flex; gap:10px; align-items:center; }
    .search-input{
      flex: 1;
      padding: 10px 12px;
      border:1px solid #ccc;
      border-radius: 10px;
      font-size: 14px;
    }
    .btn{
      padding: 8px 12px;
      border: 1px solid #333;
      background:#fff;
      border-radius: 10px;
      cursor:pointer;
    }
    .btn:hover{ background:#f2f2f2; }

    .btn-primary{
      border-color:#bbb;
      background:#fff;
      color:#333;
    }
    .btn-primary:hover{ background:#00d4ff; }

    .btn-ghost{
      border-color:#bbb;
      color:#333;
      background:#fff;
    }

    .card{
      border:1px solid #ddd;
      border-radius: 12px;
      padding: 14px;
      background:#fff;
      margin-top: 14px;
    }

    .card-selected{
      border:1px solid #d8d8ff;
      background:#fbfbff;
    }

    h2{ margin: 6px 0 10px; }
    hr{ border:none; border-top:1px solid #eee; margin: 14px 0; }

    .admin-badge{ position: fixed; top: 12px; right: 14px; z-index: 9999;
      border: 1px solid #ccc;
      background: #fff;
      padding: 6px 10px;
      border-radius: 999px;
      font-size: 13px;
    }

    .admin-badge a{ text-decoration: none; }
    .admin-badge .sep{ margin: 0 6px; color: #999; }

    .dot{
      display: inline-block;
      width: 9px;
      height: 9px;
      border-radius: 50%;
      margin-right: 6px;
      vertical-align: middle;
    }

    .dot.ok{ background: #2ecc71; }  
    .dot.ng{ background: #e74c3c; }  

    .movie-card{
      border:1px solid #eee;
      border-radius: 12px;
      padding: 12px;
      margin: 10px 0;
      display:flex;
      gap: 12px;
      background:#fff;
    }

    .poster{
      width: 92px;
      border-radius: 8px;
    }

    .overview{
      color:#333;
      font-size: 13px;
      line-height: 1.45;
      max-width: 760px;

      /* clamp lines */
      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    
    .muted { color: #666; font-size: 12px; }
    .error { color: #d00; }
    .ok { color: #0a0; }
    .btn { padding: 6px 10px; cursor: pointer; }
    .section { margin-top: 18px; }
    .box { border: 1px solid #ccc; padding: 10px; }
    label { display: inline-block; min-width: 70px; }
  </style>
</head>
<body>
  <div class="admin-badge">
    <c:choose>
      <c:when test="${sessionScope.isAdmin}">
        <span class="dot ok"></span> Admin: LOGIN
        <span class="sep">|</span>
        <a href="adminSchedules">スケジュール管理</a>
      </c:when>
      <c:otherwise>
        <span class="dot ng"></span> Admin: LOGOUT |
        <a href="adminLogin">Login</a>
      </c:otherwise>
    </c:choose>
  </div>

  <div class="container">
  <h2>TMDb検索（映画情報）</h2>

  <c:if test="${not empty result.message}">
    <p class="${result.ok ? 'ok' : 'error'}">${result.message}</p>
  </c:if>

  <!-- Search -->
  <div class="card">
    <form action="adminImportSchedule" method="get" class="searchbar">
      <input type="hidden" name="action" value="search">
      <input type="text" name="query" value="${result.query}"
            placeholder="例：インセプション / 君の名は"
            class="search-input">
      <button class="btn" type="submit">検索</button>
    </form>
    <div class="muted" style="margin-top:6px;">
      ※ TMDb APIを使用しています（TMDbの公式製品ではありません）
    </div>
  </div>

  <!-- Selected movie section -->
  <c:if test="${not empty result.selected}">
    <hr>

    <div class="section">
      <div class="card card-selected">
      <h3>選択中の映画</h3>

        <div class="movie-card">
          <div>
            <c:if test="${not empty result.selected.posterUrl}">
              <img class="poster" src="${result.selected.posterUrl}" alt="poster">
            </c:if>
          </div>
          <div style="flex:1;">
            <div><b>${result.selected.title}</b></div>
            <div class="muted">TMDb ID: ${result.selected.tmdbId}</div>
            <div class="muted">Runtime Minutes: ${result.selected.runtimeMinutes} 分</div>
            <div>Vote(avg): ${result.selected.voteAverage}</div>
            <div style="max-width: 700px;">${result.selected.overview}</div>

            <!-- Register movie first -->
            <c:choose>
              <c:when test="${empty result.selectedMovieId}">
                <div style="margin-top:10px;" class="box">
                  <p class="muted">※ この映画はDB未登録です。先にDB登録してください。</p>

                  <form action="adminImportSchedule" method="get">
                    <input type="hidden" name="action" value="register">
                    <input type="hidden" name="tmdbId" value="${result.selected.tmdbId}">
                    <input type="hidden" name="query" value="${result.query}">
                    <button class="btn btn-primary" type="submit">この映画をDBに登録</button>
                  </form>
                </div>
              </c:when>

              <c:otherwise>
                <p class="ok" style="margin-top:10px;">
                  DB登録済み：movieId = ${result.selectedMovieId}
                </p>
                <div style="margin-top:8px;">
                  <a href="adminSchedules" class="btn">→ スケジュール管理へ</a>
                </div>

              </c:otherwise>
            </c:choose>

          </div>
        </div>
      </div>
    </div>
  </c:if>

  <!-- Search results -->
  <c:if test="${not empty result.data}">
    <hr>
    <h3>検索結果</h3>

    <c:forEach var="m" items="${result.data}">
      <div class="movie-card">
        <div>
          <c:if test="${not empty m.posterUrl}">
            <img class="poster" src="${m.posterUrl}" alt="poster">
          </c:if>
        </div>

        <div style="flex:1;">
          <div><b>${m.title}</b></div>
          <div>Release: ${m.releaseDate}</div>
          <div>Vote(avg): ${m.voteAverage}</div>
          <div class="muted">TMDb ID: ${m.tmdbId}</div>

          <!-- Select this movie -->
          <form action="adminImportSchedule" method="get" style="margin-top:8px;">
            <input type="hidden" name="action" value="select">
            <input type="hidden" name="tmdbId" value="${m.tmdbId}">
            <input type="hidden" name="query" value="${result.query}">       

            <button class="btn btn-ghost" type="submit">この映画を選択</button>
          </form>
        </div>
      </div>
    </c:forEach>
  </c:if>
  </div>

</body>
</html>