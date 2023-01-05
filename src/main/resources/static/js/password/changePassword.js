$(document).ready(function () {
    $("#formChangePass").submit(function (e) {
        let form = $(this);
        let actionUrl = form.attr("action");
        e.preventDefault();
        let modal = $('#notification');
        let isEmptyError = checkEmptyError([password]);
        let isPassword = checkPasswordError(password);
        let isCheckMatchesPassword = checkMatches(password, repeatPassword);
        if (isEmptyError || isPassword || isCheckMatchesPassword) {
            console.log('Bạn không đăng kí được đâu')
            return;
        } else {

            $.ajax({
                type: "POST",
                url: actionUrl,
                data: form.serialize(),
                success: function (data) {
                    console.log('Thay đổi mật khẩu thành công');
                    $(".modal-body p").text(data.message);
                    $(".modal-title").text("Thành công");
                    if ($('#confirmButton').hasClass("close-button")) {
                        $('#confirmButton')[0].classList.remove("close-button");
                    }
                    $('#confirmButton')[0].classList.add("btnSuccessLogin");
                    $('#confirmButton').text("OK");
                    modal.modal("show");
                },
                error: function (data) {
                    console.log('An error occurred.');
                    console.log(data);
                    $(".modal-body p").text(data.err);
                    $(".modal-title").text("Lỗi");
                    $('#confirmButton')[0].classList.add("close-button");
                    $('#confirmButton').text("OK");
                    modal.modal("show")
                },
            });
        }
    });

    $('#confirmButton').on("click", function () {
        if ($(this).hasClass("btnSuccessLogin")) {
            return window.location = 'http://localhost:8081/hello';
        } else {
            return;
        }
    });
});
