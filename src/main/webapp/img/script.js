document.addEventListener('DOMContentLoaded', () => {
    const timeAxis = document.getElementById('time-axis');
    const startHour = 9;
    const endHour = 21;

    // 時間軸（9:00 〜 21:00）を自動生成
    for (let i = startHour; i <= endHour; i++) {
        const mark = document.createElement('div');
        mark.className = 'time-mark';
        mark.innerText = `${i}:00`;
        timeAxis.appendChild(mark);
    }

    // ドラッグ＆ドロップの準備（簡易的な実装例）
    const movieItems = document.querySelectorAll('.movie-item');
    movieItems.forEach(item => {
        item.addEventListener('dragstart', (e) => {
            e.dataTransfer.setData('text/plain', e.target.innerText);
        });
    });

    const columns = document.querySelectorAll('.screen-column');
    columns.forEach(column => {
        column.addEventListener('dragover', (e) => {
            e.preventDefault(); // ドロップを許可
        });

        column.addEventListener('drop', (e) => {
            e.preventDefault();
            const movieName = e.dataTransfer.getData('text/plain');
            
            // マウスの位置から時間を計算（簡易版）
            const rect = column.getBoundingClientRect();
            const y = e.clientY - rect.top;
            const gridY = Math.floor(y / 30) * 30; // 15分(30px)単位にスナップ

            // 新しいカードを作成
            const newCard = document.createElement('div');
            newCard.className = 'movie-card';
            newCard.style.top = `${gridY}px`;
            newCard.style.height = '120px'; // デフォルト1時間
            newCard.style.background = '#e1f5fe';
            newCard.style.border = '1px solid #03a9f4';
            
            newCard.innerHTML = `
                <div class="card-content">
                    <span class="movie-title">${movieName}</span>
                    <span class="time-range">新規上映</span>
                </div>
                <span class="delete-x" onclick="this.parentElement.remove()">×</span>
            `;
            
            column.appendChild(newCard);
        });
    });
});