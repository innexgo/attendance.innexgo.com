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

$(document).ready(function() {
  $('#submit-creds').click(loginattempt);
  $('#username').keydown(function(event) {
    if(event.which == 13) {
      event.preventDefault();
      $('#password').focus();
    }
  });
  $('#password').keydown(function(event) {
      if(event.which == 13) {
        event.preventDefault();
        loginattempt();
      }
  });
});
