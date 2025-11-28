const PageInitializers = {
    "/organizer/dashboard": () => {
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
    "/organizer/dashboard/analytics/summary": () => {
        if (typeof initSummaryAnalytics === "function") initSummaryAnalytics();
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