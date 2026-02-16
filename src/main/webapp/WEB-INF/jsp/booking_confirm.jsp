<%@ page pageEncoding="utf-8" contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
<head>
  <title>NEOcinema-予約確認</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">
  <link rel="stylesheet" href="css/common.css">
  <link rel="stylesheet" href="css/booking.css">

<script>
let currentSid = null;

/* ===== 合計 ===== */
function updateTotal() {
  let total = 0;
  document.querySelectorAll('input[id^="price_"]').forEach(inp => {
    total += parseInt(inp.value || "0", 10);
  });

  const totalEl = document.getElementById('totalPrice');
  const totalIn = document.getElementById('totalPriceInput');
  if (totalEl) totalEl.textContent = '¥' + total.toLocaleString();
  if (totalIn) totalIn.value = total;
}

/* ===== 券種モーダル ===== */
function openModal(sid){
  currentSid = sid;
  document.getElementById("priceModal")?.classList.remove("hidden");
}
function closeModal(){
  document.getElementById("priceModal")?.classList.add("hidden");
  currentSid = null;
}

/* ===== エラーモーダル ===== */
function openErrorModal(message) {
  const msg = document.getElementById("errorMessage");
  if (msg) msg.textContent = message;
  document.getElementById("errorModal")?.classList.remove("hidden");
}
function closeErrorModal() {
  document.getElementById("errorModal")?.classList.add("hidden");
}

/* ===== 새카드 required 제어 ===== */
function setNewCardRequired(on) {
  const num = document.getElementById('newCardNumber');
  const mm  = document.getElementById('newExpMonth');
  const yy  = document.getElementById('newExpYear');
  const brands = document.querySelectorAll('input[name="newBrand"]');

  if (num) num.required = on;
  if (mm)  mm.required  = on;
  if (yy)  yy.required  = on;
  if (brands.length) brands[0].required = on; // radio 그룹 required는 하나만
}

function clearNewCardInputs() {
  const num = document.getElementById('newCardNumber');
  const mm  = document.getElementById('newExpMonth');
  const yy  = document.getElementById('newExpYear');
  const brands = document.querySelectorAll('input[name="newBrand"]');

  if (num) num.value = "";
  if (mm)  mm.value = "";
  if (yy)  yy.value = "";
  brands.forEach(r => r.checked = false);
}

/* ===== details 상태 ===== */
function isNewCardDetailsOpen() {
  const d = document.getElementById('newCardDetails');
  return d ? d.open : false;
}

/* ===== 입력 완료 여부 ===== */
function newCardFilled() {
  const num   = document.getElementById('newCardNumber')?.value.replace(/\s/g,'') || "";
  const brand = document.querySelector('input[name="newBrand"]:checked')?.value || "";
  const mm    = document.getElementById('newExpMonth')?.value || "";
  const yy    = document.getElementById('newExpYear')?.value || "";
  return (num.length >= 13 && brand && mm && yy);
}

function savedCardSelected() {
  return !!document.querySelector('input[name="cardChoiceRadio"]:checked');
}

/* ===== 支払方法 UI ===== */
function syncPaymentUI() {
  const method = document.querySelector('input[name="paymentMethod"]:checked')?.value;
  const cardSec = document.getElementById('cardPaySection');
  if (!cardSec) return;

  const on = (method === 'CARD');
  cardSec.style.display = on ? 'block' : 'none';

  const details = document.getElementById('newCardDetails');
  const hid = document.getElementById('cardChoiceHidden');

  if (!on) {
    // CASH로 바꾸면 카드 관련 전부 초기화
    document.querySelectorAll('input[name="cardChoiceRadio"]').forEach(r => r.checked = false);
    if (details) details.open = false;

    if (hid) hid.value = ""; // ✅ hidden도 초기화
    setNewCardRequired(false);
    clearNewCardInputs();
  } else {
    // CARD면: 새 카드 details가 열려있을 때만 required ON
    setNewCardRequired(isNewCardDetailsOpen());
  }
}

/* (선택) 카드번호 자동 포맷 */
function bindCardFormatter(){
  const cc = document.getElementById('newCardNumber');
  if (!cc) return;
  cc.addEventListener('input', () => {
    let v = cc.value.replace(/\D/g, '').slice(0, 16);
    cc.value = v.replace(/(\d{4})(?=\d)/g, '$1 ');
  });
}

