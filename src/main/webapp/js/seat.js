document.querySelectorAll("#seat-area .seat-btn").forEach(btn => {
  seatBtnById.set(String(btn.dataset.seatId), btn);
});

const selectedSeatNames = new Set();
const selectedSeatIds = new Map(); // key: seatName, value: seatId

function renderBottomBar() {
  const wrap = document.getElementById("selected-wrap");
  wrap.innerHTML = "";

  const names = [...selectedSeatNames];

  names.forEach(name => {
    const pill = document.createElement("span");
    pill.className = "pill";
    pill.textContent = name.replace("-", " ");
    pill.dataset.seatName = name;
    pill.dataset.seatId = selectedSeatIds.get(name);

    pill.addEventListener("click", (e) => {
      const seatName = e.currentTarget.dataset.seatName;
      const seatId   = e.currentTarget.dataset.seatId;
      unselectById(seatId, seatName);
    });

    wrap.appendChild(pill);
  });


  const pc = document.getElementById("p-count");
  const ps = document.getElementById("p-seats");
  if (pc) pc.textContent = String(names.length);
  if (ps) ps.textContent = names.length ? names.join(", ") : "-";
}

function unselectById(seatId, seatName) {
  selectedSeatNames.delete(seatName);
  selectedSeatIds.delete(seatName);

  const btn = document.querySelector(
  `#seat-area button[data-seat-id="${seatId}"]`
);
  if (btn) btn.classList.remove("select");

  renderBottomBar();
}

function fetchSeatData() {
  const scheduleId = new URLSearchParams(window.location.search).get('scheduleId');
  const url = `${BASE}/seat?scheduleId=${encodeURIComponent(scheduleId)}`;
  fetch(url)
    .then(res => res.json())
    .then(resJson => showSeat(resJson));
}

function showSeat(seats) {
  const seatArea = document.getElementById("seat-area");
  seatArea.innerHTML = "";

  const seatIds = seats.map(seat => seat.seat_id).sort((a,b)=>a-b);
  let currentRow = '';

  seatIds.forEach(seatId => {
    const seat = seats.find(s => s.seat_id === seatId);
    const seatName = seat.seat_name;
    const reserved = seat.reserved;
    const row = seatName.split('-')[0];

    if (row !== currentRow) {
      if (currentRow !== '') seatArea.appendChild(document.createElement("br"));
      currentRow = row;
    }

    const button = document.createElement("button");
    button.type = "button";
    button.textContent = seatName;
    button.id = seatId;

    
    button.dataset.seatId = seatId;
    button.dataset.seatName = seatName;

    button.className = reserved ? "seat-reserved" : "seat-free";
    button.disabled = reserved;

    button.addEventListener("click", () => {
      button.classList.toggle("select");

      if (button.classList.contains("select")) {
        selectedSeatNames.add(seatName);
        selectedSeatIds.set(seatName, String(seatId));
      } else {
        selectedSeatNames.delete(seatName);
        selectedSeatIds.delete(seatName);
      }

      renderBottomBar();
    });

    seatArea.appendChild(button);
  });

  renderBottomBar();
}

function sendSelectSeats(seatsNodeList) {
  const form = document.getElementById("seat-form");
  form.querySelectorAll('input[name="seatIds"]').forEach(e => e.remove());

  Array.from(seatsNodeList).forEach(btn => {
    const input = document.createElement("input");
    input.type = "hidden";
    input.name = "seatIds";
    input.value = btn.id;
    form.appendChild(input);
  });

  form.submit();
}

/* 페이지 로드 시 실행 */
document.addEventListener("DOMContentLoaded", fetchSeatData);
