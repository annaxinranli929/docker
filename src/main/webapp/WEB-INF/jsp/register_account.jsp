<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>新規アカウント登録</title>
    <link rel="stylesheet" href="css/common.css">
    <link rel="stylesheet" href="css/register.css">
</head>
<body>

<jsp:include page="header.jsp" />

<main class="main-container">

<h2 class="page-title">新規アカウント登録</h2>

<!-- 登録結果メッセージ -->
<c:if test="${not empty result.message}">
    <div id="updateMessage" class="update-message ${result.status}">
        ${result.message}
    </div>
</c:if>

<form action="registerAccount" method="post">

    <div class="info-list">
        <table>
            <tr>
                <th>姓</th>
                <td><input type="text" name="firstName" value="${param.firstName}" required></td>
            </tr>
            <tr>
                <th>名</th>
                <td><input type="text" name="lastName" value="${param.lastName}" required></td>
            </tr>
            <tr>
                <th>メールアドレス</th>
                <td><input type="email" name="email" value="${param.email}" required></td>
            </tr>
            <tr>
                <th>電話番号</th>
                <td><input type="text" name="phone" value="${param.phone}"></td>
            </tr>
            <tr>
                <th>パスワード</th>
                <td class="password-cell">
                    <input type="password" id="password" name="password" required>
                    <span class="toggle-btn" onclick="togglePassword()">👁</span>
                </td>
            </tr>
            <tr>
                <th>クレジットカード番号</th>
                <td>
                    <input type="text" name="creditCardNumber" placeholder="任意">
                    <small>※ 登録後でも変更可能</small>
                </td>
            </tr>
        </table>
    </div>

    <div class="button-area">
        <button class="btn" type="submit">登録</button>
    </div>

</form>

</main>

<script>
function togglePassword() {
    const input = document.getElementById("password");
    const btn   = document.querySelector(".toggle-btn");

    if (input.type === "password") {
        input.type = "text";
        btn.textContent = "🙈";
    } else {
        input.type = "password";
        btn.textContent = "👁";
    }
}


</script>

</body>
</html>
