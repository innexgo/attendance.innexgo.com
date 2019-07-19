function displayUsername() {
  var username = Cookies.getJSON('apiKey').user.name;
  document.getElementById('info-username').innerHTML = username;
}


