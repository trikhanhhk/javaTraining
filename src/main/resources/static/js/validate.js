var firstName = document.querySelector('#firstName');
var lastName = document.querySelector('#lastName');
var phoneNumber = document.querySelector('#phoneNumber');
var address = document.querySelector('#address');
var dateOfBirth = document.querySelector('#dateOfBirth');
var email = document.querySelector('#email');
var password = document.querySelector('#password');
var repeatPassword = document.querySelector('#repeatPassword');
var form =  document.getElementById("theForm");
var textError = 'Không được để rỗng';
var textErrorEmail = 'Sai định dạng email';

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

function checkEmptyError(listInput){
 listInput.forEach(input => {
     let isEmptyError = false;
      input.value = input.value.trim();
      if (!input.value){
          isEmptyError = true;
          showError(input,textError);
      }else{
          showSuccess(input);
      }
      return isEmptyError;
 });
}

function checkEmailError(input){
    const regexEmail = /^(([^<>()[\]\.,;:\s@\"]+(\.[^<>()[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i;

    let isEmailError = !regexEmail.test(input.value);
    if (regexEmail.test(input.value)){
        showSuccess(input)
    }else {
        showError(input,textErrorEmail)
    }
    return isEmailError;
}

function checkLengthError(input,max,min){
     input.value = input.value.trim();
     if (input.value.length > max){
         showError(input,'Không được lớn hơn '+max+ ' kí tự')
         return true
     }if (input.value.length < min){
        showError(input,'Không được nhỏ hơn ' + min +' kí tự')
        return true
    }
     return false
}

function checkPasswordError(input){
    const regexPassword = /^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$/i;
    let isPasswordError = !regexPassword.test(input.value);
    if(regexPassword.test(input.value)){
        showSuccess(input)
    }else{
        showError(input,'Mật khẩu không hợp lệ')
    }
    return isPasswordError;
}

function checkMatches(passwordInput, repeatPassword){
    if (passwordInput.value !== repeatPassword.value){
        showError(repeatPassword,'Mật khẩu đang không trùng khớp!!!!')
        return true
    }else {
        console.log(passwordInput.value);
        console.log(repeatPassword.value);
        showSuccess(passwordInput);
        return false;
    }
}
form.addEventListener('submit', function (e) {
    e.preventDefault();
    let isEmptyError = checkEmptyError([firstName,lastName,phoneNumber,address,dateOfBirth,email,password]);
    let isCheckLengthFirstName = checkLengthError(firstName,100,2);
    let isCheckLengthLastName = checkLengthError(lastName,100,2);
    let isCheckLengthPhone = checkLengthError(phoneNumber,11,9);
    let isEmailError = checkEmailError(email);
    let isPassword = checkPasswordError(password);
    let isCheckMatchesPassword = checkMatches(password,repeatPassword);
    if (isEmptyError || isEmailError || isPassword || isCheckMatchesPassword || isCheckLengthPhone || isCheckLengthLastName || isCheckLengthFirstName){
        console.log('Bạn không đăng kí được đâu')
        return;
    }else{
        $(this).submit();
    }
});