document.addEventListener('DOMContentLoaded', () => {

  /* ===== チケット別料金選択 ===== */
  document.querySelectorAll('.ticket').forEach(ticket => {
    const sid = ticket.getAttribute('data-sid');
    ticket.querySelector('.ticket-pick')?.addEventListener('click', () => openModal(sid));
  });

  document.querySelectorAll('.price-option').forEach(btn => {
    btn.addEventListener('click', () => {
      if(!currentSid) return;

      const priceId = btn.dataset.priceId;
      const price   = btn.dataset.price;
      const cat     = btn.dataset.category;

      const picked = document.getElementById("picked_" + currentSid);
      const hidId  = document.getElementById("priceId_" + currentSid);
      const hidP   = document.getElementById("price_" + currentSid);
      if (!picked || !hidId || !hidP) return;

      picked.textContent = cat + " ¥" + Number(price).toLocaleString();
      hidId.value = priceId;
      hidP.value  = price;

      updateTotal();
      closeModal();
    });
  });

  /* ===== 모달 닫기 ===== */
  document.querySelector('#priceModal .modal-close')?.addEventListener('click', closeModal);
  document.querySelector('#priceModal .modal-overlay')?.addEventListener('click', closeModal);

  document.querySelector('#errorModal .modal-close')?.addEventListener('click', closeErrorModal);
  document.querySelector('#errorModal .modal-overlay')?.addEventListener('click', closeErrorModal);

  /* ===== 支払方法 change ===== */
  document.querySelectorAll('input[name="paymentMethod"]').forEach(r => {
    r.addEventListener('change', syncPaymentUI);
  });

  /* ===== 저장카드 선택 -> hidden에 SAVED_# ===== */
  document.querySelectorAll('input[name="cardChoiceRadio"]').forEach(r => {
    r.addEventListener('change', () => {
      const hid = document.getElementById('cardChoiceHidden');
      if (hid) hid.value = r.value; // SAVED_123

      const details = document.getElementById('newCardDetails');
      if (details) details.open = false;

      setNewCardRequired(false);
      clearNewCardInputs();
    });
  });

  /* ===== 새 카드 details 토글 -> hidden에 NEW / "" ===== */
  document.getElementById('newCardDetails')?.addEventListener('toggle', () => {
    const method = document.querySelector('input[name="paymentMethod"]:checked')?.value;
    if (method !== 'CARD') return;

    const hid = document.getElementById('cardChoiceHidden');
    const open = isNewCardDetailsOpen();

    setNewCardRequired(open);

    if (open) {
      if (hid) hid.value = "NEW";
      // 저장카드 선택 해제
      document.querySelectorAll('input[name="cardChoiceRadio"]').forEach(r => r.checked = false);
    } else {
      if (hid) hid.value = "";
      clearNewCardInputs();
    }
  });

  /* ===== submit 검증(가격 + 결제) ===== */
  const formEl = document.querySelector('form.form');
  formEl?.addEventListener('submit', (e) => {

    // (1) 가격 선택 검증
    let ok = true;
    document.querySelectorAll('input[id^="priceId_"]').forEach(inp => {
      if (parseInt(inp.value || "0", 10) === 0) ok = false;
    });
    if (!ok) {
      e.preventDefault();
      openErrorModal("すべての座席に対して券種（料金）を選択してください。");
      return;
    }

    const total = parseInt(document.getElementById('totalPriceInput')?.value || "0", 10);
    if (total <= 0) {
      e.preventDefault();
      openErrorModal("合計金額が0円です。券種を選択してください。");
      return;
    }

    // (2) 결제 검증
    const method = document.querySelector('input[name="paymentMethod"]:checked')?.value;

    if (method === 'CARD') {
      const savedOk = savedCardSelected();
      const newOpen = isNewCardDetailsOpen();
      const newOk   = newOpen && newCardFilled();

      if (!savedOk && !newOk) {
        e.preventDefault();
        openErrorModal("クレジットカード支払いの場合、登録済みカードを選択するか、新しいカードを追加してください。");
        return;
      }

      if (newOpen && !newOk) {
        e.preventDefault();
        openErrorModal("新しいカードを追加する場合、カード情報をすべて入力してください。");
        return;
      }
    }
  });

  bindCardFormatter();
  syncPaymentUI();
  updateTotal();
});
</script>



