function setNewCardRequired(on) {
  const num = document.getElementById('newCardNumber');
  const mm  = document.getElementById('newExpMonth');
  const yy  = document.getElementById('newExpYear');
  const brands = document.querySelectorAll('input[name="newBrand"]');

  if (num) num.required = on;
  if (mm)  mm.required  = on;
  if (yy)  yy.required  = on;
  if (brands.length) brands[0].required = on;
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

function isNewCardDetailsOpen() {
  const d = document.getElementById('newCardDetails');
  return d ? d.open : false;
}

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

function syncPaymentUI() {
  const method = document.querySelector('input[name="paymentMethod"]:checked')?.value;
  const cardSec = document.getElementById('cardPaySection');
  if (!cardSec) return;

  const on = (method === 'CARD');
  cardSec.style.display = on ? 'block' : 'none';

  const details = document.getElementById('newCardDetails');
  const hid = document.getElementById('cardChoiceHidden');

  if (!on) {
    // CASH
    document.querySelectorAll('input[name="cardChoiceRadio"]').forEach(r => r.checked = false);
    if (details) details.open = false;

    if (hid) hid.value = "";
    setNewCardRequired(false);
    clearNewCardInputs();
  } else {
    // CARD
    setNewCardRequired(isNewCardDetailsOpen());
  }
}

document.addEventListener('DOMContentLoaded', () => {
  // 결제수단 변경
  document.querySelectorAll('input[name="paymentMethod"]').forEach(r => {
    r.addEventListener('change', syncPaymentUI);
  });

  // 저장카드 선택 -> hidden에 SAVED_# 세팅 + 새카드 닫기
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

  // 새카드 details 토글 -> hidden에 NEW 세팅/해제
  document.getElementById('newCardDetails')?.addEventListener('toggle', () => {
    const method = document.querySelector('input[name="paymentMethod"]:checked')?.value;
    if (method !== 'CARD') return;

    const open = isNewCardDetailsOpen();
    setNewCardRequired(open);

    const hid = document.getElementById('cardChoiceHidden');
    if (open) {
      if (hid) hid.value = "NEW";
      // 저장카드 선택 해제
      document.querySelectorAll('input[name="cardChoiceRadio"]').forEach(r => r.checked = false);
    } else {
      if (hid) hid.value = "";
      clearNewCardInputs();
    }
  });

  syncPaymentUI();
});
