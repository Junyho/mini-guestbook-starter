// API 주소는 고정
const API = "/guestbook";

// ===== API 라이브 로그 =====
// 호출할 때마다 어떤 요청이 오갔는지 콘솔 패널 맨 위에 한 줄씩 기록
function logApi(method, path, status, note) {
  const log = document.getElementById("log");
  const placeholder = log.querySelector(".log-empty");
  if (placeholder) placeholder.remove();

  const row = document.createElement("div");
  row.className = "log-row " + method.toLowerCase();

  const m = document.createElement("span");
  m.className = "log-method";
  m.textContent = method;

  const p = document.createElement("span");
  p.className = "log-path";
  p.textContent = path;

  const s = document.createElement("span");
  s.className = "log-status";
  s.textContent = status;

  row.appendChild(m);
  row.appendChild(p);
  row.appendChild(s);

  if (note) {
    const n = document.createElement("div");
    n.className = "log-note";
    n.textContent = note;
    row.appendChild(n);
  }

  log.prepend(row);
}

function clearLog() {
  const log = document.getElementById("log");
  log.innerHTML = '<div class="log-empty">아직 호출이 없어요. 등록·조회·삭제를 해보세요!</div>';
}

// 이름 → 색상 해시: 같은 이름은 항상 같은 색 아바타
function colorFor(name) {
  let hash = 0;
  for (let i = 0; i < name.length; i++) {
    hash = name.charCodeAt(i) + ((hash << 5) - hash);
  }
  return "hsl(" + (Math.abs(hash) % 360) + ", 65%, 60%)";
}

function initial(name) {
  return name ? name.trim().charAt(0).toUpperCase() : "?";
}

// ISO 문자열(예: 2026-06-26T12:34:56)을 "2026.06.26 12:34" 형식으로
function formatDate(iso) {
  if (!iso) return "";
  const d = new Date(iso);
  if (isNaN(d.getTime())) return iso;
  const pad = function (n) { return String(n).padStart(2, "0"); };
  return d.getFullYear() + "." + pad(d.getMonth() + 1) + "." + pad(d.getDate())
    + " " + pad(d.getHours()) + ":" + pad(d.getMinutes());
}

// 수정된 적이 있는지 (생성일 ≠ 수정일)
function isEdited(e) {
  return e.modifiedAt && e.modifiedAt !== e.createdAt;
}

// ===== 전체 목록 (갤러리) =====
async function loadEntries() {
  const res = await fetch(API);
  const entries = await res.json();
  logApi("GET", "/guestbook", res.status, entries.length + "건 불러옴");

  const list = document.getElementById("list");
  list.innerHTML = "";

  if (entries.length === 0) {
    const empty = document.createElement("div");
    empty.className = "empty";
    empty.textContent = "아직 방명록이 없어요. 첫 글을 남겨보세요! ✍️";
    list.appendChild(empty);
    return;
  }

  entries.forEach(function (e, i) {
    list.appendChild(makeCard(e, i));
  });
}

// 방명록 한 건을 카드로
function makeCard(e, i) {
  const card = document.createElement("div");
  card.className = "card";
  card.style.animationDelay = (i * 40) + "ms";

  const avatar = document.createElement("div");
  avatar.className = "avatar";
  avatar.style.background = colorFor(e.name || "?");
  avatar.textContent = initial(e.name);

  const idBadge = document.createElement("span");
  idBadge.className = "card-id";
  idBadge.textContent = "#" + e.id;

  const who = document.createElement("div");
  who.className = "card-name";
  who.textContent = e.name; // textContent 로 안전하게 출력

  const msg = document.createElement("div");
  msg.className = "card-msg";
  msg.textContent = e.message;

  const date = document.createElement("div");
  date.className = "card-date";
  date.textContent = "🕒 " + formatDate(e.createdAt) + (isEdited(e) ? " · 수정됨" : "");

  const actions = document.createElement("div");
  actions.className = "card-actions";

  const view = document.createElement("button");
  view.className = "btn-view";
  view.textContent = "🔎 보기";
  view.onclick = function () { viewEntry(e.id); };

  const edit = document.createElement("button");
  edit.className = "btn-edit";
  edit.textContent = "✏️ 수정";
  edit.onclick = function () { startEdit(e, card); };

  const del = document.createElement("button");
  del.className = "btn-del";
  del.textContent = "🗑 삭제";
  del.onclick = function () { deleteEntry(e.id, card); };

  actions.appendChild(view);
  actions.appendChild(edit);
  actions.appendChild(del);

  card.appendChild(avatar);
  card.appendChild(idBadge);
  card.appendChild(who);
  card.appendChild(msg);
  card.appendChild(date);
  card.appendChild(actions);
  return card;
}