<body>
<jsp:include page="header.jsp" />

<div class="container page">
  <div class="page-head">
    <h2 class="page-title">予約内容の確認</h2>
    <p class="page-sub">内容をご確認の上、問題なければ予約を確定してください。</p>
  </div>

  <!-- 上映情報 -->
  <section class="card">
    <div class="card-head">
      <h3 class="card-title">上映情報</h3>
    </div>

    <div class="screening-wrap">
      <c:choose>
        <c:when test="${not empty result.posterUrl}">
          <img src="${result.posterUrl}"
               alt="movie poster"
               style="width:160px; height:220px; object-fit:contain; border-radius:12px; background:#111; border:1px solid #222;">
        </c:when>
        <c:otherwise>
          <div style="width:160px; height:220px; border-radius:12px; background:#111; border:1px solid #222; color:#888;
                      display:flex; align-items:center; justify-content:center; font-weight:800;">
            NO IMAGE
          </div>
        </c:otherwise>
      </c:choose>

      <dl class="info-dl">
        <dt>作品</dt>
        <dd>${result.movieName}</dd>

        <dt>時間</dt>
        <dd>
          <fmt:formatDate value="${result.startTime}" pattern="HH:mm" />
          〜
          <fmt:formatDate value="${result.endTime}" pattern="HH:mm" />
        </dd>

        <dt>スクリーン</dt>
        <dd>${result.screenName}</dd>

        <dt>座席</dt>
        <dd>
          <c:forEach var="sn" items="${result.seatNames}" varStatus="st">
            ${sn}<c:if test="${!st.last}">, </c:if>
          </c:forEach>
        </dd>
      </dl>
    </div>
  </section>

  <!-- 座席 -->
  <section class="card">
    <div class="card-head">
      <h3 class="card-title">選択した座席</h3>
    </div>
    <div class="card-body">
      <ul class="seat-list">
        <c:forEach var="sn" items="${result.seatNames}">
          <li class="seat-chip">${sn}</li>
        </c:forEach>
      </ul>
    </div>
  </section>

  <form class="form" action="${pageContext.request.contextPath}/booking" method="post">
    <input type="hidden" name="scheduleId" value="${result.scheduleId}" />
    <input type="hidden" name="cardChoice" id="cardChoiceHidden" value="">

    <c:forEach var="sid" items="${result.seatIds}">
      <input type="hidden" name="seatIds" value="${sid}" />
    </c:forEach>

    <c:forEach var="sn" items="${result.seatNames}">
      <input type="hidden" name="seatNames" value="${sn}" />
    </c:forEach>

    <!-- チケット価格 -->
    <section class="card">
      <div class="card-head">
        <h3 class="card-title">チケットごとの料金</h3>
      </div>
      <div class="card-body">
        <div class="ticket-grid">
          <c:forEach var="sid" items="${result.seatIds}" varStatus="st">
            <div class="ticket" data-sid="${sid}">
              <div class="ticket-body">
                <button type="button" class="ticket-pick">券種を選択</button>
                <div class="picked-text" id="picked_${sid}">未選択</div>
                <input type="hidden" name="priceId_${sid}" id="priceId_${sid}" value="0">
                <input type="hidden" name="price_${sid}" id="price_${sid}" value="0">
              </div>
            </div>
          </c:forEach>
        </div>
      </div>
    </section>

    <!-- 支払方法 -->
    <section class="card">
      <div class="card-head">
        <h3 class="card-title">支払方法</h3>
      </div>

      <div class="card-body">
        <div class="payment-grid">
          <label class="pay-item">
            <input type="radio" name="paymentMethod" value="CARD" required>
            クレジットカード
          </label>
          <label class="pay-item">
            <input type="radio" name="paymentMethod" value="CASH" required>
            現金払い
          </label>
        </div>

        <!-- カード払い -->
        <div id="cardPaySection" class="card-pay" style="display:none; margin-top:12px;">
          <h4 class="sub-title">カード選択</h4>

          <!-- 登録済みカード -->
          <details class="card-acc" open>
            <summary class="card-acc-sum">
              <span>登録済みカード</span>
              <span class="card-acc-sub">
                <c:choose>
                  <c:when test="${empty result.cards}">0枚</c:when>
                  <c:otherwise>${fn:length(result.cards)}枚</c:otherwise>
                </c:choose>
              </span>
            </summary>

            <div class="card-choice-list">
              <c:if test="${empty result.cards}">
                <p class="muted">登録済みカードがありません。</p>
              </c:if>

              <c:forEach var="c" items="${result.cards}">
                <label class="card-option">
                  <input type="radio" name="cardChoiceRadio" value="SAVED_${c.cardId}">
                  <span class="brand">${c.brand}</span>
                  <span class="masked">•••• ${c.last4}</span>
                  <span class="exp">(<fmt:formatNumber value="${c.expMonth}" pattern="00"/>/${c.expYear})</span>
                </label>
              </c:forEach>
            </div>
          </details>

