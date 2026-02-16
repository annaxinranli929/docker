<%@ page pageEncoding="utf-8" contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="utf-8">
    <title>ログイン - NEOcinema</title>
    <link rel="stylesheet" href="css/common.css">
    <link rel="stylesheet" href="css/login.css">
</head>
<body>

<jsp:include page="header.jsp" />

<main class="main-container">

    <h2 class="page-title">ログイン</h2>

    <!-- ===== 中央ポップアップ通知 ===== -->
    <c:if test="${not empty result.success}">
        <div id="updateMessage" class="update-message success">
            ${result.success}
        </div>
    </c:if>

    <c:if test="${not empty result.message}">
        <div id="updateMessage" class="update-message error">
            ${result.message}
        </div>
    </c:if>

    <form action="login" method="post" class="login-form">

        <div class="form-group">
            <label for="email">メールアドレス</label>
            <input type="email" id="email" name="email"
                   value="${param.email}" required>
        </div>

        <div class="form-group">
            <label for="password">パスワード</label>
            <div class="password-wrap">
                <input type="password" id="password" name="password" required>
                <span class="toggle-btn" onclick="togglePassword()">👁</span>
            </div>
        </div>

        <button type="submit" class="login-btn">ログイン</button>

    </form>

    <div class="form-footer">
        <p>アカウントをお持ちでない方は <a href="account">新規登録</a></p>
    </div>

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

// ポップアップ自動消去
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
