$(document).ready(function() {
    $("#formResetPass").submit(function(e) {
        let form = $(this);
        let actionUrl = form.attr("action");
        e.preventDefault();
        let modal = $('#notification');
        $.ajax({
            type: "POST",
            url: actionUrl,
            data: form.serialize(),
            success: function(data)
            {
                console.log('Thay đổi mật khẩu thành công');
                $(".modal-body p").text(data.message);
                $(".modal-title").text("Thành công");
                // alert(data); // show response from the php script.
                modal.modal("show")
            },
            error: function (data) {
                console.log('An error occurred.');
                console.log(data);
                $(".modal-body p").text(data.err);
                $(".modal-title").text("Lỗi");
                modal.modal("show")
            },
        });
    });
});
