// ===== 全域圖表變數 =====
let adminTrafficChart = null;
let adminTxChart = null;

// ===== 初始化 =====
function initAdminDashboard() {
    if (window.adminDashboardInitialized) return;
    window.adminDashboardInitialized = true;

    console.log("Admin Dashboard 初始化");

    const filterBtn = document.getElementById("adminFilterBtn");
    filterBtn.addEventListener("click", loadAdminAnalytics);

    requestAnimationFrame(() => {
        loadAdminAnalytics();
        initAdminLoginLogs();
    });
}


// ===== 載入分析 API =====
async function loadAdminAnalytics() {
    const start = document.getElementById("adminStartDate").value;
    const end = document.getElementById("adminEndDate").value;

    let url = `/api/admin/dashboard-analytics?`;
    if (start) url += `startDate=${start}&`;
    if (end) url += `endDate=${end}`;

    console.log("呼叫後端:", url);

    const resp = await fetch(url);
    const data = await resp.json();

    console.log("後端回傳:", data);

    // 後端 DTO：AdminAnalyticsDTO
    const views = data.homepageViews ?? 0;
    const traffic = data.traffic ?? null;
    const transactions = data.transactions ?? null;
    const successRate = data.successRate ?? 0;

    // ===== 更新 KPI =====
    document.getElementById("kpiHomepageViews").textContent = views;

    // 成功率（顯示成百分比）
    if (document.getElementById("kpiSuccessRate")) {
        document.getElementById("kpiSuccessRate").textContent =
            (successRate * 100).toFixed(1) + "%";
    }

    // ===== 折線圖 =====
    if (traffic) renderAdminTrafficChart(traffic);
    if (transactions) renderAdminTxChart(transactions);
}

// ======== 日流量折線圖 ========
function renderAdminTrafficChart(trafficData) {
    const ctx = document.getElementById("adminTrafficChart");

    if (adminTrafficChart) adminTrafficChart.destroy();

    adminTrafficChart = new Chart(ctx, {
        type: "line",
        data: {
            labels: trafficData.labels,
            datasets: [{
                label: "每日總流量",
                data: trafficData.data,
                borderWidth: 2,
                borderColor: "#0077ff",
                backgroundColor: "rgba(0, 119, 255, 0.25)",
                tension: 0.3
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false
        }
    });
}

// ======== 銷售額折線圖 ========
function renderAdminTxChart(txData) {
    const ctx = document.getElementById("adminTxChart");

    if (adminTxChart) adminTxChart.destroy();

    adminTxChart = new Chart(ctx, {
        type: "line",
        data: {
            labels: txData.labels,
            datasets: [{
                label: "每日成功交易筆數",
                data: txData.data,
                borderWidth: 2,
                borderColor: "#ff6600",
                backgroundColor: "rgba(255, 102, 0, 0.25)",
                tension: 0.3
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false
        }
    });
}


// ====== 登入紀錄參數 ======
let loginLogPage = 0;
let loginLogSize = 10;
let loginLogKeyword = "";

// ====== 初始化登入紀錄 ======
function initAdminLoginLogs() {
    if (window.loginLogInitialized) return;
    window.loginLogInitialized = true;
    document.getElementById("loginLogSearchBtn").addEventListener("click", () => {
        loginLogKeyword = document.getElementById("loginLogKeyword").value.trim();
        loginLogPage = 0;
        loadLoginLogs();
    });

    document.getElementById("loginLogPrev").addEventListener("click", () => {
        if (loginLogPage > 0) {
            loginLogPage--;
            loadLoginLogs();
        }
    });

    document.getElementById("loginLogNext").addEventListener("click", () => {
        loginLogPage++;
        loadLoginLogs();
    });

    loadLoginLogs();
}

// ====== 呼叫 API ======
async function loadLoginLogs() {
    const url = `/api/admin/login-logs?keyword=${loginLogKeyword}&page=${loginLogPage}&size=${loginLogSize}`;

    const resp = await fetch(url);
    const page = await resp.json();

    console.log("登入紀錄:", page);

    renderLoginLogTable(page.content);

    // 更新分頁資訊
    document.getElementById("loginLogPageInfo").textContent =
        `${page.number + 1} / ${page.totalPages}`;

    // 下一頁限制
    document.getElementById("loginLogNext").disabled = page.last;
    document.getElementById("loginLogPrev").disabled = page.first;
}

// ====== 渲染表格 ======
function renderLoginLogTable(list) {
    const tbody = document.getElementById("loginLogTableBody");
    tbody.innerHTML = "";

    list.forEach(log => {
        const tr = document.createElement("tr");

        tr.innerHTML = `
            <td>${log.user?.account || "-"}</td>
            <td>${formatDateTime(log.createdAt)}</td>
            <td>${log.ipAddress}</td>
            <td>${parseUserAgent(log.userAgent)}</td>
            <td>${log.status}</td>
        `;

        tbody.appendChild(tr);
    });
}

// ====== 轉換日期格式 ======
function formatDateTime(str) {
    if (!str) return "-";
    const d = new Date(str);
    return `${d.getFullYear()}/${d.getMonth() + 1}/${d.getDate()} ${d.getHours()}:${String(d.getMinutes()).padStart(2, '0')}`;
}

// ====== 解析 User-Agent → 裝置資訊 ======
function parseUserAgent(ua) {
    if (!ua) return "-";

    let os = "Unknown OS";
    if (ua.includes("Windows")) os = "Windows";
    else if (ua.includes("Mac OS")) os = "macOS";
    else if (ua.includes("iPhone")) os = "iPhone";
    else if (ua.includes("Android")) os = "Android";
    else if (ua.includes("Linux")) os = "Linux";

    let browser = "Unknown Browser";
    if (ua.includes("Chrome")) browser = "Chrome";
    else if (ua.includes("Safari") && !ua.includes("Chrome")) browser = "Safari";
    else if (ua.includes("Firefox")) browser = "Firefox";
    else if (ua.includes("Edg")) browser = "Edge";

    // 抓版本（可選）
    const match = ua.match(/(Chrome|Safari|Firefox|Edg)\/([\d\.]+)/);
    const version = match ? match[2] : "";

    return `${os} / ${browser} ${version}`;
}
