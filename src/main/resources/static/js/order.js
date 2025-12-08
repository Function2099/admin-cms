function initOrders() {

    console.log("initOrders() 執行");

    let selectedEvents = [];
    let allOrderEvents = [];
    let visibleCount = 10;

    // sessionStorage 還原
    function loadSavedState() {
        const saved = sessionStorage.getItem("selectedEvents");
        selectedEvents = saved ? JSON.parse(saved) : [];
    }

    function saveState() {
        sessionStorage.setItem("selectedEvents", JSON.stringify(selectedEvents));
    }

    // 讀取活動清單
    async function fetchEvents(keyword = "") {
        const resp = await fetch(`/api/events/my?keyword=${keyword}`);
        allOrderEvents = await resp.json();
        visibleCount = 10;

        renderEventList();
    }

    // 渲染活動清單 ( modal 裡）
    function renderEventList() {
        const listEl = document.getElementById("eventList");
        const moreEl = document.getElementById("eventShowMore");
        if (!listEl) return;

        const slice = allOrderEvents.slice(0, visibleCount);

        let html = "";
        slice.forEach(ev => {
            const checked = selectedEvents.some(se => se.id === ev.id) ? "checked" : "";
            html += `
                <label class="event-item">
                    <input type="checkbox" class="event-checkbox"
                            data-id="${ev.id}" data-title="${ev.title}" ${checked}>
                    <span>${ev.title}</span>
                </label>
            `;
        });

        listEl.innerHTML = html;

        moreEl.style.display = (visibleCount < allOrderEvents.length) ? "block" : "none";
    }

    // 主畫面顯示已選活動
    function renderSelectedEvents() {
        const box = document.getElementById("selectedEventBox");
        if (!box) return;

        if (selectedEvents.length === 0) {
            box.innerHTML = `
                <button id="openEventModalBtn" class="select-event-btn">選擇活動</button>
                <div class="placeholder">(尚未選擇活動)</div>
            `;
            const tbody = document.getElementById("orderTableBody");
            if (tbody) tbody.innerHTML = "";
            return;
        }

        let html = `<div class="event-selected-list">`;
        selectedEvents.forEach(ev => {
            html += `<span class="event-selected-item" data-id="${ev.id}">${ev.title}</span>`;
        });
        html += `</div>`;

        box.innerHTML = html;

        loadOrderList();
    }

    // 載入訂單
    async function loadOrderList() {
        if (selectedEvents.length === 0) return;

        const keyword = document.getElementById("orderSearchInput")?.value.trim() || "";
        let params = new URLSearchParams();

        selectedEvents.forEach(ev => params.append("eventIds", ev.id));
        if (keyword) params.append("keyword", keyword);

        const resp = await fetch(`/api/orders?` + params.toString());
        const list = await resp.json();

        const tbody = document.getElementById("orderTableBody");
        if (!tbody) return;

        let html = "";
        list.forEach(o => {
            html += `
                <tr>
                    <td>${o.orderId}</td>
                    <td>${o.createdAt}</td>
                    <td>${o.buyerName}</td>
                    <td>${o.eventTitle}</td>
                    <td>${o.ticketCount}</td>
                    <td>${o.status}</td>
                    <td><button onclick="showOrderDetail(${o.orderId})">詳</button></td>
                </tr>
            `;
        });

        tbody.innerHTML = html;
    }

    // 綁定事件
    function bindEvents() {

        // 開啟 modal
        document.getElementById("selectedEventBox")?.addEventListener("click", (e) => {
            if (e.target.id === "openEventModalBtn" ||
                e.target.classList.contains("event-selected-item")) {
                document.getElementById("eventModal").style.display = "flex";
                fetchEvents();
            }
        });

        // 關閉 modal
        document.getElementById("eventModalClose")?.addEventListener("click", () => {
            document.getElementById("eventModal").style.display = "none";
        });

        document.getElementById("eventModal")?.addEventListener("click", (e) => {
            if (e.target.id === "eventModal") {
                document.getElementById("eventModal").style.display = "none";
            }
        });


        // 搜尋活動
        document.getElementById("eventSearchInput")?.addEventListener("input", (e) => {
            fetchEvents(e.target.value.trim());
        });

        // 顯示更多
        document.getElementById("eventShowMore")?.addEventListener("click", () => {
            visibleCount += 10;
            renderEventList();
        });

        // checkbox 勾選
        document.getElementById("eventList")?.addEventListener("change", (e) => {
            if (e.target.classList.contains("event-checkbox")) {
                const id = Number(e.target.dataset.id);
                const title = e.target.dataset.title;

                if (e.target.checked) {
                    if (!selectedEvents.some(ev => ev.id === id)) {
                        selectedEvents.push({ id, title });
                    }
                } else {
                    selectedEvents = selectedEvents.filter(ev => ev.id !== id);
                }
                saveState();
            }
        });

        // 套用活動按鈕
        document.getElementById("eventModalApply")?.addEventListener("click", () => {
            document.getElementById("eventModal").style.display = "none";
            renderSelectedEvents();
            saveState();
        });

        // 清除全部
        document.getElementById("clearEventSelectionBtn")?.addEventListener("click", () => {
            selectedEvents = [];
            saveState();
            renderEventList();
            renderSelectedEvents();
        });

        // 查詢訂單
        document.getElementById("orderSearchBtn")?.addEventListener("click", loadOrderList);
    }

    // 初始化流程(避免分頁切回來後失效)
    loadSavedState();
    bindEvents();
    renderSelectedEvents();
}
