document.addEventListener("DOMContentLoaded", () => {

    let selectedEvents = [];
    let allOrderEvents = [];
    let visibleCount = 10;

    // 保存活動選項
    function saveSelectedEvents() {
        sessionStorage.setItem("selectedEvents", JSON.stringify(selectedEvents));
    }

    function loadSelectedEvents() {
        const saved = sessionStorage.getItem("selectedEvents");
        if (saved) {
            selectedEvents = JSON.parse(saved);
        }
    }

    /* 初始化 */
    loadSelectedEvents();
    renderSelectedEvents();

    /* 打開活動選單 */
    document.addEventListener("click", (e) => {
        if (e.target.closest("#openEventModalBtn")) {
            document.getElementById("eventModal").style.display = "flex";
            loadOrderEventList();
        }
    });

    /* 關閉 modal */
    document.addEventListener("click", (e) => {
        if (e.target.closest("#eventModalClose")) {
            document.getElementById("eventModal").style.display = "none";
        }
    });

    /* 搜尋活動 */
    document.addEventListener("input", (e) => {
        if (e.target.id === "eventSearchInput") {
            loadOrderEventList(e.target.value.trim());
        }
    });

    function loadOrderEventList(keyword = "") {
        fetch(`/api/events/my?keyword=${keyword}`)
            .then(resp => resp.json())
            .then(list => {
                allOrderEvents = list;
                visibleCount = 10;
                renderOrderEventList();
            });
    }

    function renderOrderEventList() {

        const listEl = document.getElementById("eventList");
        const moreEl = document.getElementById("eventShowMore");
        const slice = allOrderEvents.slice(0, visibleCount);

        let html = "";
        slice.forEach(ev => {
            html += `
                <label class="event-item">
                    <input type="checkbox"
                            class="event-checkbox"
                            data-id="${ev.id}"
                            data-title="${ev.title}">
                    <span>${ev.title}</span>
                </label>
            `;
        });

        listEl.innerHTML = html;

        moreEl.style.display = visibleCount < allOrderEvents.length
            ? "block"
            : "none";
    }

    /* 套用活動選取 */
    document.addEventListener("click", (e) => {
        if (e.target.closest("#eventModalApply")) {

            const checks = document.querySelectorAll(".event-checkbox:checked");
            selectedEvents = [];

            checks.forEach(chk => {
                selectedEvents.push({
                    id: Number(chk.dataset.id),
                    title: chk.dataset.title
                });
            });
            saveSelectedEvents();

            document.getElementById("eventModal").style.display = "none";
            renderSelectedEvents();
        }
    });

    /* 顯示更多 */
    document.addEventListener("click", (e) => {
        if (e.target.closest("#eventShowMore")) {
            visibleCount += 10;
            renderOrderEventList();
        }
    });

    /* 查詢訂單 */
    document.addEventListener("click", (e) => {
        if (e.target.closest("#orderSearchBtn")) {
            loadOrderList();
        }
    });

    function loadOrderList() {

        if (selectedEvents.length === 0) return;

        let keyword = document.getElementById("orderSearchInput").value.trim();

        let params = new URLSearchParams();

        selectedEvents.forEach(ev => params.append("eventIds", ev.id));

        if (keyword) params.append("keyword", keyword);

        fetch(`/api/orders?` + params.toString())
            .then(resp => resp.json())
            .then(list => {
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
                        <td><button onclick="showOrderDetail(${o.orderId})">...</button></td>
                    </tr>
                `;
                });
                document.getElementById("orderTableBody").innerHTML = html;
            });
    }



    function showOrderDetail(orderId) {
        alert("TODO: 未來做詳細訂單內容 orderId=" + orderId);
    }

    /* 顯示已選活動 */
    function renderSelectedEvents() {
        const box = document.getElementById("selectedEventBox");

        if (selectedEvents.length === 0) {
            box.innerHTML = `
            <button id="openEventModalBtn" class="select-event-btn">選擇活動</button>
            <div class="placeholder">(尚未選擇活動)</div>
        `;
            return;
        }

        let html = "<div class='event-selected-list'>";

        selectedEvents.forEach(ev => {
            html += `
            <span class="event-selected-item" data-id="${ev.id}">
                ${ev.title}
            </span>
        `;
        });

        html += "</div>";
        box.innerHTML = html;

        loadOrderList();
    }


    /* 切換活動（事件委派版本） */
    document.addEventListener("click", (e) => {
        const item = e.target.closest(".event-selected-item");

        if (item) {
            const id = Number(item.dataset.id);
            selectedEvents = selectedEvents.filter(ev => ev.id === id);
            saveSelectedEvents();
            renderSelectedEvents();
        }
    });

});
