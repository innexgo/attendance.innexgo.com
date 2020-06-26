"use strict"

/* global
 window.Cookies moment
 modalAlert apiUrl staticUrl fetchJson sleep INT32_MAX
 givePermError
 */



// Removes credentials and moves to login page
function logOut() {
    window.Cookies.remove('apiKey');
    window.Cookies.remove('courses');
    window.Cookies.remove('period');
    window.Cookies.remove('nextPeriod');
    window.Cookies.remove('semester');
    window.location.replace(staticUrl() + '/login.html');
}

// moves to login page on cookie expiration
async function ensureSignedIn() {
  // check if sign in cookie exists and is logged in
  let apiKey = window.Cookies.getJSON('apiKey');
  if (apiKey == null) {
    logOut();
    return;
  }

  // now check if the cookie is expired
  if(apiKey.expirationTime < moment().valueOf()) {
    modalAlert('Session has expired');
    logOut();
  }

  // make test request, on failure delete the cookies
  // usually means something went wrong with server
  await fetch(`${apiUrl()}/misc/validate/?apiKey=${apiKey.key}`)
    .then(function(response) {
      if(!response.ok) {
        alert('Current session invalid, refresh the page.');
        logOut();
      }
    });
 }

async function userInfo() {
  var apiKey = window.Cookies.getJSON('apiKey');
  if (apiKey == null) {
    console.log('No ApiKey!');
    return;
  }

  try {
    let period = await fetchJson(`${apiUrl()}/misc/getPeriodByTime/?time=${moment().valueOf()}&apiKey=${apiKey.key}`);
    window.Cookies.set('period', period, {sameSite: 'strict'} );

    let nextPeriod = await fetchJson(`${apiUrl()}/misc/nextPeriod/?apiKey=${apiKey.key}`);
    window.Cookies.set('nextPeriod', nextPeriod, {sameSite: 'strict'} );

    let semester = await fetchJson(`${apiUrl()}/misc/getSemesterByTime/?time=${moment().valueOf()}&apiKey=${apiKey.key}`);
    window.Cookies.set('semester', semester, {sameSite: 'strict'} );

    let courses = await fetchJson(`${apiUrl()}/course/?semesterStartTime=${semester.startTime}&userId=${apiKey.user.id}&offset=0&count=${INT32_MAX}&apiKey=${apiKey.key}`);
    window.Cookies.set('courses', courses, {sameSite: 'strict'} );

  }
  catch(err) {
    givePermError('Failed to fetch data, refresh.');
  }
}

async function pollUserInfo() {
  for(;;) {
    await userInfo();
    let nextPeriod = window.Cookies.getJSON('nextPeriod');
    await sleep(nextPeriod.startTime - moment().valueOf());
  }
}

async function pollEnsureSignedIn() {
  for(;;) {
    await ensureSignedIn();
    let apiKey = window.Cookies.getJSON('apiKey');
    await sleep(apiKey.expirationTime - moment().valueOf());
  }
}

pollUserInfo();
pollEnsureSignedIn();
