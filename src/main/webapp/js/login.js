"use strict"

//Checks for blank password or user id, or other obvious misconfigurations 
function validateattempt(userName, password)  {
  if(isBlank(userName)){
    giveError("Please enter your username")
    return false
  }
  if(isBlank(password)){
    giveError("Please enter your password")
    return false
  }
  // TODO do some validations, (no JS, SQL, invalid chars)
  return true;
}

function giveError(errormsg) {
  document.getElementById('error').innerHTML = errormsg;
}

function loginattempt() {
  var userName = document.getElementById('username').value;
  var password = document.getElementById('password').value;

  if(!validateattempt(userName, password)) {
    // failed attempt.
    return;
  }

  // get date 30 min into the future
  var apiKeyExpirationTime = moment().add(30, 'minutes').unix();

  var url = thisUrl() +
    '/apiKey/new/?userName=' + encodeURIComponent(userName) +
    '&password=' + encodeURIComponent(password) +
    '&expirationTime=' + encodeURIComponent(apiKeyExpirationTime);

  request(url,
    // success function
    function(xhr) {
      var apiKey = JSON.parse(xhr.responseText);
      // store info
      Cookies.set('apiKey', apiKey);
      // now jump to next page
      if(apiKey.user.permissionLevel < 2) {
        // TODO make administrator overview
        window.location.assign(thisUrl() + "/overviewdecider.html");
      } else{
        giveError("At the moment, students cannot access an account.");
      }
    },
    // failure function
    function(xhr) {
      console.log("authentication failure!");
      giveError("Check your username and password.");
    }
  );
}

Cookies.remove('apiKey');

window.onload = function() {
  var username = document.getElementById("username");
  var password = document.getElementById("password");

  username.addEventListener("keydown", function(event) {
    if (event.keyCode === 13) {
      event.preventDefault();
      if(!isBlank(password)){
        loginattempt();
      }
      else {
        password.focus();
      }
    }
  });

  password.addEventListener("keydown", function(event) {
    if (event.keyCode === 13) {
      event.preventDefault();
      loginattempt();
    }
  });
}
