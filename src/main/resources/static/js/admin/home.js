$(".deleteEmployee").on("click", function () {
    var id = $(this).attr("data-id");
    console.log(id);
    <!--              alert(id);-->
    var result = confirm("Bạn có muốn xóa nhân viên này không?");
    if (result) {
        window.location.href = "/" + id;
    } else {
        return;
    }
});


$("#formImport").on("submit", function(e) {
    // e.preventDefault();
    let modal = $('#notification');
    var input = document.getElementById('fileInput');
    if (input.files.length > 0) { // This is VERY unlikely, browser support is near-universal
        for (var i = 0; i <= input.files.length - 1; i++) {

            var fileName = input.files.item(i).name;      // THE NAME OF THE FILE.
            var fileSize = input.files.item(i).size;
            const file = Math.round((fileSize / 1024));// THE SIZE OF THE FILE.
            if (fileName.trim().endsWith('xlsx') || fileName.trim().endsWith('xls')){
                if (file >= 4096){
                    e.preventDefault();
                    $("#notification  .modal-body p").text("Dung lượng file phải nhỏ hơn 4MB");
                    $("#notification  .modal-title").text("Lỗi");
                    $('#notification #confirmButton')[0].classList.add("close-button");
                    $('#notification  #confirmButton').text("OK");
                    modal.modal("show");
                    return;
                }else{
                    return;
                }
            } else {
                e.preventDefault();
                $("#notification  .modal-body p").text("Chỉ nhận các file .xlsx, .xls");
                $("#notification  .modal-title").text("Lỗi");
                $('#notification #confirmButton')[0].classList.add("close-button");
                $('#notification #confirmButton').text("OK");
                modal.modal("show")
            }
        }
    }else{
        e.preventDefault();
        $("#notification .modal-body p").text("Vui lòng chọn file để import");
        $("#notification .modal-title").text("Lỗi");
        $('#notification #confirmButton')[0].classList.add("close-button");
        $('#notification #confirmButton').text("OK");
        modal.modal("show")
        return;
    }
});
