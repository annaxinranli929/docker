<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:useBean id="now" class="java.util.Date" />

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>マイページ | NEOcinema</title>
    <link rel="stylesheet" href="css/common.css">
    <link rel="stylesheet" href="css/my_page.css">
</head>
<body>

<jsp:include page="header.jsp" />

<main class="main-container">

    <c:if test="${not empty sessionScope.message}">
    <div id="updateMessage" class="update-message">
        ${sessionScope.message}
    </div>
    <c:remove var="message" scope="session"/>
    </c:if>


    <div class="logout-flex">
        <h1 class="page-title">マイページ</h1>
        <a href="logout" class="btn btn-logout">↪ ログアウト</a>
    </div>
    <!-- 登録情報 -->
    <div class="section user-info">
        <h2>登録情報</h2>
        <div class="info-list">
            <p><strong>ユーザー名</strong>
                <span><c:out value="${sessionScope.account.firstName} ${sessionScope.account.lastName}" /> 様</span>
            </p>
            <p><strong>メールアドレス</strong>
                <span><c:out value="${sessionScope.account.email}" /></span>
            </p>
            <p><strong>電話番号</strong>
                <span><c:out value="${sessionScope.account.phone}" /></span>
            </p>
            <p><strong>クレジットカード</strong>
                <span>
                    <c:choose>
                        <c:when test="${not empty sessionScope.account.creditCardNumber}">
                            ****-****-****-${fn:substring(
                                sessionScope.account.creditCardNumber,
                                fn:length(sessionScope.account.creditCardNumber) - 4,
                                fn:length(sessionScope.account.creditCardNumber)
                            )}
                        </c:when>
                        <c:otherwise>未登録</c:otherwise>
                    </c:choose>
                </span>
            </p>
        </div>
        <div class="action-area">
            <a href="updateAccount" class="btn btn-edit">登録情報を変更する</a>
        </div>
    </div> 
     </div>

    <!-- 予約中（未来） -->
    <div class="section">
        <h2>予約中のチケット</h2>

        <c:set var="hasFuture" value="false" />
        <c:forEach var="res" items="${result}">
            <c:if test="${res.endTime.time > now.time}">
                <c:set var="hasFuture" value="true" />
            </c:if>
        </c:forEach>

        <c:choose>
            <c:when test="${not hasFuture}">
                <div class="empty-state">
                    <p>現在、予約中のチケットはありません。</p>
                </div>
            </c:when>
            <c:otherwise>

                <div class="table-wrapper">
                <table class="reservation-table">
                    <thead>
                        <tr>
                            <th>予約内容</th>
                            <th>座席</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="res" items="${result}">
                            <c:if test="${res.endTime.time > now.time}">
                                <c:forEach var="seat" items="${res.seatsName}" varStatus="st">
                                    <tr>
                                        <c:if test="${st.first}">
                                            <td rowspan="${fn:length(res.seatsName)}" class="movie-td">
                                                <div class="movie-info-cell">
                                                    <div class="booking-date">
                                                        予約日時:
                                                        <fmt:formatDate value="${res.bookingAt}" pattern="yyyy/MM/dd HH:mm" />
                                                    </div>
                                                    <strong><c:out value="${res.movieName}" /></strong>
                                                    <div class="location-text">
                                                        <c:out value="${res.cinemaName}" /> /
                                                        <c:out value="${res.screenName}" />
                                                    </div>
                                                    <span class="start-time-text">
                                                        上映:
                                                        <fmt:formatDate value="${res.startTime}" pattern="MM/dd HH:mm" />
                                                        ～ <fmt:formatDate value="${res.endTime}" pattern="HH:mm" />
                                                    </span>
                                                </div>
                                            </td>
                                        </c:if>

                                        <td class="seat-name">
                                            <span class="seat-badge"><c:out value="${seat}" /></span>
                                        </td>

                                        <td>
                                            <form action="cancelBooking" method="post">
                                                <input type="hidden" name="seatId" value="${res.seatsId[st.index]}">
                                                <input type="hidden" name="bookingId" value="${res.bookingId}">
                                                <button type="button"
                                                        class="btn btn-cancel confirm-trigger"
                                                        data-message="この座席 [${seat}] の予約を取り消しますか？"
                                                        data-target="${res.movieName}">
                                                    予約取消
                                                </button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:if>
                        </c:forEach>
                    </tbody>
                </table>
                </div>

            </c:otherwise>
        </c:choose>
    </div>

    <!-- 過去の予約 -->
    <div class="section">
        <h2>過去の予約</h2>

        <c:set var="hasPast" value="false" />
        <c:forEach var="res" items="${result}">
            <c:if test="${res.endTime.time <= now.time}">
                <c:set var="hasPast" value="true" />
            </c:if>
        </c:forEach>

        <c:choose>
            <c:when test="${not hasPast}">
                <div class="empty-state">
                    <p>過去の予約はありません。</p>
                </div>
            </c:when>
            <c:otherwise>
                <table class="reservation-table responsive">
                    <tbody>
                    <c:forEach var="res" items="${result}">
                        <c:if test="${res.endTime.time <= now.time}">
                            <c:forEach var="seat" items="${res.seatsName}">
                                <tr class="ticket-card past">
                                    <td class="movie-block">
                                        <div class="movie-info-cell">
                                            <div class="booking-date">
                                                予約日時:
                                                <fmt:formatDate value="${res.bookingAt}" pattern="yyyy/MM/dd HH:mm" />
                                            </div>
                                            <strong><c:out value="${res.movieName}" /></strong>
                                            <div class="location-text">
                                                <c:out value="${res.cinemaName}" /> /
                                                <c:out value="${res.screenName}" />
                                            </div>
                                            <span class="start-time-text past">
                                                上映終了:
                                                <fmt:formatDate value="${res.endTime}" pattern="MM/dd HH:mm" />
                                            </span>
                                        </div>
                                    </td>

                                    <td class="seat-block">
                                        <span class="seat-badge past">
                                            <c:out value="${seat}" />
                                        </span>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:if>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- アカウント削除 -->
    <div class="section danger-zone">
        <h3 class="danger-title">⚠️ アカウントの完全削除</h3>
        <p class="danger-note">
            一度退会すると、これまでの予約履歴や会員データはすべて抹消され、復元することはできません。
        </p>
        <form id="removalForm" action="userRemoval" method="post">
            <input type="hidden" name="userId" value="${sessionScope.account.userId}">
            <button type="button" class="btn-danger-block confirm-trigger"
                    data-message="本当にアカウントを削除しますか？<br><br>
                                  <span class='warning-text'>※この操作により全てのデータが消失します。</span>"
                    data-target="あなたの会員アカウント情報すべて">
                アカウントを削除する
            </button>
        </form>
    </div>

    <!-- 確認モーダル -->
    <div id="confirmModal" class="modal">
        <div class="modal-content">
            <h3 id="modalTitle">内容の確認</h3>
            <div class="modal-info-box">
                <span id="modalTargetLabel">対象</span>
                <div id="modalTargetValue"></div>
            </div>
            <p id="modalMessage"></p>
            <div class="modal-actions">
                <button type="button" id="modalCancelBtn" class="btn btn-secondary">キャンセル</button>
                <button type="button" id="modalConfirmBtn" class="btn btn-danger">確定する</button>
            </div>
        </div>
    </div>
</div>

<script src="js/my_page.js"></script>
</body>
<script>
  setTimeout(() => {
    const msg = document.getElementById('updateMessage');
    if (msg) {
      msg.classList.add('fade-out');
      setTimeout(() => msg.remove(), 500);
    }
  }, 1500);
</script>

</html>
