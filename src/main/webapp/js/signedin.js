"use strict"

// Removes credentials and moves to login page
function logout() {
    Cookies.remove('apiKey');
    Cookies.remove('courses');
    Cookies.remove('period');
    Cookies.remove('nextPeriod');
    window.location.replace(staticUrl() + '/login.html');
}

// moves to login page on cookie expiration
async function ensureSignedIn() {
  // check if sign in cookie exists and is logged in
  var apiKey = Cookies.getJSON('apiKey');
  if (apiKey == null) {
    logout();
  }

  // now check if the cookie is expired
  if (apiKey.expirationTime < moment().unix()) {
    alert('Session has expired');
    logout();
  }

  // make test request, on failure delete the cookies
  // usually means something went wrong with server
  fetch(apiUrl() + '/misc/validate/?apiKey=' + apiKey.key)
    .then(response => parseResponse(response))
    .catch(function(err) {
      alert('Current session invalid, refresh the page.');
      logOut()
    });
 }

async function userInfo() {
  var apiKey = Cookies.getJSON('apiKey');
  if (apiKey == null) {
    console.log('No ApiKey!');
    return;
  }
  fetch(`${apiUrl()}/misc/currentPeriod/?apiKey=${apiKey.key}`)
    .then(response => parseResponse(response))
    .then(function(data) {
        Cookies.set('period', data);
    })
    .catch(function(err) {
      givePermError('Error Fetching necessary data, try refreshing');
    })
  fetch(`${apiUrl()}/misc/nextPeriod/?apiKey=${apiKey.key}`)
    .then(response => parseResponse(response))
    .then(function(data) {
        Cookies.set('nextPeriod', data);
    })
    .catch(function(err) {
      givePermError('Error Fetching necessary data, try refreshing');
    })
  fetch(`${apiUrl()}/course/?teacherId=${apiKey.user.id}&apiKey=${apiKey.key}`)
    .then(response => parseResponse(response))
    .then(function(data) {
        Cookies.set('courses', data);
    })
    .catch(function(err) {
      givePermError('Error Fetching necessary course data, try refreshing');
    })
}


async function pollUserInfo() {
  while(true) {
    userInfo();
    let nextPeriod = Cookies.getJSON('nextPeriod');
    sleep(nextPeriod.startTime - moment.value());
  }
}

async function pollEnsureSignedIn() {
  while(true) {
    ensureSignedIn();
    let apiKey = Cookies.getJSON('nextPeriod');
    sleep(apiKey.expirationTime - moment.value());
  }
}

pollUserInfo();
pollEnsureSignedIn();
