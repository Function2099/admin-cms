$(function () {
    // 載入公告列表
    $.get("/api/announcements", function (data) {
        let html = $.map(data, function (ann) {
            return `<div><strong>${ann.title}</strong><p>${ann.content}</p><hr/></div>`;
        }).join("");
        $("#anno-list").html(html);
    });

    // 表單送出
    $(document).on("submit", "#anno-form", function (e) {
        e.preventDefault();
        let title = $("#title").val();
        let content = $("#content").val();

        console.log("表單送出", { title, content });

        $.ajax({
            url: "/api/announcements",
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify({ title, content }),
            success: function () {
                $("#anno-form")[0].reset();

                $.get("/api/announcements", function (data) {
                    const html = $.map(data, function (ann) {
                        return `<div><strong>${ann.title}</strong><p>${ann.content}</p><hr/></div>`;
                    }).join("");
                    $("#anno-list").html(html);
                });
            }
        });
    });

});
