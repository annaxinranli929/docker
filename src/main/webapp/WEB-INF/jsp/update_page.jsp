<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>ユーザー情報変更</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="css/common.css">
    <link rel="stylesheet" href="css/update_page.css">
</head>
<body>

<jsp:include page="header.jsp" />

<main class="main-container">

<h2 class="page-title">ユーザー情報変更</h2>

<c:if test="${not empty sessionScope.message}">
    <div id="updateMessage" class="update-message">
        ${sessionScope.message}
    </div>
    <c:remove var="message" scope="session"/>
</c:if>

<form action="userUpdate" method="post">
    <div class="info-list">
        <table>
            <tr>
                <th>姓</th>
                <td><input type="text" name="firstName" value="${sessionScope.account.firstName}" required></td>
            </tr>
            <tr>
                <th>名</th>
                <td><input type="text" name="lastName" value="${sessionScope.account.lastName}" required></td>
            </tr>
            <tr>
                <th>メールアドレス</th>
                <td><input type="email" name="email" value="${sessionScope.account.email}" required></td>
            </tr>
            <tr>
                <th>電話番号</th>
                <td><input type="text" name="phone" value="${sessionScope.account.phone}"></td>
            </tr>
            <tr>
                <th>パスワード</th>
                <td class="password-cell">
                    <input type="password" id="password" name="password"
                           value="${sessionScope.account.password}">
                    <span class="toggle-btn" onclick="togglePassword()">👁</span>
                </td>
            </tr>
            <tr>
                <th>クレジットカード番号</th>
                <td>
                    <c:choose>
                        <c:when test="${not empty sessionScope.account.creditCardNumber}">
                            <input type="text" readonly
                                   value="**** **** **** ${fn:substring(sessionScope.account.creditCardNumber,
                                   fn:length(sessionScope.account.creditCardNumber)-4,
                                   fn:length(sessionScope.account.creditCardNumber))}">
                        </c:when>
                        <c:otherwise>
                            <input type="text" readonly value="未登録">
                        </c:otherwise>
                    </c:choose>
                    <small>※ 下4桁のみ表示</small>
                </td>
            </tr>
        </table>

           <!--仮表示-->
        <!-- 保存したカード -->
        <div class="section">
          <h2>保存したカード</h2>

          <c:choose>
            <c:when test="${empty cards}">
              <div class="empty-state">
                <p>保存されたカードはありません。</p>
              </div>
            </c:when>

            <c:otherwise>
              <div class="card-list">
                <c:forEach var="c" items="${cards}">
                  <div class="saved-card">
                    <div class="card-line">
                      <strong><c:out value="${c.brand}" /></strong>
                      <span>**** **** **** <c:out value="${c.last4}" /></span>

                      <c:if test="${c.defaultFlag}">
                        <span class="badge-default">DEFAULT</span>
                      </c:if>
                    </div>

                    <div class="card-sub">
                      有効期限:
                      <c:out value="${c.expMonth}" /> /
                      <c:out value="${c.expYear}" />
                    </div>
                  </div>
                </c:forEach>
              </div>
            </c:otherwise>
          </c:choose>
        </div>       
    </div>

    <div class="button-area">
        <button class="btn" type="submit">更新</button>
        <button type="button" class="btn" onclick="openModal()">クレジットカード登録・変更</button>
        <input type="hidden" name="userId" value="${sessionScope.account.userId}">
    </div>
</form>

</main>

<div id="modalOverlay" class="modal-overlay" onclick="closeModal()"></div>

<div id="creditModal" class="modal-box">
    <div class="modal-header">
        <h3>クレジットカード登録</h3>
        <span class="close-btn" onclick="closeModal()">×</span>
    </div>

    <form action="creditCardRegister" method="post">
        <table class="modal-table">
            <tr>
                <th>カード番号</th>
                <td><input type="text" name="creditCardNumber" required></td>
            </tr>
            <tr>
                <th>有効期限 / セキュリティコード</th>
                <td>
                    <div class="expire-cvv">
                        <input type="text" name="expireMonth" placeholder="MM" maxlength="2" required>
                        <span class="slash">/</span>
                        <input type="text" name="expireYear" placeholder="YY" maxlength="2" required>
                        <input type="password" name="securityCode" placeholder="CVV" maxlength="4" required>
                    </div>
                </td>
            </tr>
            <tr>
                <th>カード名義</th>
                <td><input type="text" name="cardName" required></td>
            </tr>
        </table>

        <div class="modal-btn-area">
            <button class="btn" type="submit">登録</button>
        </div>

        <input type="hidden" name="userId" value="${sessionScope.account.userId}">
    </form>
</div>

<script>
function togglePassword() {
    const input = document.getElementById("password");
    const btn   = document.querySelector(".toggle-btn");
    input.type = input.type === "password" ? "text" : "password";
    btn.textContent = input.type === "password" ? "👁" : "🙈";
}

function openModal() {
    document.getElementById("modalOverlay").style.display = "block";
    document.getElementById("creditModal").style.display = "block";
}

function closeModal() {
    document.getElementById("modalOverlay").style.display = "none";
    document.getElementById("creditModal").style.display = "none";
}

setTimeout(() => {
    const msg = document.getElementById('updateMessage');
    if (msg) {
        msg.classList.add('fade-out');
        setTimeout(() => msg.remove(), 500);
    }
}, 1500);
</script>

</body>
</html>
