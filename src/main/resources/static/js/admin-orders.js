function initAdminOrders() {
    // 綁定查詢按鈕
    const btn = document.getElementById("searchBtn");
    if (btn) {
        btn.onclick = () => loadOrders(0);
    }

    // 綁定表格內的「詳情」按鈕點擊 (使用事件代理)
    const tableBody = document.querySelector("#orderTable tbody");
    if (tableBody) {
        tableBody.addEventListener("click", (e) => {
            if (e.target.classList.contains("order-detail-btn")) {
                const orderId = e.target.dataset.id;
                showOrderDetail(orderId);
            }
        });
    }

    // 綁定 Modal 關閉事件
    const modal = document.getElementById("orderDetailModal");
    const closeBtn = document.getElementById("orderDetailClose");

    if (modal && closeBtn) {
        // 點擊關閉按鈕
        closeBtn.addEventListener("click", () => {
            modal.style.display = "none";
        });

        // 點擊遮罩背景也能關閉
        modal.addEventListener("click", (e) => {
            if (e.target === modal) {
                modal.style.display = "none";
            }
        });
    }

    // 首次載入第 0 頁
    loadOrders(0);
}

async function loadOrders(page) {

    const keyword = document.getElementById("keyword")?.value.trim() || "";
    const startDate = document.getElementById("startDate")?.value || "";
    const endDate = document.getElementById("endDate")?.value || "";
    const size = 10; // 每頁 10 筆

    let params = [`page=${page}`, `size=${size}`];

    if (keyword) params.push(`keyword=${keyword}`);
    if (startDate) params.push(`startDate=${startDate}`);
    if (endDate) params.push(`endDate=${endDate}`);

    const url = `/api/admin/orders?${params.join("&")}`;
    console.log("呼叫 API:", url);

    try {
        const resp = await fetch(url);
        if (!resp.ok) {
            console.error("API 錯誤:", resp.status);
            return;
        }

        const data = await resp.json(); // Page 格式
        console.log("後端回傳 Page:", data);

        renderOrders(data.content);
        renderOrderPagination(data);

    } catch (err) {
        console.error("無法取得訂單資料:", err);
    }
}

// 渲染表格
function renderOrders(list) {
    const tbody = document.querySelector("#orderTable tbody");
    tbody.innerHTML = "";

    if (!list || list.length === 0) {
        tbody.innerHTML = `<tr><td colspan="8" style="text-align:center;">無訂單資料</td></tr>`;
        return;
    }

    list.forEach(o => {
        const tr = document.createElement("tr");

        tr.innerHTML = `
            <td>${o.orderId}</td>
            <td>${formatDate(o.createdAt)}</td>
            <td>${o.buyerName}</td>
            <td>${o.buyerAccount}</td>
            <td>${o.eventTitle}</td>
            <td>${o.totalQuantity}</td>
            <td>${o.status}</td>

            <td style="text-align: center;">
                <button class="events-btn events-btn-secondary order-detail-btn" data-id="${o.orderId}">
                    詳情
                </button>
            </td>
        `;

        tbody.appendChild(tr);
    });
}

async function showOrderDetail(orderId) {
    const modal = document.getElementById("orderDetailModal");
    const tbody = document.getElementById("orderDetailTbody");
    const totalEl = document.getElementById("orderDetailTotal");

    if (!modal) return;

    try {
        // 假設後端有一個通用的 API 或者 Admin 專用的 API
        // 如果 Admin 權限夠大，通常可以直接呼叫這個
        const resp = await fetch(`/api/orders/detail/${orderId}`);

        if (!resp.ok) throw new Error("無法讀取明細");

        const list = await resp.json();

        let html = "";
        let total = 0;

        list.forEach(i => {
            const subtotal = i.quantity * i.unitPrice;
            total += subtotal;
            html += `
                <tr>
                    <td style="text-align: left;">${i.ticketName}</td>
                    <td>${i.quantity}</td>
                    <td>$${i.unitPrice}</td>
                    <td style="font-weight: bold;">$${subtotal}</td>
                </tr>
            `;
        });

        tbody.innerHTML = html;
        totalEl.innerText = `$${total}`; // 加上錢字號

        modal.style.display = "flex"; // 顯示視窗

    } catch (err) {
        console.error("明細載入失敗:", err);
        alert("無法載入訂單明細");
    }
}

// 分頁按鈕
function renderOrderPagination(pageData) {
    const container = document.getElementById("ordersPagination");
    container.innerHTML = "";

    const current = pageData.number;
    const total = pageData.totalPages;

    const createBtn = (text, onClick, isActive = false) => {
        const btn = document.createElement("button");
        btn.textContent = text;
        btn.className = `events-page-btn ${isActive ? 'active' : ''}`;
        btn.onclick = onClick;
        return btn;
    };

    // 上一頁
    if (current > 0) {
        container.appendChild(createBtn("上一頁", () => loadOrders(current - 1)));
    }

    // 頁碼按鈕
    for (let i = 0; i < total; i++) {
        const btn = createBtn(i + 1, () => loadOrders(i), i === current);
        if (i === current) btn.onclick = null; // 當前頁不可點
        container.appendChild(btn);
    }

    // 下一頁
    if (current < total - 1) {
        container.appendChild(createBtn("下一頁", () => loadOrders(current + 1)));
    }
}

// 格式化日期
function formatDate(dateTimeStr) {
    if (!dateTimeStr) return "-";
    const date = new Date(dateTimeStr);
    return date.toLocaleString("zh-TW", { hour12: false });
}