<%@ page pageEncoding="utf-8" contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>NEOcinema - 予約結果</title>
  <link rel="stylesheet" href="css/common.css">
  <link rel="stylesheet" href="css/booking.css">
</head>
<body>
  <jsp:include page="header.jsp" />

  <div class="container page">
    <div class="page-head">
      <h2 class="page-title">予約結果</h2>
      <p class="page-sub">ご利用ありがとうございます。</p>
    </div>

    <c:choose>

    
      <c:when test="${result.bookingResult}">
        <section class="card">
          <div class="card-head">
            <h3 class="card-title">予約が完了しました</h3>
            <p class="card-sub">内容をご確認ください。</p>
          </div>

          <div class="card-body">
            <div class="kv">
              <div class="kv-row">
                <div class="kv-key">座席</div>
                <c:forEach var ="seat" items="${result.bookingData.seatNames}">
                  <div class="kv-val seat-chip">${seat}</div>
                </c:forEach>
              </div>
              <div class="kv-row">
                <div class="kv-key">合計金額</div>
                <div class="kv-val price">￥${result.bookingData.totalPrice}</div>
              </div>
            </div>

            <div class="actions" style="margin-top:18px;">
              <a class="btn btn-ghost" href="${pageContext.request.contextPath}/">トップへ戻る</a>
            </div>
          </div>
        </section>
      </c:when>

      <c:otherwise>
        <section class="card">
          <div class="card-head">
            <h3 class="card-title">予約に失敗しました</h3>
            <p class="card-sub">お手数ですが、もう一度お試しください。</p>
          </div>

          <div class="card-body">
            <div class="result-badge ng">❌ FAILED</div>

            <c:if test="${not empty result.error}">
              <div class="error-box">
                ${result.error}
              </div>
            </c:if>

            <div class="actions" style="margin-top:18px;">
              <a class="btn btn-ghost" href="javascript:history.back()">戻る</a>
              <a class="btn btn-primary" href="${pageContext.request.contextPath}/">トップへ戻る</a>
            </div>
          </div>
        </section>
      </c:otherwise>

    </c:choose>
  </div>
</body>
</html>
