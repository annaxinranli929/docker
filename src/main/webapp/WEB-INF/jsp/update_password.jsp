<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>パスワード再設定 - CinemaSystem</title>
    <link rel="stylesheet" href="css/users.css">
    <style>
        .reset-container {
            max-width: 400px;
            margin: 100px auto;
            padding: 30px;
            background: #fff;
            border-radius: 8px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }
        .form-group { margin-bottom: 20px; }
        .form-group label { display: block; margin-bottom: 8px; font-weight: bold; }
        .form-group input { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }
        .error-msg { color: #e74c3c; font-size: 0.9rem; margin-top: 5px; display: none; }
        .btn-submit { width: 100%; padding: 12px; background: #3498db; color: #fff; border: none; border-radius: 4px; cursor: pointer; font-size: 1rem; transition: background 0.3s; }
        .btn-submit:hover { background: #2980b9; }
    </style>
</head>
<body>

    <div class="reset-container">
        <h2>パスワードの再設定</h2>
        <p>新しいパスワードを入力してください。</p>
        
        <form action="updatePasswordExecute" method="post" id="resetForm">
            <%-- URLから受け取ったトークンを隠しパラメータで保持 --%>
            <input type="hidden" name="token" value="${param.token}">

            <div class="form-group">
                <label for="newPassword">新しいパスワード</label>
                <input type="password" id="newPassword" name="newPassword" required 
                       placeholder="8文字以上の英数字" minlength="8">
            </div>

            <div class="form-group">
                <label for="confirmPassword">新しいパスワード（確認）</label>
                <input type="password" id="confirmPassword" name="confirmPassword" required
                       placeholder="もう一度入力してください">
                <p id="errorMatch" class="error-msg">パスワードが一致しません。</p>
            </div>

            <button type="submit" class="btn-submit">パスワードを更新する</button>
        </form>
    </div>

    <script>
        document.getElementById('resetForm').addEventListener('submit', function(e) {
            const pass = document.getElementById('newPassword').value;
            const confirm = document.getElementById('confirmPassword').value;
            const errorMsg = document.getElementById('errorMatch');

            if (pass !== confirm) {
                e.preventDefault(); // 送信を止める
                errorMsg.style.display = 'block';
            } else {
                errorMsg.style.display = 'none';
                this.querySelector('.btn-submit').innerText = '更新中...';
            }
        });
    </script>
</body>
</html>