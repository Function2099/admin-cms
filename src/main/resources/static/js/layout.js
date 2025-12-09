function loadContent(event, link) {
    event.preventDefault();

    // active 樣式處理
    document.querySelectorAll(".sidebar li")
        .forEach((li) => li.classList.remove("active"));

    const li = link.closest("li");
    const isDropdownToggle = link.classList.contains("dropdown-toggle");

    if (!isDropdownToggle) {
        localStorage.setItem("activeLink", link.id);
    }

    if (li) li.classList.add("active");

    // 下拉選單邏輯（不載入頁面）
    if (isDropdownToggle) {
        const dropdown = li.querySelector(".dropdown-menu");
        if (dropdown) dropdown.classList.add("show");
        return;
    }

    // AJAX 載入 fragment
    const url = link.getAttribute("href");         // /organizer/xxx-frag OR /admin/xxx-frag
    const logicalPath = link.dataset.path || "";   // data-path="orders" / "admin/users"

    let newUrl;

    // 判斷是否為 Admin 分頁：href 以 /admin 開頭
    if (url.startsWith("/admin")) {
        newUrl = "/admin" + (logicalPath ? "/" + logicalPath : "");

    } else {
        // Organizer 分頁
        newUrl = "/organizer/dashboard" + (logicalPath ? "/" + logicalPath : "");
    }

    history.pushState(null, "", newUrl);

    // Fetch fragment
    fetch(url)
        .then((response) => {
            if (!response.ok) throw new Error("載入失敗: " + url);
            return response.text();
        })
        .then((html) => {
            document.getElementById("main-content-area").innerHTML = html;
            pageInitializer();
        })
        .catch((error) => {
            console.error("載入錯誤:", error);
        });
}

window.addEventListener("DOMContentLoaded", () => {
    pageInitializer()

    // 收和所有 dropdown
    document.querySelectorAll(".dropdown-menu").forEach((menu) => {
        menu.classList.remove("show");
        menu.style.maxHeight = "0";
    });

    // 還原 dropdown 狀態
    document.querySelectorAll(".dropdown-menu").forEach((menu) => {
        const id = menu.id;
        const state = localStorage.getItem("dropdown:" + id);
        if (state === "open") {
            menu.classList.add("show");
            menu.style.maxHeight = "none";
        }
    });

    // 還原active樣式
    const activeId = localStorage.getItem("activeLink");
    if (activeId) {
        const link = document.getElementById(activeId);
        if (link) {
            const li = link.closest("li");
            if (li) li.classList.add("active");

            const dropdown = link.closest(".dropdown-menu");
            if (dropdown) dropdown.classList.add("show");
        }
    }
    // 依照 URL 自動設定 active
    (function autoActiveByUrl() {
        const path = location.pathname;

        let matchLink = null;

        document.querySelectorAll(".sidebar a").forEach(a => {
            if (a.classList.contains("dropdown-toggle")) return;

            const dataPath = a.dataset.path || "";
            // Admin
            if (path.startsWith("/admin")) {
                if (("/admin/" + dataPath) === path ||
                    (path === "/admin/dashboard" && dataPath === "dashboard")) {
                    matchLink = a;
                }
            }

            // Organizer
            if (path.startsWith("/organizer/dashboard")) {
                if (path === "/organizer/dashboard" && dataPath === "") {
                    matchLink = a;
                }
                if (("/organizer/dashboard/" + dataPath) === path) {
                    matchLink = a;
                }
            }
        });

        if (matchLink) {
            const li = matchLink.closest("li");
            li.classList.add("active");
            localStorage.setItem("activeLink", matchLink.id);

            // 如果存在，展開上層的 dropdown-menu
            const dropdown = matchLink.closest(".dropdown-menu");
            if (dropdown) {
                dropdown.classList.add("show");
                dropdown.style.maxHeight = "none"; // 防動畫卡住
                const parentLi = dropdown.closest("li");
                if (parentLi) parentLi.classList.add("expanded");
            }
        }
    })();


});

function toggleDropdown(id) {
    const menu = document.getElementById(id);
    const parentLi = menu.closest("li");
    const isOpening = !menu.classList.contains("show");

    // 儲存狀態
    localStorage.setItem("dropdown:" + id, isOpening ? "open" : "closed");

    if (!isOpening) {
        // === 收合 ===
        const currentHeight = menu.scrollHeight;
        menu.style.maxHeight = currentHeight + "px";
        requestAnimationFrame(() => {
            menu.style.maxHeight = "0";
        });
        menu.classList.remove("show");
        parentLi.classList.remove("expanded");
        return;
    }

    // === 展開 ===
    menu.classList.add("show");
    const targetHeight = menu.scrollHeight + "px";
    menu.style.maxHeight = "0";
    requestAnimationFrame(() => {
        menu.style.maxHeight = targetHeight;
    });

    menu.addEventListener("transitionend", function handler() {
        if (menu.classList.contains("show")) {
            menu.style.maxHeight = "none";
        }
        menu.removeEventListener("transitionend", handler);
    });

    parentLi.classList.add("expanded");
}


