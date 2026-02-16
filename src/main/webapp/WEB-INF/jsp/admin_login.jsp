<%@ page pageEncoding="utf-8" contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>管理人ログイン</title>
  <style>
    body{
      font-family: sans-serif;
      margin: 0;
      background: #fafafa;
      min-height: 100vh;
      display:flex;
      align-items:center;
      justify-content:center;
      padding: 24px;
    }
    .card{
      width: 100%;
      max-width: 420px;
      background:#fff;
      border:1px solid #e8e8e8;
      border-radius: 14px;
      padding: 18px 18px 16px;
      box-shadow: 0 6px 20px rgba(0,0,0,0.06);
    }
    h2{ margin: 0 0 10px; font-size: 20px; }
    .muted{ color:#666; font-size: 12px; margin: 0 0 14px; }

    .notice{
      border: 1px solid #f1d7d7;
      background: #fff5f5;
      color: #8a1f1f;
      border-radius: 10px;
      padding: 10px 12px;
      font-size: 13px;
      margin: 0 0 12px;
      line-height: 1.4;
    }

    .row{ margin: 10px 0; }
    label{
      display:block;
      font-size: 12px;
      color:#444;
      margin-bottom: 6px;
    }
    input[type="text"], input[type="password"]{
      width: 100%;
      padding: 10px 12px;
      border: 1px solid #ccc;
      border-radius: 10px;
      font-size: 14px;
      box-sizing: border-box;
      background:#fff;
    }
    input:focus{
      outline:none;
      border-color:#333;
    }

    .actions{
      margin-top: 14px;
      display:flex;
      gap: 10px;
      align-items:center;
      justify-content:space-between;
    }
    .btn{
      padding: 9px 12px;
      border: 1px solid #333;
      background:#333;
      color:#fff;
      border-radius: 10px;
      cursor:pointer;
      font-size: 13px;
      width: 100%;
    }
    .btn:hover{ opacity: .92; }

    .link{
      display:inline-block;
      font-size: 12px;
      color:#333;
      text-decoration:none;
      opacity: .75;
      white-space: nowrap;
      margin-top: 10px;
    }
    .link:hover{ opacity: 1; text-decoration: underline; }

    .footer{
      margin-top: 10px;
      text-align: center;
    }
  </style>
</head>
<body>

  <div class="card">
    <h2>管理人ログイン</h2>
    <p class="muted">管理画面を利用するにはログインが必要です。</p>

    <c:if test="${not empty result.message}">
      <div class="notice">${result.message}</div>
    </c:if>

    <form action="adminLogin" method="post">
      <div class="row">
        <label>管理ID</label>
        <input type="text" name="adminId" autocomplete="username" required>
      </div>

      <div class="row">
        <label>パスワード</label>
        <input type="password" name="password" autocomplete="current-password" required>
      </div>

      <div class="actions">
        <button class="btn" type="submit">ログイン</button>
      </div>
    </form>

    <div class="footer">
      <a class="link" href=".">← トップへ戻る</a>
    </div>
  </div>

</body>
</html>