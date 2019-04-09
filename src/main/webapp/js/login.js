
//Checks for blank password or user id, or other obvious misconfigurations 
function validateattempt()  {

}

function loginattempt() {
  var userId = document.getElementById('user-id').value;
  var password = document.getElementById('password').value;

  setCookie('userId', username);
  setCookie('password', password);

  request(thisUrl() + '/key/new/?userId=' + userId + 