// ===== 등록 (POST /guestbook) =====
async function addEntry() {
  const nameEl = document.getElementById("name");
  const msgEl = document.getElementById("message");
  const name = nameEl.value.trim();
  const message = msgEl.value.trim();
  if (!name || !message) {
    shakeForm();
    return;
  }

  const btn = document.getElementById("submit");
  btn.disabled = true;
  btn.textContent = "등록 중…";

  const res = await fetch(API, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name: name, message: message })
  });
  const saved = await res.json();
  logApi("POST", "/guestbook", res.status, "저장됨 → #" + saved.id + " " + saved.name);

  nameEl.value = "";
  msgEl.value = "";
  btn.disabled = false;
  btn.textContent = "등록";
  await loadEntries();
}

// 빈 칸으로 등록하면 폼을 살짝 흔들
function shakeForm() {
  const form = document.getElementById("form");
  form.classList.remove("shake");
  void form.offsetWidth; // 애니메이션 재시작을 위한 reflow
  form.classList.add("shake");
}

// ===== 삭제 (DELETE /guestbook/{id}) =====
async function deleteEntry(id, card) {
  if (card) {
    card.classList.add("removing");
    await new Promise(function (r) { setTimeout(r, 220); });
  }
  const res = await fetch(API + "/" + id, { method: "DELETE" });
  logApi("DELETE", "/guestbook/" + id, res.status, "삭제됨");
  loadEntries();
}

// ===== 수정 (PUT /guestbook/{id}) =====
// "수정"을 누르면 카드가 입력 폼
function startEdit(e, card) {
  const edit = document.createElement("div");
  edit.className = "card editing";

  const nameIn = document.createElement("input");
  nameIn.className = "edit-input";
  nameIn.value = e.name;
  nameIn.maxLength = 20;

  const msgIn = document.createElement("input");
  msgIn.className = "edit-input";
  msgIn.value = e.message;

  const actions = document.createElement("div");
  actions.className = "card-actions";

  const save = document.createElement("button");
  save.className = "btn-save";
  save.textContent = "💾 저장";
  save.onclick = function () { saveEdit(e.id, nameIn.value, msgIn.value); };

  const cancel = document.createElement("button");
  cancel.className = "btn-cancel";
  cancel.textContent = "취소";
  cancel.onclick = function () { loadEntries(); };

  actions.appendChild(save);
  actions.appendChild(cancel);

  edit.appendChild(nameIn);
  edit.appendChild(msgIn);
  edit.appendChild(actions);

  card.replaceWith(edit);
  nameIn.focus();
}

async function saveEdit(id, name, message) {
  name = name.trim();
  message = message.trim();
  if (!name || !message) {
    alert("이름과 메시지를 모두 입력하세요!");
    return;
  }
  const res = await fetch(API + "/" + id, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name: name, message: message })
  });
  const saved = await res.json();
  logApi("PUT", "/guestbook/" + id, res.status, "수정됨 → " + saved.name);
  loadEntries();
}

// ===== 단건 조회 (GET /guestbook/{id}) → 모달 =====
async function viewEntry(id) {
  const res = await fetch(API + "/" + id);
  logApi("GET", "/guestbook/" + id, res.status, res.ok ? "단건 조회" : "없는 id");

  const modal = document.getElementById("modal");
  const body = document.getElementById("modalBody");
  body.innerHTML = "";

  // 없는 id면 백엔드가 404를
  if (!res.ok) {
    const err = document.createElement("p");
    err.className = "modal-error";
    err.textContent = "해당 방명록을 찾을 수 없어요 (id: " + id + ")";
    body.appendChild(err);
    modal.classList.add("open");
    return;
  }

  const e = await res.json();

  const avatar = document.createElement("div");
  avatar.className = "avatar avatar-lg";
  avatar.style.background = colorFor(e.name || "?");
  avatar.textContent = initial(e.name);

  const who = document.createElement("h2");
  who.className = "modal-name";
  who.textContent = e.name;

  const msg = document.createElement("p");
  msg.className = "modal-msg";
  msg.textContent = e.message;

  const dates = document.createElement("p");
  dates.className = "modal-dates";
  let dateText = "작성 " + formatDate(e.createdAt);
  if (isEdited(e)) {
    dateText += "  ·  수정 " + formatDate(e.modifiedAt);
  }
  dates.textContent = dateText;

  const meta = document.createElement("p");
  meta.className = "modal-meta";
  meta.textContent = "GET /guestbook/" + e.id;

  body.appendChild(avatar);
  body.appendChild(who);
  body.appendChild(msg);
  body.appendChild(dates);
  body.appendChild(meta);
  modal.classList.add("open");
}

function closeModal() {
  document.getElementById("modal").classList.remove("open");
}

// Enter 키로도 등록할 수 있게
["name", "message"].forEach(function (id) {
  document.getElementById(id).addEventListener("keydown", function (ev) {
    if (ev.key === "Enter") addEntry();
  });
});

// 페이지 시작
clearLog();
loadEntries();
