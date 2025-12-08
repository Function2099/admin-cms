const PageInitializers = {
    "/organizer/dashboard": () => {
        if (typeof loadKpi === "function") loadKpi();
        if (typeof initActivityCards === "function") initActivityCards();
    },
    "/organizer/dashboard/announcement": () => {
        if (typeof initAnnouncement === "function") initAnnouncement();
    },
    "/organizer/dashboard/event": () => {
        if (typeof initEvent === "function") initEvent();
    },
    "/organizer/dashboard/event/ticket": () => {
        if (typeof initEvent === "function") initEvent();
    },
    "/organizer/dashboard/analytics/traffic": () => {
        if (typeof initTrafficAnalytics === "function") initTrafficAnalytics();
    },
    "/organizer/dashboard/analytics/consumer": () => {
        if (typeof initConsumerAnalytics === "function") initConsumerAnalytics();
    },
    "/organizer/dashboard/orders": () => {
        if (typeof initOrders === "function") initOrders();
    }
};

function pageInitializer() {
    console.log("Current path:", location.pathname);
    const path = location.pathname;
    const init = PageInitializers[path];
    if (typeof init === "function") {
        init();
    }
}