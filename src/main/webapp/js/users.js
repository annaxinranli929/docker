/**
 * ユーザー管理画面用スクリプト (POST送信・モーダル統一版)
 */
document.addEventListener('DOMContentLoaded', () => {
    // --- 1. イベントリスナーの登録 ---

    // 検索入力のリアルタイムフィルタリング
    const userSearch = document.getElementById('userSearch');
    if (userSearch) {
        userSearch.addEventListener('keyup', filterUsers);
    }

    // トップに戻るボタン
    const backBtn = document.getElementById('backToTop');
    if (backBtn) {
        backBtn.addEventListener('click', () => {
            location.href = 'index.html';
        });
    }

    // テーブル内の操作ボタン (Event Delegation)
    const tableBody = document.querySelector('.user-table tbody');
    if (tableBody) {
        tableBody.addEventListener('click', (e) => {
            // クリックされた要素の親を辿って .btn-action を探す
            const btn = e.target.closest('.btn-action');
            if (btn) {
                const userId = btn.dataset.id;
                const type = btn.dataset.type;
                openActionModal(userId, type);
            }
        });
    }

    // モーダル：フォーム送信時の処理
    const actionForm = document.getElementById('actionForm');
    if (actionForm) {
        actionForm.addEventListener('submit', () => {
            const confirmBtn = document.getElementById('modalConfirmBtn');
            confirmBtn.disabled = true;
            confirmBtn.innerText = "処理中...";
            // 以降、ブラウザの標準機能でPOST送信される
        });
    }

    // モーダル：キャンセルボタン
    document.querySelectorAll('.btn-cancel').forEach(btn => {
        btn.addEventListener('click', closeModal);
    });

    // モーダル：背景クリックで閉じる
    const modalOverlay = document.getElementById('actionModal');
    if (modalOverlay) {
        modalOverlay.addEventListener('click', (e) => {
            if (e.target === modalOverlay) closeModal();
        });
    }

    // --- 2. 初期化処理 ---
    initTableSort();    // ソート機能の有効化
    cleanQueryString(); // 処理完了後のURLクリーンアップ
});

/**
 * 汎用アクションモーダルを開き、フォームに値をセットする
 */
function openActionModal(userId, type) {
    const rows = Array.from(document.querySelectorAll('.user-table tbody tr'));
    const targetRow = rows.find(row => {
        const btn = row.querySelector('.btn-action');
        return btn && btn.dataset.id === userId;
    });

    if (!targetRow) return;

    // 行から情報を取得
    const name = targetRow.querySelector('.fullname').innerText;
    const email = targetRow.querySelector('.email-cell').innerText;

    // モーダル内要素の取得
    const modal = document.getElementById('actionModal');
    const title = document.getElementById('modalTitle');
    const msg = document.getElementById('modalMessage');
    const warn = document.getElementById('modalWarning');
    const detailLine = document.getElementById('modalDetailLine');
    const confirmBtn = document.getElementById('modalConfirmBtn');
    const actionForm = document.getElementById('actionForm');

    // 隠しフィールド（Form送信用のデータ）
    const formUserId = document.getElementById('formUserId');
    const formAction = document.getElementById('formAction');
    const formMail = document.getElementById('formMailAddress');

    // 表示と値のリセット
    warn.innerText = "";
    detailLine.style.display = "none";
    confirmBtn.className = "btn-confirm";
    formMail.value = ""; 

    // 操作タイプごとに文言、ボタン色、送信アクションを切り替え
    switch(type) {
        case 'toggleAdmin':
            const isAdmin = targetRow.classList.contains('is-admin-row');
            title.innerText = "権限変更の確認";
            msg.innerText = isAdmin ? 
                "管理権限を取り消し、一般ユーザーに降格させますか？" : 
                "このユーザーを管理者に昇格させますか？";
            confirmBtn.classList.add('role');
            formAction.value = "toggleAdmin";
            actionForm.action = "adminUserUpdate";
            break;

        case 'resetPass':
            title.innerText = "パスワードリセット";
            msg.innerText = "再設定用リンクをメールで送信します。";
            warn.innerText = "※安全のため、再設定が完了するまでアカウントは一時ロックされます。";
            document.getElementById('modalTargetDetail').innerText = email;
            detailLine.style.display = "block";
            confirmBtn.classList.add('reset');
            actionForm.action = "sendMail";
            
            formAction.value = "sendResetMail"; 
            formMail.value = email; // サーブレット側で request.getParameter("mailAddress") で取得可能
            break;

        case 'delete':
            title.innerText = "ユーザー削除の確認";
            msg.innerText = "このユーザーを論理削除しますか？";
            warn.innerText = "※削除後はリストの下方に移動し、ログイン不可となります。";
            confirmBtn.classList.add('delete');
            formAction.value = "delete";
            actionForm.action = "adminUserUpdate";
            break;

        case 'restore':
            title.innerText = "ユーザー復活の確認";
            msg.innerText = "削除されたユーザーを有効な状態に戻しますか？";
            confirmBtn.classList.add('restore');
            formAction.value = "restore";
            actionForm.action = "adminUserUpdate";
            break;
    }

    // 共通データのセット
    document.getElementById('modalTargetName').innerText = name;
    formUserId.value = userId;

    // モーダル表示
    modal.style.display = 'flex';
}

