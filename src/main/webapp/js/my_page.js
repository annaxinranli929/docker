document.addEventListener('DOMContentLoaded', () => {
    const modal = document.getElementById('confirmModal');
    const modalMessage = document.getElementById('modalMessage');
    const modalTargetValue = document.getElementById('modalTargetValue');
    const modalTargetLabel = document.getElementById('modalTargetLabel'); // ラベル要素
    const cancelBtn = document.getElementById('modalCancelBtn');
    const confirmBtn = document.getElementById('modalConfirmBtn');
    let activeForm = null;

    document.querySelectorAll('.confirm-trigger').forEach(btn => {
        btn.addEventListener('click', () => {
            activeForm = btn.closest('form');
            modalMessage.innerHTML = btn.getAttribute('data-message');
            modalTargetValue.textContent = btn.getAttribute('data-target');

            // --- 色の切り替えロジック ---
            if (btn.classList.contains('btn-danger-block')) {
                // アカウント削除（赤ベース）
                modal.classList.add('mode-danger');
                modal.classList.remove('mode-blue');
                modalTargetLabel.textContent = "削除されるデータ";
            } else {
                // 映画キャンセル（青ベース）
                modal.classList.add('mode-blue');
                modal.classList.remove('mode-danger');
                modalTargetLabel.textContent = "キャンセル対象";
            }

            modal.style.display = 'block';
        });
    });

    const close = () => { modal.style.display = 'none'; activeForm = null; };
    cancelBtn.addEventListener('click', close);
    confirmBtn.addEventListener('click', () => { if(activeForm) activeForm.submit(); });
    window.addEventListener('click', (e) => { if(e.target === modal) close(); });
});