// 紀錄輪播的位置與每次滑動幾張
let currentIndex = 0;

function initActivityCards() {
  const $container = $("#activityContainer");
  const $empty = $("#emptyState");

  // 輪播圖的按鈕
  const $controls = $("#carouselControls");
  const $prevBtn = $("#prevBtn");
  const $nextBtn = $("#nextBtn");

  if ($container.length === 0 || $empty.length === 0) return;

  $empty.on("click", ".add-btn", function () {
    localStorage.setItem("activeLink", "sidebar-event");
    localStorage.setItem("dropdown:content-dropdown", "open");
    window.location.href = "/organizer/dashboard/event";
  });

  $.getJSON("/api/events/latest")
    .done(activities => {

      if (!activities || activities.length === 0) {
        $empty.show();
        $controls.hide();
        return;
      }

      $empty.hide();

      activities.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
      $container.empty();
      activities.forEach(act => {
        const imgUrl = act.images?.[0]?.imageUrl || 'https://placehold.co/100x100?text=No+Image';
        const startTime = act.eventStart || "未設定";
        const endTime = act.eventEnd || "未設定";

        const cardHtml = `
          <div class="activity-card">
            <div class="card-top">
              <img src="${imgUrl}" alt="活動圖片">
              <div class="card-content">
                <h3>${act.title}</h3>
                <div class="meta">
                  <span class="created">活動開始時間：</span>
                  ${startTime}
                  <span class="event-time">活動結束時間：<wbr></span>
                  ${endTime}
                </div>
              </div>
            </div>
            <div class="card-bottom">
              <div class="stats">
                <div class="stat-item">
                  <span class="label">👁️ 瀏覽量</span>
                  <span class="value">${act.views ?? 0}</span>
                </div>
                <div class="stat-item">
                  <span class="label">💰 總營收</span>
                  <span class="value">${act.revenue ?? 0}</span>
                </div>
                <div class="stat-item">
                  <span class="label">🎟️ 售出票數</span>
                  <span class="value">${act.ticketsSold ?? 0}</span>
                </div>
                <div class="stat-item">
                  <span class="label">🔗 分享數</span>
                  <span class="value">${act.shares ?? 0}</span>
                </div>
              </div>
            </div>
          </div>
        `;

        $container.append(cardHtml);
      });

      // 輪播圖相關設定
      const totalItems = activities.length;

      checkControlsVisibility(totalItems);
      $prevBtn.off('click').on('click', () => {
        const visibleCount = getVisibleItemsCount();

        if (currentIndex - visibleCount >= 0) {
          currentIndex -= visibleCount;
        } else {

          const remainder = totalItems % visibleCount;
          const lastPageCount = remainder === 0 ? visibleCount : remainder;
          currentIndex = totalItems - lastPageCount;

        }
        moveCarousel();
      });

      // 下一頁
      $nextBtn.off('click').on('click', () => {
        const visibleCount = getVisibleItemsCount();

        // 邏輯：加上一頁的數量
        if (currentIndex + visibleCount < totalItems) {
          currentIndex += visibleCount;
        } else {
          // 循環：回到第一頁
          currentIndex = 0;
        }
        moveCarousel();
      });

      // 4. 視窗縮放時重置
      $(window).off('resize.carousel').on('resize.carousel', function () {
        currentIndex = 0;
        moveCarousel();
        checkControlsVisibility(totalItems);
      });

    })
    .fail(() => {
      console.error("無法載入活動資料");
      $empty.show().html(`<p style="color:red;">載入資料失敗，請稍後再試。</p>`);
    });

  function checkControlsVisibility(total) {
    const visibleCount = getVisibleItemsCount();
    if (total > visibleCount) {
      $controls.css('display', 'flex');
    } else {
      $controls.hide();
    }
  }
}

function loadKpi() {
  $.getJSON("/api/dashboard/kpi", function (res) {
    if (!res.success) return;

    const k = res.data;

    $("#viewsTotal").removeClass("loading-text").text(k.viewsTotal);
    $("#ticketsTotal").removeClass("loading-text").text(k.ticketsTotal);
    $("#revenueTotal").removeClass("loading-text").text(k.revenueTotal);
  });
}

// 移動輪播圖函式
function moveCarousel() {
  const $container = $("#activityContainer");
  const $card = $(".activity-card").first();

  // 防呆：如果沒有卡片就不跑
  if ($card.length === 0) return;

  // 計算移動距離：(單張卡片寬 + CSS Gap)
  const cardWidth = $card.outerWidth();
  const gap = 20;

  const shift = (cardWidth + gap) * currentIndex;

  $container.css("transform", `translateX(-${shift}px)`);
}

// 判斷可以顯示幾張卡片
function getVisibleItemsCount() {
  const width = $(window).width();
  if (width <= 600) return 1;       // 手機: 1張
  if (width <= 992) return 2;       // 平板: 2張
  return 3;                         // 電腦: 3張
}