<!-- 新しいカードを追加 -->
<details class="card-acc" id="newCardDetails">
  <summary class="card-acc-sum">＋ 新しいカードを追加</summary>

  <div class="new-card" style="margin-top:10px;">
    <div class="field-grid">

      <label class="field">
        <span>カード番号</span>
        <input type="text" name="newCardNumber" id="newCardNumber"
               inputmode="numeric" autocomplete="cc-number"
               placeholder="1234 5678 9012 3456" maxlength="19">
      </label>

      <label class="field">
        <span>ブランド</span>
        <div class="brand-toggle">
          <label class="brand-btn">
            <input type="radio" name="newBrand" value="VISA"> VISA
          </label>
          <label class="brand-btn">
            <input type="radio" name="newBrand" value="MASTER"> MASTER
          </label>
        </div>
      </label>

      <label class="field">
        <span>有効期限(月)</span>
        <select name="newExpMonth" id="newExpMonth" autocomplete="cc-exp-month">
          <option value="">MM</option>
          <c:forEach var="m" begin="1" end="12">
            <option value="${m}"><fmt:formatNumber value="${m}" pattern="00"/></option>
          </c:forEach>
        </select>
      </label>

      <label class="field">
        <span>有効期限(年)</span>
       <input type="text"
       name="newExpYear"
       id="newExpYear"
       inputmode="numeric"
       autocomplete="cc-exp-year"
       placeholder="YYYY"
       maxlength="4">
      </label>

    
      <div class="field save-card-field">
        <label class="save-card-label">
          <input type="checkbox" name="saveCard" value="1">
              このカードを保存する
        </label>
      </div>

                
              </div>
            </div>
          </details>
        </div>
      </div>
    </section>

    <!-- 合計  -->
    <section class="card total-card">
      <div class="card-head">
        <h3 class="card-title">合計金額</h3>
      </div>
      <div class="card-body">
        <span id="totalPrice">¥0</span>
        <input type="hidden" name="totalPrice" id="totalPriceInput" value="0">
      </div>
    </section>

    <div class="actions">
      <a button class="btn btn-primary"  href="javascript:history.back()">戻る</a>
      <button class="btn btn-primary" type="submit">予約確定</button>
    </div>
  </form>
</div>

<!-- 価格モーダル -->
<div id="priceModal" class="modal hidden">
  <div class="modal-overlay"></div>
  <div class="modal-content">
    <h3>券種を選択</h3>
    <c:forEach var="p" items="${result.prices}">
      <button type="button" class="price-option"
        data-price-id="${p.priceId}"
        data-price="${p.price}"
        data-category="${p.category}">
        ${p.category} ￥${p.price}
      </button>
    </c:forEach>
    <button type="button" class="modal-close">キャンセル</button>
  </div>
</div>

<!-- エラーモーダル -->
<div id="errorModal" class="modal hidden">
  <div class="modal-overlay"></div>
  <div class="modal-content">
    <h3>エラー</h3>
    <p id="errorMessage" style="margin:12px 0; font-weight:600;"></p>
    <button type="button" class="modal-close">OK</button>
  </div>
</div>

</body>
</html>