/**
 * モーダルを閉じる
 */
function closeModal() {
    const modal = document.getElementById('actionModal');
    if (modal) modal.style.display = 'none';
}

/**
 * ユーザーのリアルタイム絞り込み検索
 */
function filterUsers() {
    const filter = document.getElementById('userSearch').value.toLowerCase();
    const rows = document.querySelectorAll('.user-table tbody tr:not(.empty-msg)');
    let visibleCount = 0;

    rows.forEach(row => {
        const text = row.textContent.toLowerCase();
        const isMatch = text.includes(filter);
        row.style.display = isMatch ? "" : "none";
        if (isMatch) visibleCount++;
    });

    const countDisplay = document.getElementById('visibleCount');
    if (countDisplay) countDisplay.innerText = visibleCount;
}

/**
 * テーブルソート機能の初期化
 */
function initTableSort() {
    const headers = document.querySelectorAll('.sort-header');
    let isAsc = true;
    let currentColumn = 0; 

    // 初期状態のアイコン設定 (ID列)
    headers.forEach(h => {
        const icon = h.querySelector('.sort-icon');
        const colIndex = parseInt(h.dataset.column);
        if (colIndex === currentColumn) {
            if (icon) icon.innerText = ' ▲';
            h.classList.add('active-sort');
        } else {
            if (icon) icon.innerText = ' ↕';
        }
    });

    headers.forEach(header => {
        header.addEventListener('click', () => {
            const column = parseInt(header.dataset.column);
            const tbody = document.querySelector('.user-table tbody');
            const rows = Array.from(tbody.querySelectorAll('tr:not(.empty-msg)'));

            headers.forEach(h => {
                const icon = h.querySelector('.sort-icon');
                if (icon) icon.innerText = ' ↕';
                h.classList.remove('active-sort');
            });

            isAsc = (currentColumn === column) ? !isAsc : true;
            currentColumn = column;
            
            const currentIcon = header.querySelector('.sort-icon');
            if (currentIcon) currentIcon.innerText = isAsc ? ' ▲' : ' ▼';
            header.classList.add('active-sort');

            rows.sort((a, b) => {
                const aCol = a.children[column];
                const bCol = b.children[column];
                const valA = (column === 4) ? parseInt(aCol.dataset.time) : aCol.innerText.trim();
                const valB = (column === 4) ? parseInt(bCol.dataset.time) : bCol.innerText.trim();

                if (column === 0 || column === 4) {
                    return isAsc ? valA - valB : valB - valA;
                }
                return isAsc ? valA.localeCompare(valB, 'ja') : valB.localeCompare(valA, 'ja');
            });

            tbody.append(...rows);
        });
    });
}

/**
 * クリーンアップ処理
 */
function cleanQueryString() {
    // 成功メッセージなどがクエリに残っている場合、リロードしても消えるように履歴を書き換える
    if (window.location.search.includes('action=')) {
        const cleanUrl = window.location.protocol + "//" + window.location.host + window.location.pathname;
        window.history.replaceState({}, document.title, cleanUrl);
    }
}