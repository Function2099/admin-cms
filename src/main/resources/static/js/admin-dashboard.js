// ===== 全域圖表變數 =====
let adminTrafficChart = null;
let adminSalesChart = null;

// ===== 初始化 =====
function initAdminDashboard() {
    console.log("Admin Dashboard 初始化...");

    loadAdminDashboardAnalytics();

    // 綁定搜尋按鈕
    document.getElementById("adminFilterBtn").addEventListener("click", () => {
        loadAdminDashboardAnalytics();
    });
}

// ===== 載入分析 API =====
async function loadAdminDashboardAnalytics() {
    const start = document.getElementById("adminStartDate").value;
    const end = document.getElementById("adminEndDate").value;

    let url = `/api/admin/dashboard-analytics`;

    const params = [];
    if (start) params.push(`startDate=${start}`);
    if (end) params.push(`endDate=${end}`);

    if (params.length > 0) {
        url += "?" + params.join("&");
    }

    console.log("呼叫後端:", url);

    const resp = await fetch(url);
    const data = await resp.json();

    console.log("後端回傳資料:", data);

    // ===== 更新 KPI =====
    document.getElementById("kpiHomepageViews").textContent = data.homepageViews;

    // ===== 畫圖 =====
    renderAdminTrafficChart(data.analytics.lineCharts.traffic);
    renderAdminSalesChart(data.analytics.lineCharts.sales);
}

// ======== 總流量折線圖 ========
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
                backgroundColor: "rgba(0, 119, 255, 0.2)",
                tension: 0.3
            }]
        }
    });
}

// ======== 銷售額折線圖 ========
function renderAdminSalesChart(salesData) {
    const ctx = document.getElementById("adminSalesChart");

    if (adminSalesChart) adminSalesChart.destroy();

    adminSalesChart = new Chart(ctx, {
        type: "line",
        data: {
            labels: salesData.labels,
            datasets: [{
                label: "每日總銷售額",
                data: salesData.data,
                borderWidth: 2,
                borderColor: "#ff6600",
                backgroundColor: "rgba(255, 102, 0, 0.2)",
                tension: 0.3
            }]
        }
    });
}
