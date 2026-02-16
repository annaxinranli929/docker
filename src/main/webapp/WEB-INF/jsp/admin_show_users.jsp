<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>ユーザー管理 - CinemaSystem</title>
    <link rel="stylesheet" href="css/users.css">
    <script src="js/users.js" defer></script>
</head>
<body>
    <header>
        <div class="header-left">
            <h1>ユーザー管理</h1>
        </div>
        <div class="header-right">
            <button type="button" class="btn-secondary" id="backToTop">トップに戻る</button>
        </div>
    </header>

    <main class="user-container">
        <div class="card">
            <div class="card-header">
                <h2>登録利用者リスト</h2>
            </div>

            <div class="search-container">
                <input type="text" id="userSearch" placeholder="名前、メール、電話番号で検索...">
                <span class="search-count">
                    表示中: <span id="visibleCount">${result.userList.size()}</span> / 全 ${result.userList.size()} 件
                </span>
            </div>

            <div class="card-body">
                <table class="user-table">
                    <thead>
                        <tr>
                            <th class="sort-header" data-column="0">ID <span class="sort-icon"></span></th>
                            <th class="sort-header" data-column="1">氏名 <span class="sort-icon"></span></th>
                            <th class="sort-header" data-column="2">メールアドレス <span class="sort-icon"></span></th>
                            <th class="sort-header" data-column="3">電話番号 <span class="sort-icon"></span></th>
                            <th class="sort-header" data-column="4">最終ログイン <span class="sort-icon"></span></th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="user" items="${result.userList}">
                            <tr class="${user.admin ? 'is-admin-row' : ''} ${user.deletedAt != null ? 'is-deleted-row' : ''}">
                                <td class="id-cell">${user.userId}</td>
                                <td><span class="fullname">${user.lastName} ${user.firstName}</span></td>
                                <td class="email-cell">${user.email}</td>
                                <td>${user.phone}</td>
                                <td class="date-cell" data-time="${user.lastLoginAt != null ? user.lastLoginAt.time : 0}">
                                    <c:choose>
                                        <c:when test="${user.lastLoginAt != null}">
                                            <fmt:formatDate value="${user.lastLoginAt}" pattern="yyyy/MM/dd HH:mm" />
                                        </c:when>
                                        <c:otherwise>---</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div class="action-group">
                                        <c:choose>
                                            <c:when test="${user.deletedAt != null}">
                                                <button type="button" class="action-btn restore btn-action" 
                                                        data-id="${user.userId}" data-type="restore" title="復活">
                                                    <span class="icon">↺</span> 復活
                                                </button>
                                            </c:when>
                                            <c:otherwise>
                                                <button type="button" class="action-btn role ${user.admin ? 'is-admin' : ''} btn-action" 
                                                        data-id="${user.userId}" data-type="toggleAdmin" 
                                                        title="${user.admin ? '一般へ降格' : '管理者へ昇格'}">
                                                    ${user.admin ? '★' : '☆'} 権限
                                                </button>

                                                <button type="button" class="action-btn reset btn-action" 
                                                        data-id="${user.userId}" data-type="resetPass" title="パスワードリセット">
                                                    🔑 リセット
                                                </button>

                                                <button type="button" class="action-btn delete btn-action" 
                                                        data-id="${user.userId}" data-type="delete" title="削除">
                                                    🗑 削除
                                                </button>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
    <%-- モーダル部分はこれ1つで全操作をまかないます --%>
    <div id="actionModal" class="modal-overlay">
        <form id="actionForm" action="sendMail" method="post" class="modal-content">
            <h3 id="modalTitle">操作の確認</h3>
            <p id="modalMessage">この操作を実行しますか？</p>
            
            <input type="hidden" name="id" id="formUserId">
            <input type="hidden" name="action" id="formAction">
            <input type="hidden" name="mailAddress" id="formMailAddress">

            <div class="user-info-box">
                <p><strong>対象ユーザー:</strong> <span id="modalTargetName"></span></p>
                <p id="modalDetailLine" style="display:none;">
                    <strong>メール:</strong> <span id="modalTargetDetail"></span>
                </p>
            </div>

            <p id="modalWarning" class="warning-text"></p>

            <div class="modal-actions">
                <button type="button" class="btn-cancel">キャンセル</button>
                <button type="submit" class="btn-confirm" id="modalConfirmBtn">実行する</button>
            </div>
        </form>
    </div>
</body>
</html>