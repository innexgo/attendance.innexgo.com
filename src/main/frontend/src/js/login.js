"use strict"

/* global
  Cookies moment
  isEmpty fetchJson staticUrl apiUrl
*/

function giveError(errormsg) {
  document.getElementById('error').innerHTML = errormsg;
}

//Checks for blank password or user id, or other obvious misconfigurations
function validateattempt(userName, password)  {
  if(isEmpty(userName)){
    giveError('Please enter your email')
    return false
  }
  if(isEmpty(password)){
    giveError('Please enter your password')
    return false
  }
  // TODO do some validations, (no JS, SQL, invalid chars)
  return true;
}

async function loginattempt() {
  let userName = document.getElementById('username').value;
  let password = document.getElementById('password').value;

  if(!validateattempt(userName, password)) {
    // failed attempt.
    return;
  }

  // get date 30 min into the future
  let apiKeyExpirationTime = moment().add(30, 'hours').valueOf();

  try {

    let apiKey = await fetchJson(`${apiUrl()}/apiKey/new/?userEmail=${userName}&userPassword=${password}&expirationTime=${apiKeyExpirationTime}`);
    Cookies.set('apiKey', apiKey);

    if (Cookies.getJSON('prefs') == null) {
      console.log('resetTheme login');
      Cookies.set('prefs', {colourTheme: 'default', sidebarStyle: 'fixed'});
    }

    // now jump to next page
    if(apiKey.user.ring == 0) {
      window.location.assign(staticUrl() + '/adminoverview.html');
    } else if(apiKey.user.ring == 1) {
      //TODO split ensuresignedin into a userinfo and use this to prefetch the data before jumping
      window.location.assign(staticUrl() + '/overview.html');
    }
  } catch(err) {
    console.log(err);
    giveError('Your email or password doesn\'t match our records.');
  }
}

window.onload = function() {
  let username = document.getElementById('username');
  let password = document.getElementById('password');

  username.addEventListener('keydown', function(event) {
    if (event.keyCode === 13) {
      event.preventDefault();
      if(isEmpty(password)){
        loginattempt();
      }
      else {
        password.focus();
      }
    }
  });

  password.addEventListener('keydown', function(event) {
    if (event.keyCode === 13) {
      event.preventDefault();
      loginattempt();
    }
  });
}
