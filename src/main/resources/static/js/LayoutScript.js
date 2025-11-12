function loadContent(fragmentUrl, virtualUrl, el) {
    // 更新網址但不重整
    history.pushState(null, "", virtualUrl);

    // 載入 fragment
    fetch(fragmentUrl)
        .then(res => {
            if (!res.ok) throw new Error("Fragment 加載失敗：" + fragmentUrl);
            return res.text();
        })
        .then(html => {
            document.getElementById("main-content").innerHTML = html;

            // active 樣式處理
            document.querySelectorAll(".sidebar li").forEach(li => li.classList.remove("active"));
            if (el && el.parentElement) el.parentElement.classList.add("active");

            // 展開 dropdown
            const dropdown = el ? el.closest(".dropdown-menu") : null;
            if (dropdown) {
                dropdown.classList.add("show");
                const parentLi = dropdown.closest("li");
                parentLi.classList.add("expanded");
            }

            if (virtualUrl === "/admin/dashboard/announcement") {
                initAnnouncementFragment();
            }
        })
        .catch(err => console.error(err.message));
}

// 返回鍵
window.addEventListener("popstate", function () {
    const path = location.pathname;
    if (path === "/admin/dashboard/announcement") {
        loadContent("/admin/content/announcement", path);
    } else if (path === "/admin/dashboard/event") {
        loadContent("/admin/content/event", path);
    } else {
        window.location.href = "/admin/dashboard";
    }
});

// 重新整理
window.addEventListener("DOMContentLoaded", () => {
    const path = window.location.pathname;
    if (path === "/admin/dashboard/announcement") {
        loadContent("/admin/content/announcement", path);
    } else if (path === "/admin/dashboard/event") {
        loadContent("/admin/content/event", path);
    } else {
        console.log("停留在 /admin/dashboard，未載入 fragment。");
    }
});

function initAnnouncementFragment() {
    $.get("/api/announcements", function (data) {
        const html = $.map(data, ann => `<div><strong>${ann.title}</strong><p>${ann.content}</p><hr/></div>`).join("");
        $("#anno-list").html(html);
    });
}


function toggleDropdown(id) {
    const menu = document.getElementById(id);
    const parentLi = menu.closest("li");

    if (menu.classList.contains("show")) {
        // 正在收合
        const currentHeight = menu.scrollHeight; // 目前高度
        menu.style.maxHeight = currentHeight + "px"; // 先固定住
        requestAnimationFrame(() => {
            menu.style.maxHeight = "0"; // 再收回去
        });
        menu.classList.remove("show");
        parentLi.classList.remove("expanded");

    } else {
        // 正在展開
        menu.classList.add("show");
        const targetHeight = menu.scrollHeight + "px"; // 真實內容高度
        menu.style.maxHeight = "0"; // 從0開始
        requestAnimationFrame(() => {
            menu.style.maxHeight = targetHeight;
        });

        parentLi.classList.add("expanded");

        // 動畫結束後清除 maxHeight 限制，避免高度被鎖死
        menu.addEventListener("transitionend", function handler() {
            if (menu.classList.contains("show")) {
                menu.style.maxHeight = "none";
            }
            menu.removeEventListener("transitionend", handler);
        });
    }

}

