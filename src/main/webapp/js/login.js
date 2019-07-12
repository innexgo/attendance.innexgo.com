"use strict"

//Checks for blank password or user id, or other obvious misconfigurations 
function validateattempt(userName, password)  {
  // TODO do some validations, (no JS, SQL, invalid chars)
  return true;
}

function giveError(errormsg) {
  document.getElementById('error').innerHTML = errormsg;
}

function loginattempt() {
  var userName = document.getElementById('username').value;
  var password = document.getElementById('password').value;

  if(!validateattempt) {
    // post error text and fail
    giveError("Your entry is invalid or contains invalid characters.");
    return;
  }

  // get date 30 min into the future
  var apiKeyExpirationTime = new Date();
  apiKeyExpirationTime.setMinutes(apiKeyExpirationTime.getMinutes() + 30);

  var url = thisUrl() +
    '/apiKey/new/?userName=' + encodeURIComponent(userName) +
    '&password=' + encodeURIComponent(password) +
    '&expirationTime=' + epochTime(apiKeyExpirationTime)

  request(url,
    // success function
    function(xhr) {
      var apiKey = JSON.parse(xhr.responseText);
      // store info
      Cookies.set('apiKey', apiKey.key);
      Cookies.set('apiKeyExpirationTime', apiKey.expirationTime);
      Cookies.set('userName', apiKey.user.name);
      Cookies.set('userId', apiKey.user.id);
      // now jump to next page
      window.location.replace(thisUrl() + "/index.html");
    },
    // failure function
    function(xhr) {
      console.log("authentication failure!");
      giveError("Check your usename and password.");
    }
  );
}

// on load remove any cookies to prevent confusion
Cookies.remove('apiKey');
Cookies.remove('apiKeyExpirationTime');
Cookies.remove('userName');
Cookies.remove('userId');
