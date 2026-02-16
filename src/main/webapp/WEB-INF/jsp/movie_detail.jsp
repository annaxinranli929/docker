<%@ page pageEncoding="utf-8" contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="ja">
<head>
    <title>${result.movieDetail.movieName} - NEOcinema</title>
    <meta charset="utf-8">
    <link rel="stylesheet" href="css/common.css">
    <link rel="stylesheet" href="css/movie_detail.css">
    <script src="js/movie_detail.js" defer></script>
</head>
<body>
    <jsp:include page="header.jsp" />

    <div class="movie-detail-container">
        <div class="movie-hero">
            <div class="detail-poster">
                <img src="${result.movieDetail.posterUrl}" alt="${result.movieDetail.movieName}" onerror="this.src='img/noimage.svg'">
            </div>
            <div class="detail-info">
                <!-- <div class="detail-meta">MOVIE ID: ${result.movieDetail.movieId}</div> -->
                <h1 class="detail-title">${result.movieDetail.movieName}</h1>
                <div class="status-badge-container">
                    <c:choose>
                        <c:when test="${result.movieDetail.active}"><span class="status-badge active">上映中</span></c:when>
                        <c:otherwise><span class="status-badge inactive">上映終了 / 近日公開</span></c:otherwise>
                    </c:choose>
                </div>
                <div class="detail-evaluation"><span class="star">★</span> ${result.movieDetail.evaluation != 0 ? result.movieDetail.evaluation : '集計中'}</div>
                <div class="detail-genres">
                    <c:forEach var="genre" items="${result.movieDetail.genres}"><span class="genre-tag">${genre}</span></c:forEach>
                </div>
            </div>
        </div>

        <div class="summary-section">
            <h3 class="section-title">あらすじ</h3>
            <div class="movie-summary"><p id="movie-summary-text">${result.movieDetail.summary}</p></div>
        </div>

        <div class="schedule-section">
            <h3 class="section-title">上映スケジュール</h3>
            
            <c:choose>
                <%-- スケジュールが存在する場合 --%>
                <c:when test="${not empty result.schedules}">
                    <c:forEach var="cinema" items="${result.schedules}">
                        <div class="cinema-block">
                            <h4 class="cinema-name-header">${cinema.cinemaName}</h4>
                            
                            <c:forEach var="date" items="${cinema.dates}">
                                <div class="date-schedule-row">
                                    <div class="date-side-label">
                                        <fmt:formatDate value="${date.date}" pattern="M/d" />
                                        <span>(<fmt:formatDate value="${date.date}" pattern="E" />)</span>
                                    </div>
                                    
                                    <div class="table-wrapper">
                                        <table class="flat-schedule-table">
                                            <tbody>
                                                <c:forEach var="sch" items="${date.schedules}">
                                                    <tr class="${sch.reservable ? '' : 'row-disabled'}">
                                                        <td class="col-time">
                                                            <span class="start-time"><fmt:formatDate value="${sch.startTime}" pattern="HH:mm" /></span>
                                                            <span class="end-time">〜<fmt:formatDate value="${sch.endTime}" pattern="HH:mm" /></span>
                                                        </td>
                                                        <td class="col-screen">${sch.screenName}</td>
                                                        <td class="col-status">
                                                            <c:choose>
                                                                <c:when test="${sch.reservable}">
                                                                    <a href="seat?scheduleId=${sch.scheduleId}" class="mini-reserve-btn">予約</a>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="status-text-sold">終了</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:forEach>
                </c:when>

                <%-- スケジュールが空の場合 --%>
                <c:otherwise>
                    <div class="no-schedule-message">
                        <p>現在、この作品の上映スケジュールはございません。</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    <jsp:include page="footer.jsp" />
</body>
</html>