let currentCinemaId = ""; 
let currentDate = "";    

document.addEventListener("DOMContentLoaded", () => {
    markSearchWord();

    // 1. 初期選択状態の取得
    const firstCinemaBtn = document.querySelector(".cinema-tab.active");
    if (firstCinemaBtn) {
        currentCinemaId = firstCinemaBtn.id;
        const firstDateBtn = document.querySelector(`#cont-${currentCinemaId} .date-button.active`);
        if (firstDateBtn) {
            currentDate = firstDateBtn.id.split('-').slice(2).join('-');
        }
    }

    // 2. 劇場タブクリックイベント
    document.querySelectorAll(".cinema-tab").forEach(btn => {
        btn.addEventListener("click", () => {
            currentCinemaId = btn.id;
            
            // 劇場が変わったらその劇場の最初の日付を自動選択
            const firstDateBtn = document.querySelector(`#cont-${currentCinemaId} .date-button`);
            if (firstDateBtn) {
                currentDate = firstDateBtn.id.split('-').slice(2).join('-');
            }

            // タブの見た目更新
            document.querySelectorAll(".cinema-tab").forEach(b => {
                b.classList.remove("active");
                b.classList.add("non-active");
            });
            btn.classList.add("active");
            btn.classList.remove("non-active");

            updateUI();
        });
    });

    // 3. 日付ボタンクリックイベント（委譲）
    document.addEventListener("click", (e) => {
        if (e.target.classList.contains("date-button")) {
            currentDate = e.target.id.split('-').slice(2).join('-');
            updateUI();
        }
    });

    updateUI();
});

function updateUI() {
    // 全劇場コンテナを隠す
    document.querySelectorAll('[name="cinema"]').forEach(el => el.style.display = "none");

    const activeCont = document.getElementById(`cont-${currentCinemaId}`);
    if (activeCont) {
        activeCont.style.display = "block";

        // 劇場内の日付ボタンの状態更新
        activeCont.querySelectorAll(".date-button").forEach(btn => {
            const btnDate = btn.id.split('-').slice(2).join('-');
            btn.classList.toggle("active", btnDate === currentDate);
        });

        // スケジュール枠の表示
        activeCont.querySelectorAll(".date-group").forEach(g => g.style.display = "none");
        const activeGroup = document.getElementById(`group-${currentCinemaId}-${currentDate}`);
        if (activeGroup) {
            activeGroup.style.display = "block";
        }
    }
}

function markSearchWord() {
    const params = new URLSearchParams(window.location.search);
    const keyword = params.get("summary"); // 検索ワードを取得
    if (!keyword || !keyword.trim()) return;

    const summaryElement = document.getElementById("movie-summary-text");
    if (!summaryElement) return;

    // ひらがな・カタカナを相互に変換する関数
    const toKanaPattern = (str) => {
        return str.split('').map(char => {
            const code = char.charCodeAt(0);
            // ひらがな -> カタカナの範囲
            if (code >= 0x3041 && code <= 0x3096) {
                const kana = String.fromCharCode(code + 0x60);
                return `[${char}${kana}]`;
            }
            // カタカナ -> ひらがなの範囲
            if (code >= 0x30A1 && code <= 0x30F6) {
                const hira = String.fromCharCode(code - 0x60);
                return `[${char}${hira}]`;
            }
            // それ以外（漢字・英数字など）はそのまま
            return char.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'); 
        }).join('');
    };

    const pattern = toKanaPattern(keyword);
    const regex = new RegExp(`(${pattern})`, "gi");

    console.log(pattern);

    summaryElement.innerHTML = summaryElement.innerText.replace(regex, '<span class="highlight">$1</span>');
}