<%@ page pageEncoding="utf-8" contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>上映スケジュール管理</title>
  <style>
    body { font-family: sans-serif; margin: 16px; }

    .admin-badge{
      position: fixed; top: 12px; right: 14px; z-index: 9999;
      border: 1px solid #ccc; background: #fff;
      padding: 6px 10px; border-radius: 999px; font-size: 13px;
    }
    .admin-badge a{ text-decoration: none; }
    .dot{
      display: inline-block; width: 9px; height: 9px; border-radius: 50%;
      margin-right: 6px; vertical-align: middle;
    }
    .dot.ok{ background: #2ecc71; }
    .dot.ng{ background: #e74c3c; }

    .layout{ display:flex; gap: 16px; align-items:flex-start; }
    .left{ width: 38%; min-width: 360px; }
    .right{ flex: 1; }

    .card{ border:1px solid #ddd; border-radius: 10px; padding: 12px; background: #fff; }
    .muted{ color:#666; font-size: 12px; }
    .ok{ color:#0a0; }
    .error{ color:#d00; }

    .movie-list{
      border:1px solid #eee; border-radius: 10px;
      padding: 10px; max-height: 340px; overflow:auto;
      display:flex; flex-wrap: wrap; gap: 8px;
      background: #fafafa;
    }
    .movie-btn{
      border:1px solid #ddd; background:#fff;
      padding: 6px 10px; border-radius: 999px;
      cursor:pointer; font-size: 13px;
    }
    .movie-btn:hover{ background:#f2f2f2; }
    .movie-btn.active{ border-color:#333; font-weight: 600; }

    .selected-movie { margin-left: 40px; }

    label{ display:inline-block; width: 90px; }
    input[type="text"], select, input[type="datetime-local"]{
      padding: 6px; border:1px solid #ccc; border-radius: 6px;
    }
    .row{ margin: 8px 0; }
    .btn{
      padding: 8px 12px; border:1px solid #333; background:#fff;
      border-radius: 8px; cursor:pointer;
    }
    .btn:hover{ background:#f2f2f2; }

    .btn-s{ padding: 4px 8px; font-size: 12px; border-radius: 6px; }

    table{ width: 100%; border-collapse: collapse; }
    th, td{ border-bottom: 1px solid #eee; padding: 8px; text-align:left; font-size: 13px; vertical-align: top; }
    th{ background:#fafafa; position: sticky; top: 0; z-index: 1; }

    /* column widths */
    .col-time{ width: 150px; }
    .col-movie{ width: 300px; }
    .col-theater{ width: 150px; }
    .col-status{ width: 80px; text-align:center; }
    .col-actions{ width: 120px; }

    td{
      vertical-align: top;
    }
    .col-movie, .col-theater{
      word-break: break-word;
    }

    .badge{
      display:inline-block; padding: 2px 8px; border-radius: 999px;
      border:1px solid #ddd; font-size: 12px; background:#fff;
    }
    .actions{
      display: flex;
      align-items: center;
      gap: 6px;
      flex-wrap: nowrap;   /* keep in one row */
    }

    .actions form{ margin: 0; }     
    .actions details{ margin-bottom: 5px; display: inline-block; }  
    .actions summary{ margin: 0; }  

    /* softer conflict box */
    .notice{
      border: 1px solid #eee;
      background: #fcfcfc;
      border-radius: 10px;
      padding: 10px 12px;
      margin: 10px 0;
    }
    .notice-title{
      font-weight: 600;
      font-size: 13px;
      color: #444;
      margin-bottom: 6px;
    }
    .notice-list{
      margin: 0;
      padding-left: 18px;
      color: #555;
      font-size: 12px;
      line-height: 1.45;
    }
    .notice-list code{
      font-size: 11px;
      background: #f3f3f3;
      padding: 1px 6px;
      border-radius: 999px;
      border: 1px solid #eee;
    }

    .seg{
      display:inline-flex;
      border:1px solid #ddd;
      border-radius: 999px;
      overflow:hidden;
      background:#fff;
    }
    .seg a{
      padding: 6px 10px;
      font-size: 12px;
      text-decoration:none;
      color:#333;
      border-right:1px solid #eee;
    }
    .seg a:last-child{ border-right:none; }
    .seg a.active{
      background:#333;
      color:#fff;
    }
  </style>

  <script>
    const BUFFER_MINUTES = 10;
    let selectedRuntime = null;

    function setMovie(movieId, movieName, el){
      document.getElementById("movieId").value = movieId;
      document.getElementById("selectedMovieName").textContent = movieName;

      // get runtime from button data-runtime
      selectedRuntime = parseInt(el.dataset.runtime || "", 10);
      if (Number.isNaN(selectedRuntime)) selectedRuntime = null;

      document.querySelectorAll(".movie-btn").forEach(b => b.classList.remove("active"));
      if (el) el.classList.add("active");

      // refresh preview immediately when movie changes
      updatePreview();
    }

    function validateMovie(){
      if(!document.getElementById("movieId").value){
        alert("映画を選択してください。");
        return false;
      }
      return true;
    }

    function pad(n){ return String(n).padStart(2,'0'); }

    function formatLocal(dt){
      return dt.getFullYear() + '-' + pad(dt.getMonth()+1) + '-' + pad(dt.getDate())
        + ' ' + pad(dt.getHours()) + ':' + pad(dt.getMinutes()) + ':00';
    }

    function updatePreview(){
      const startInput = document.querySelector('input[name="startTime"]');
      const el = document.getElementById('previewEnd');
      const hidden = document.getElementById('endTimePreview');

      const startVal = startInput ? startInput.value : '';

      if(!startVal || !selectedRuntime){
        el.textContent = '—';
        hidden.value = '';
        return;
      }

      const start = new Date(startVal); // datetime-local is OK
      const end = new Date(start.getTime() + (selectedRuntime + BUFFER_MINUTES) * 60000);

      el.textContent = formatLocal(end);
      hidden.value = formatLocal(end);
    }

    window.addEventListener('DOMContentLoaded', () => {
      const start = document.querySelector('input[name="startTime"]');
      if (start) start.addEventListener('change', updatePreview);
    });
  </script>
</head>

<body>
  <div class="admin-badge">
    <c:choose>
      <c:when test="${sessionScope.isAdmin}">
        <span class="dot ok"></span> Admin: LOGIN
      </c:when>
      <c:otherwise>
        <span class="dot ng"></span> Admin: LOGOUT |
        <a href="adminLogin">Login</a>
      </c:otherwise>
    </c:choose>
  </div>

  <h2>上映スケジュール管理</h2>

  <c:if test="${param.saved == '1'}">
    <p class="ok">保存しました。</p>
  </c:if>
  <c:if test="${not empty result.message}">
    <p class="${result.ok ? 'ok' : 'error'}">${result.message}</p>
  </c:if>
  <c:if test="${not empty result.conflicts}">
    <div class="notice">
      <div class="notice-title">時間が重複しているスケジュールがあります</div>
      <ul class="notice-list">
        <c:forEach var="c" items="${result.conflicts}">
          <li>
            <code>#${c.scheduleId}</code>
            ${fn:substring(c.startTime, 0, 16)} 〜 ${fn:substring(c.endTime, 0, 16)}
            ／ ${c.movieName}
            <span class="muted">（screenId=${c.screenId}）</span>
          </li>
        </c:forEach>
      </ul>
    </div>
  </c:if>

  <div class="layout">
    <!-- LEFT: add -->
    <div class="left">
      <div class="card">
        <h3 style="margin-top:0;">スケジュール追加</h3>

        <div class="row">
          <div class="muted">映画選択</div>
          <div class="movie-list">
            <c:if test="${empty result.movies}">
              <span class="muted">moviesがありません（result.movies をセットしてね）</span>
            </c:if>

            <c:forEach var="m" items="${result.movies}">
              <button type="button"
                class="movie-btn"
                data-id="${m.movieId}"
                data-name="${fn:escapeXml(m.movieName)}"
                data-runtime="${m.runtimeMinutes}"
                onclick="setMovie(this.dataset.id, this.dataset.name, this)">
                ${m.movieName}
              </button>
            </c:forEach>

          </div>
          <div class="row" style="margin-top:15px;">
            選択中: <span class="selected-movie"> <b id="selectedMovieName">（未選択）</b>
          </div>
        </div>

        <form action="adminSaveSchedule" method="post" onsubmit="return validateMovie();" style="margin-top:12px;">
          <input type="hidden" name="action" value="insert">
          <input id="movieId" type="hidden" name="movieId">

          <div class="row">
            <label>スクリーン</label>
            <select name="screenId" required>
              <c:forEach var="s" items="${result.screens}">
                <option value="${s.screenId}">
                  ${s.cinemaName} - ${s.screenName} (id=${s.screenId})
                </option>
              </c:forEach>
            </select>
          </div>

          <div class="row">
            <label>開始時間</label>
            <input type="datetime-local" name="startTime" required>
          </div>

          <div class="row">
            <label>繰り返し</label>
            <input type="number" name="repeatDays" min="1" max="31" value="1" style="width:80px;">
            <span class="muted">日分</span>

            <span style="display:inline-block; width:12px;"></span>

            <span class="muted">間隔</span>
            <input type="number" name="repeatEvery" min="1" max="7" value="1" style="width:70px;">
            <span class="muted">日</span>
          </div>

          <div class="row" style="margin-top:10px;">予定終了時間 <b id="previewEnd">—</b>
          <input type="hidden" name="endTime" id="endTimePreview"></div>

          <div class="row">
            <label>公開</label>
            <select name="isActive">
              <option value="true" selected>TRUE</option>
              <option value="false">FALSE</option>
            </select>
          </div>

          <button class="btn" type="submit" style="margin-top:10px;">追加</button>
        </form>

        <hr style="margin:14px 0;">

        <div>
          <a class="btn" href="adminImportSchedule" style="text-decoration:none;">映画追加（TMDb検索）へ</a>
        </div>
      </div>
    </div>

    <!-- RIGHT: list -->
    <div class="right">
      <div class="card">
        <div style="display:flex; justify-content:space-between; align-items:center; gap:10px;">
          <h3 style="margin:0;">スケジュール一覧</h3>

          <!-- showPast toggle -->
          <div>
            <c:choose>
              <c:when test="${param.range == 'past'}">
                <span class="badge">表示: 過去</span>
                <a class="btn btn-s" href="adminSchedules" style="text-decoration:none; margin-left:6px;">今日以降</a>
              </c:when>
              <c:otherwise>
                <span class="badge">表示: 今日以降</span>
                <a class="btn btn-s" href="adminSchedules?range=past" style="text-decoration:none; margin-left:6px;">過去</a>
              </c:otherwise>
            </c:choose>
          </div>
        </div>

        <c:if test="${empty result.data}">
          <p class="muted" style="margin-top:10px;">スケジュールはまだありません。</p>
        </c:if>

        <c:if test="${not empty result.data}">
          <div style="margin-top:10px; overflow:auto; max-height: 70vh;">
            <table>
              <thead>
                <tr>
                  <th class="col-time">開始 / 終了</th>
                  <th class="col-movie">映画</th>
                  <th class="col-theater">劇場 / スクリーン</th>
                  <th class="col-status">状態</th>
                  <th class="col-actions">操作</th>
                </tr>
              </thead>
              <tbody>
                <c:forEach var="s" items="${result.data}">
                  <tr>
                    <td class="col-time">
                      <div>${fn:substring(s.startTime, 0, 16)}</div>
                      <div class="muted">${fn:substring(s.endTime, 0, 16)}</div>
                    </td>
                    <td class="col-movie">
                      <div><b>${s.movieName}</b></div>
                      <div class="muted">movieId=${s.movieId} / scheduleId=${s.scheduleId}</div>
                    </td>
                    <td class="col-theater">
                      <div>${s.cinemaName}</div>
                      <div class="muted">${s.screenName} (screenId=${s.screenId})</div>
                    </td>
                    <td class="col-status">
                      <c:choose>
                        <c:when test="${s.active}">
                          <span class="badge">ACTIVE</span>
                        </c:when>
                        <c:otherwise>
                          <span class="badge">STOP</span>
                        </c:otherwise>
                      </c:choose>
                    </td>
                    <td class="col-actions">
                      <div class="actions">
                        <!-- toggle -->
                        <form action="adminSaveSchedule" method="post">
                          <input type="hidden" name="action" value="delete">
                          <input type="hidden" name="scheduleId" value="${s.scheduleId}">
                          <button class="btn btn-s" type="submit">${s.active ? '停止' : '再開'}</button>
                        </form>

                        <!-- details for edit (progressive disclosure) -->
                        <details>
                          <summary class="btn btn-s" style="display:inline-block; margin-top:6px;">編集</summary>

                          <form action="adminSaveSchedule" method="post" style="margin-top:8px;">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="scheduleId" value="${s.scheduleId}">

                            <div class="row">
                              movieId:
                              <input type="text" name="movieId" value="${s.movieId}" required>
                            </div>

                            <div class="row">
                              screenId:
                              <input type="text" name="screenId" value="${s.screenId}" required>
                            </div>

                            <div class="row">
                              startTime:
                              <input type="datetime-local" name="startTime"
                                value="${fn:replace(fn:substring(s.startTime, 0, 16), ' ', 'T')}" required>
                            </div>

                            <div class="row">
                              endTime:
                              <input type="datetime-local" name="endTime"
                                value="${fn:replace(fn:substring(s.endTime, 0, 16), ' ', 'T')}" required>
                            </div>

                            <button class="btn" type="submit">更新</button>
                          </form>
                        </details>
                      </div>
                    </td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
          </div>
        </c:if>

        <p style="margin-top:10px;">
          <a href="adminSchedules">一覧を再読み込み</a>
        </p>
      </div>
    </div>
  </div>
</body>
</html>