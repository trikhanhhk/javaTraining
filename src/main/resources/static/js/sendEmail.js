var textError = 'Không được để rỗng';
$(document).ready(function () {
    $("#message-success").css("display", "none");
    $(".block_repeat").css("display", "none");
    $("#department").css("display", "none");
    $("#typeSend").on("change", function () {
        if (this.value == "1") {
            $("#department").css("display", "block");
        } else {
            $("#department").css("display", "none");
        }
    });
    $("#repeat").on("click", function () {
        if ($('#repeat').is(":checked")) {
            $(".block_repeat").css("display", "block");
        } else {
            $(".block_repeat").css("display", "none");
        }
    });


    $('#formSendMail').on("submit", function (e) {
        e.preventDefault();
        let listNotNull1 = $(".not-null-1");
        let listNotNull2 = $(".not-null-2");
        if(checkEmptyError([listNotNull1[0], listNotNull1[1]])) {
            e.preventDefault();
            return;
        } else {
            if ($('#repeat').is(":checked")) {
                if (checkEmptyError([listNotNull2[0], listNotNull2[1], listNotNull2[2]])) {
                    e.preventDefault();
                    return;
                } else {
                    doSubmit($(this));
                }
            } else {
                doSubmit($(this));
            }
        }

    });

});

function doSubmit(form) {
    let actionUrl = form.action;
    $.ajax({
        type: "POST",
        url: actionUrl,
        data: form.serialize(),
        success: function (data) {
            $("#message-success").text("Đã gửi Email thành công");
            $("#message-success").css("display", "block");
        },
        error: function (data) {
            console.log("error")
        },
    });
}
function checkEmptyError(listInput){
    let isEmptyError = false;
    listInput.forEach(input => {
        input.value = input.value.trim();
        if (!input.value){
            isEmptyError = true;
            showError(input,textError);
        }else {
            showSuccess(input);
        }
    });
    return isEmptyError;
}
function showError(input,message){
    let parent =  input.parentElement;
    let small = parent.querySelector('small');
    parent.classList.add('error');
    small.innerText = message;
}
function showSuccess(input){
    let parent =  input.parentElement;
    let small = parent.querySelector('small');
    parent.classList.remove('error');
    small.innerText = '';
}
