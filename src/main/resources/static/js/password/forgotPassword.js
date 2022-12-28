let password = document.querySelector('#typePasswordX-2');
let repeatPassword = document.querySelector('#typeRepeatPasswordX-2');
let form = document.getElementById("formResetPass");
let textError = 'Không được để rỗng';

$(document).ready(function () {
    $("#formResetPass").submit(function (e) {
        let form = $(this);
        let actionUrl = form.attr("action");
        e.preventDefault();
        let modal = $('#notification');

        let isEmptyError = checkEmptyError([password]);
        let isPassword = checkPasswordError(password);
        let isCheckMatchesPassword = checkMatches(password, repeatPassword);
        if (isEmptyError || isPassword || isCheckMatchesPassword || checkLengthError) {
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
                    $('#confirmButton').text("Đăng nhập");
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
            return window.location = 'http://localhost:8081/login';
        } else {
            return;
        }
    });
});

function showError(input, message) {
    let parent = input.parentElement;
    let small = parent.querySelector('small');
    parent.classList.add('error');
    small.innerText = message;
}

function showSuccess(input) {
    let parent = input.parentElement;
    let small = parent.querySelector('small');
    parent.classList.remove('error');
    small.innerText = '';
}

function checkEmptyError(listInput) {
    listInput.forEach(input => {
        let isEmptyError = false;
        input.value = input.value.trim();
        if (!input.value) {
            isEmptyError = true;
            showError(input, textError);
        } else {
            showSuccess(input);
        }
        return isEmptyError;
    });
}

function checkPasswordError(input) {
    const regexPassword = /^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$/i;
    let isPasswordError = !regexPassword.test(input.value);
    if (regexPassword.test(input.value)) {
        showSuccess(input)
    } else {
        showError(input, 'Mật khẩu không hợp lệ')
    }
    return isPasswordError;
}

function checkMatches(passwordInput, repeatPassword) {
    if (passwordInput.value !== repeatPassword.value) {
        showError(repeatPassword, 'Mật khẩu đang không trùng khớp!!!!')
        return true
    } else {
        console.log(passwordInput.value);
        console.log(repeatPassword.value);
        showSuccess(passwordInput);
        return false;
    }
}


