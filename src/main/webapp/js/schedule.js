document.addEventListener("DOMContentLoaded", () => {

    // 劇場ボタン切替
    const cinemaButtons = document.querySelectorAll("[id$='-button']");
    cinemaButtons.forEach(btn => {
        btn.addEventListener("click", () => {
            const cinemaId = btn.id.replace("-button", "");

            // ボタン見た目
            cinemaButtons.forEach(b => {
                b.classList.remove("active");
                b.classList.add("non-active");
            });
            btn.classList.add("active");
            btn.classList.remove("non-active");

            // パネル切替
            document.querySelectorAll(".cinema-panel").forEach(p => p.style.display = "none");
            const targetPanel = document.getElementById(cinemaId);
            if (targetPanel) {
                targetPanel.style.display = "block";

                // 最初の日付ボタンを自動クリック
                const firstDateBtn = targetPanel.querySelector(".date-button");
                if (firstDateBtn) firstDateBtn.click();
            }
        });
    });

    // 日付ボタン切替
    const dateButtons = document.querySelectorAll(".date-button");
    dateButtons.forEach(btn => {
        btn.addEventListener("click", () => {
            const targetId = btn.getAttribute("data-target");
            const panel = btn.closest(".cinema-panel");

            // ボタン active 切替
            panel.querySelectorAll(".date-button").forEach(b => b.classList.remove("active"));
            btn.classList.add("active");

            // コンテンツ切替
            panel.querySelectorAll(".date-content").forEach(c => c.style.display = "none");
            const targetContent = document.getElementById(targetId);
            if (targetContent) targetContent.style.display = "block";
        });
    });

    // ログイン/ログアウトメッセージフェードアウト
    setTimeout(() => {
        const msg = document.getElementById('loginLogoutMessage');
        if(msg){
            msg.classList.add("fade-out");
            setTimeout(() => msg.remove(), 500);
        }
    }, 1500);
});

// ページ読み込み時にメッセージをフェードアウト
document.addEventListener("DOMContentLoaded", () => {
    const msg = document.getElementById('loginLogoutMessage');
    if(msg){
        // 2秒後にフェードアウト
        setTimeout(() => {
            msg.classList.add('fade-out');
            setTimeout(() => msg.remove(), 500); // 0.5秒後に完全削除
        }, 2000);
    }
});

