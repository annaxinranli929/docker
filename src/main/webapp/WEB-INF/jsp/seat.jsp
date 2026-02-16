<%@ page pageEncoding="utf-8" contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">
    <title>座席選択 - NEOcinema</title>
    <link rel="stylesheet" href="css/common.css">
    <link rel="stylesheet" href="css/seat.css">
    
    


</head>
<body>

<h2 class="page-title"></h2>

<div class="container">

  <!--座席アイコン情報 -->
  <div class="page-actions">
  <a class="btn-back" href="${pageContext.request.contextPath}/">トップへ</a>
  </div>
  <section class="legend">
    <h3>アイコン説明</h3>
    <div class="legend-items">
      <div class="legend-item">
        <span class="legend-box legend-empty"></span>
        <span>空席（購入可能）</span>
      </div>
      <div class="legend-item">
        <span class="legend-box legend-selected"></span>
        <span>選択した席</span>
      </div>
      <div class="legend-item">
        <span class="legend-box legend-sold"></span>
        <span>購入済み／販売対象外</span>
      </div>
    </div>
  </section>


  <main class="content">


    <section class="seat-area-wrap">

      <div class="screen-container">
        <div class="screen"></div>
        <div class="screen-text">SCREEN</div>
      </div>

      
      <div id="seat-area">


        <c:set var="maxCol" value="0" />
        <c:forEach var="s" items="${result.seats}">
          <c:set var="colNum" value="${fn:split(s.seatName, '-')[1] + 0}" />
          <c:if test="${colNum > maxCol}">
            <c:set var="maxCol" value="${colNum}" />
          </c:if>
        </c:forEach>

        <%-- 列番号ヘッダー --%>
        <div class="seat-row header-row">
          <div class="row-label"></div>
          <c:forEach begin="1" end="${maxCol}" var="i">
            <div class="col-label">${i}</div>
          </c:forEach>
        </div>

        <c:set var="currentRow" value="" />
        <c:set var="lastSeatNum" value="0" />
        <c:set var="rowCount" value="0" />

        <c:forEach var="s" items="${result.seats}" varStatus="status">
          <c:set var="seatParts" value="${fn:split(s.seatName, '-')}" />
          <c:set var="rowName" value="${seatParts[0]}" />
          <c:set var="currentSeatNum" value="${seatParts[1] + 0}" />

          <c:if test="${rowName ne currentRow}">
            <c:if test="${not empty currentRow}"></div></c:if>

            <c:set var="rowCount" value="${rowCount + 1}" />

            <div class="seat-row ${ (rowCount > 1 && (rowCount - 1) % 5 == 0) ? 'aisle-top' : '' }">
              <div class="row-label">${rowName}</div>

            <c:set var="currentRow" value="${rowName}" />
            <c:set var="lastSeatNum" value="0" />
          </c:if>

          <c:if test="${currentSeatNum - lastSeatNum > 1}">
            <c:forEach begin="1" end="${currentSeatNum - lastSeatNum - 1}">
              <div class="seat-spacer"></div>
            </c:forEach>
          </c:if>

          <button type="button"
                  class="seat-btn ${s.reserved ? 'seat-reserved' : 'seat-free'}"
                  data-seat-id="${s.seatId}"
                  data-seat-name="${s.seatName}"
                  ${s.reserved ? 'disabled' : ''}>
            <c:if test="${!s.reserved}">
              <span>${rowName}</span>
              <span>${seatParts[1]}</span>
            </c:if>
          </button>

          <c:set var="lastSeatNum" value="${currentSeatNum}" />
          <c:if test="${status.last}"></div></c:if>
        </c:forEach>

      </div>

      
      <form id="seat-form" action="${pageContext.request.contextPath}/showBookingConfirm" method="post">
        <input type="hidden" name="scheduleId" value="${result.scheduleId}">
      </form>

    </section>

    <!-- みぎがわの購入情報 -->
 <aside class="purchase">
    <c:choose>
    <c:when test="${not empty result.posterUrl}">
      <img class="purchase-poster" src="${result.posterUrl}" alt="movie poster">
    </c:when>
    <c:otherwise>
      <div class="purchase-poster placeholder">NO IMAGE</div>
    </c:otherwise>
  </c:choose>

  <h3>ご購入内容</h3>

  <dl>
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

    <dt>人数</dt>
    <dd><span id="p-count">0</span>名</dd>

    <dt>選択席</dt>
    <dd id="p-seats">-</dd>
  </dl>

