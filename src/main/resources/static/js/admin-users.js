function initAdminUsers() {
    // 綁定查詢按鈕
    const btn = document.getElementById("searchBtn");
    if (btn) {
        btn.onclick = () => loadUsers(0);
    }
    // 第一次載入
    loadUsers(0);
}

async function loadUsers(page) {

    const keyword = document.getElementById("keyword")?.value.trim() || "";
    const size = 10;

    let params = [`page=${page}`, `size=${size}`];
    if (keyword) params.push(`keyword=${encodeURIComponent(keyword)}`);

    const url = `/api/admin/users?${params.join("&")}`;

    try {
        const resp = await fetch(url);
        if (!resp.ok) {
            console.error("API 錯誤:", resp.status);
            return;
        }

        const data = await resp.json();
        // console.log("後端回傳 Page:", data);

        renderUsers(data.content);
        renderUserPagination(data);
    } catch (err) {
        console.error("載入失敗:", err);
    }
}

function renderUsers(list) {
    const tbody = document.querySelector("#userTable tbody");
    tbody.innerHTML = "";

    if (!list || list.length === 0) {
        tbody.innerHTML = `<tr><td colspan="6" style="text-align:center;">無資料</td></tr>`;
        return;
    }

    list.forEach(u => {
        const tr = document.createElement("tr");

        const toggleBtnClass = u.isActive
            ? "events-btn events-btn-danger"
            : "events-btn events-btn-secondary";

        const toggleBtnText = u.isActive ? "封鎖" : "啟用";
        const btnStyle = "padding: 5px 10px; font-size: 14px;";

        const getRoleBtnAttr = (targetRole) => {
            if (u.role === targetRole) {
                // 如果是當前角色：加上 disabled，並且給一個深一點的顏色或透明度表示已選中
                return `disabled style="${btnStyle} opacity: 0.5; cursor: not-allowed; background-color: #cfcfcfff; color: #fff;"`;
            }
            // 如果不是：正常樣式
            return `style="${btnStyle}" onclick="setRole(${u.id}, ${targetRole})"`;
        };

        tr.innerHTML = `
            <td>${u.id}</td>
            <td>${u.account}</td>
            <td>${u.username}</td>
            <td>${roleLabel(u.role)}</td>
            <td>
                <span style="color: ${u.isActive ? 'green' : 'red'}; font-weight: bold;">
                    ${u.isActive ? "啟用" : "封鎖"}
                </span>
            </td>
            <td>
                <div style="display: flex; gap: 5px; justify-content: center;">
                    <button class="events-btn events-btn-secondary" ${getRoleBtnAttr(0)}>管理者</button>
                    <button class="events-btn events-btn-secondary" ${getRoleBtnAttr(1)}>主辦方</button>
                    <button class="events-btn events-btn-secondary" ${getRoleBtnAttr(2)}>使用者</button>
                    
                    <div style="width: 1px; background: #ddd; margin: 0 5px;"></div>

                    <button class="${toggleBtnClass}" style="${btnStyle}" onclick="toggleActive(${u.id}, ${u.isActive})">
                        ${toggleBtnText}
                    </button>
                </div>
            </td>
        `;

        tbody.appendChild(tr);
    });
}

function roleLabel(r) {
    if (r === 0) return "管理者";
    if (r === 1) return "主辦方";
    return "使用者";
}

async function setRole(id, role) {
    if (!confirm("確定要變更此使用者的權限嗎？")) return;
    await fetch(`/api/admin/users/${id}/role?role=${role}`, {
        method: "PUT"
    });
    loadUsers(0);
}

async function toggleActive(id, isActive) {
    const action = isActive ? "封鎖" : "啟用";
    if (!confirm(`確定要${action}此帳號嗎？`)) return;

    const newState = isActive ? 0 : 1;
    await fetch(`/api/admin/users/${id}/active?active=${newState}`, {
        method: "PUT"
    });
    loadUsers(0);
}

function renderUserPagination(data) {
    const container = document.getElementById("UserPagination");
    container.innerHTML = "";

    const total = data.totalPages;
    const current = data.number;

    for (let i = 0; i < total; i++) {
        const btn = document.createElement("button");
        btn.innerText = i + 1;

        btn.className = `events-page-btn ${i === current ? 'active' : ''}`;

        if (i === current) {
            btn.disabled = true;
        } else {
            btn.onclick = () => loadUsers(i);
        }

        container.appendChild(btn);
    }
}