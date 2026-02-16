<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="ja">
<head>
    <title>NEOcinema-top</title>
    <meta charset="utf-8">
    <link rel="stylesheet" href="css/common.css">
    <link rel="stylesheet" href="css/schedule.css">
</head>
<body>
    <jsp:include page="header.jsp" />

    <%-- ログインメッセージ --%>
    <c:if test="${not empty sessionScope.message}">
        <div id="loginLogoutMessage" class="update-message">
            ${sessionScope.message}
        </div>
        <c:remove var="message" scope="session"/>
    </c:if>

    <%-- ログアウトメッセージ --%>
    <c:if test="${not empty sessionScope.logoutMessage}">
        <div id="loginLogoutMessage" class="update-message update-message-danger">
            ${sessionScope.logoutMessage}
        </div>
        <c:remove var="logoutMessage" scope="session"/>
    </c:if>


    <main class="main-container">
        <h2 class="page-title">上映スケジュール</h2>

        <%-- 劇場選択タブ --%>
        <div class="cinema-selection" id="cinema-list">
            <c:forEach var="cinema" items="${result}" varStatus="status">
                <button id="cinema-${cinema.cinemaId}-button" 
                        class="cinema-tab ${status.first ? 'active' : 'non-active'}" type="button">
                    ${cinema.cinemaName}
                </button>
            </c:forEach>
        </div>

        <div id="schedules">
            <c:forEach var="cinema" items="${result}" varStatus="cStatus">
                <div id="cinema-${cinema.cinemaId}" class="cinema-panel" 
                     style="${cStatus.first ? 'display: block;' : 'display: none;'}">

                    <%-- 日付選択 --%>
                    <div class="date-selection-container">
                        <div class="date-selection">
                            <c:forEach var="date" items="${cinema.dates}" varStatus="dStatus">
                                <fmt:formatDate value="${date.date}" pattern="yyyy-MM-dd" var="idDate" />
                                <button id="dateBtn-${cinema.cinemaId}-${idDate}" 
                                        class="date-button ${dStatus.first ? 'active' : ''}"
                                        data-target="dateList-${cinema.cinemaId}-${idDate}">
                                    <fmt:formatDate value="${date.date}" pattern="M/d(E)" />
                                </button>
                            </c:forEach>
                        </div>
                    </div>

                    <%-- 上映スケジュール --%>
                    <c:forEach var="date" items="${cinema.dates}" varStatus="dStatus">
                        <fmt:formatDate value="${date.date}" pattern="yyyy-MM-dd" var="sectionDate" />
                        <div id="dateList-${cinema.cinemaId}-${sectionDate}" class="date-content" 
                             style="${dStatus.first ? 'display: block;' : 'display: none;'}">

                            <c:set var="prevMovieId" value="" />
                            <c:forEach var="schedule" items="${date.schedules}" varStatus="loopStatus">
                                <c:if test="${schedule.movie.movieId != prevMovieId}">
                                    <c:if test="${not empty prevMovieId}">
                                        </div></div></div>
                                    </c:if>

                                    <div class="movie-schedule-wrapper">
                                        <div class="movie-poster">
                                            <a href="movieDetail?id=${schedule.movie.movieId}">
                                                <img src="${schedule.movie.posterUrl}" alt="Poster" onerror="this.src='img/noimage.svg'">
                                            </a>
                                        </div>
                                        <div class="movie-details">
                                            <a href="movieDetail?id=${schedule.movie.movieId}" class="movie-detail-link">
                                                <div class="movie-title">${schedule.movie.movieName}</div>
                                                <div class="phone navigator">詳細を見る <span>&rsaquo;</span></div>
                                            </a>
                                            <div class="time-slots">
                                    <c:set var="prevMovieId" value="${schedule.movie.movieId}" />
                                </c:if>

                                <c:url var="seatUrl" value="seat">
                                    <c:param name="scheduleId" value="${schedule.scheduleId}" />
                                    <c:param name="posterUrl" value="${schedule.movie.posterUrl}" />
                                </c:url>

                                <a href="${schedule.reservable ? seatUrl : '#'}" 
                                   class="time-slot ${schedule.reservable ? '' : 'disabled'}">
                                    <span class="start-time">
                                        <fmt:formatDate value="${schedule.startTime}" pattern="HH:mm" />
                                    </span>
                                    <span class="end-time">
                                        〜<fmt:formatDate value="${schedule.endTime}" pattern="HH:mm" /> 終
                                    </span>
                                    <span class="screen-name">${schedule.screenName}</span>
                                </a>

                                <c:if test="${loopStatus.last}">
                                        </div></div></div>
                                </c:if>
                            </c:forEach>
                        </div>
                    </c:forEach>
                </div>
            </c:forEach>
        </div>
    </main>

    <jsp:include page="footer.jsp" />

    <script src="js/schedule.js"></script>
</body>
</html>