</aside>

<!-- 画面下部の固定バー-->
<footer class="bottom-bar">
  <div class="selected-wrap" id="selected-wrap"></div>
  <button type="button" class="btn-clear" id="btn-clear">解除</button>
  <button type="button" id="submit-button">決定</button>
</footer>

<script>
  const selectedSeatNames = new Set();
  const selectedSeatIds = new Map();   // key: seatName, value: seatId(string)
  const seatBtnById = new Map();       // key: seatId(string), value: button element


  document.querySelectorAll("#seat-area .seat-btn").forEach(btn => {
    seatBtnById.set(String(btn.dataset.seatId), btn);
  });

  function renderBottomBar() {
    const wrap = document.getElementById("selected-wrap");
    wrap.innerHTML = "";

    const names = [...selectedSeatNames];

    names.forEach(seatName => {
      const seatId = String(selectedSeatIds.get(seatName)); // 반드시 string

      const pill = document.createElement("span");
      pill.className = "pill";
      pill.textContent = seatName.replace("-", " ");
      pill.dataset.seatName = seatName;
      pill.dataset.seatId = seatId;

      pill.addEventListener("click", (e) => {
        const name = e.currentTarget.dataset.seatName;
        const id   = e.currentTarget.dataset.seatId;
        unselectById(id, name);
      });

      wrap.appendChild(pill);
    });

    document.getElementById("p-count").textContent = String(names.length);
    document.getElementById("p-seats").textContent = names.length ? names.join(", ") : "-";
  }

  function unselectById(seatId, seatName) {
    seatId = String(seatId);

    selectedSeatNames.delete(seatName);
    selectedSeatIds.delete(seatName);

 
    const btn = seatBtnById.get(seatId);
    if (btn) btn.classList.remove("select");

    renderBottomBar();
  }

  // 座席クリック解除
  document.getElementById("seat-area").addEventListener("click", (e) => {
    const btn = e.target.closest(".seat-btn");
    if (!btn || btn.disabled) return;

    const seatName = btn.dataset.seatName;
    const seatId   = String(btn.dataset.seatId);

    btn.classList.toggle("select");

    if (btn.classList.contains("select")) {
      selectedSeatNames.add(seatName);
      selectedSeatIds.set(seatName, seatId);
    } else {
      selectedSeatNames.delete(seatName);
      selectedSeatIds.delete(seatName);
    }

    renderBottomBar();
  });
  
  document.getElementById("btn-clear").addEventListener("click", () => {
    selectedSeatNames.clear();
    selectedSeatIds.clear();

    document.querySelectorAll("#seat-area .seat-btn.select")
      .forEach(el => el.classList.remove("select"));

    renderBottomBar();
  });

  
  document.getElementById("submit-button").addEventListener("click", () => {
    if (selectedSeatNames.size === 0) {
      alert("座席を選択してください。");
      return;
    }

    const form = document.getElementById("seat-form");
    form.querySelectorAll('input[name="seatIds"], input[name="seatNames"]').forEach(el => el.remove());

    [...selectedSeatNames].forEach(name => {
      const id = String(selectedSeatIds.get(name));

      const inputId = document.createElement("input");
      inputId.type = "hidden";
      inputId.name = "seatIds";
      inputId.value = id;
      form.appendChild(inputId);

      const inputName = document.createElement("input");
      inputName.type = "hidden";
      inputName.name = "seatNames";
      inputName.value = name;
      form.appendChild(inputName);
    });

    form.submit();
  });

  
  renderBottomBar();
</script>


</body>

</html>