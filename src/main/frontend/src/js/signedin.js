"use strict"

/* global
 Cookies moment
 modalAlert apiUrl staticUrl fetchJson sleep INT32_MAX
 givePermError
 */



// Removes credentials and moves to login page
function logOut() {
    Cookies.remove('apiKey');
    Cookies.remove('courses');
    Cookies.remove('period');
    Cookies.remove('nextPeriod');
    Cookies.remove('semester');
    window.location.replace(staticUrl() + '/login.html');
}

// moves to login page on cookie expiration
async function ensureSignedIn() {
  // check if sign in cookie exists and is logged in
  let apiKey = Cookies.getJSON('apiKey');
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
  var apiKey = Cookies.getJSON('apiKey');
  if (apiKey == null) {
    console.log('No ApiKey!');
    return;
  }

  try {
    let period = await fetchJson(`${apiUrl()}/misc/getPeriodByTime/?time=${moment().valueOf()}&apiKey=${apiKey.key}`);
    Cookies.set('period', period);

    let nextPeriod = await fetchJson(`${apiUrl()}/misc/nextPeriod/?apiKey=${apiKey.key}`);
    Cookies.set('nextPeriod', nextPeriod);

    let semester = await fetchJson(`${apiUrl()}/misc/getSemesterByTime/?time=${moment().valueOf()}&apiKey=${apiKey.key}`);
    Cookies.set('semester', semester);

    let courses = await fetchJson(`${apiUrl()}/course/?semesterStartTime=${semester.startTime}&userId=${apiKey.user.id}&offset=0&count=${INT32_MAX}&apiKey=${apiKey.key}`);
    Cookies.set('courses', courses);

  }
  catch(err) {
    givePermError('Failed to fetch data, refresh.');
  }
}

async function pollUserInfo() {
  for(;;) {
    await userInfo();
    let nextPeriod = Cookies.getJSON('nextPeriod');
    await sleep(nextPeriod.startTime - moment().valueOf());
  }
}

async function pollEnsureSignedIn() {
  for(;;) {
    await ensureSignedIn();
    let apiKey = Cookies.getJSON('apiKey');
    await sleep(apiKey.expirationTime - moment().valueOf());
  }
}

pollUserInfo();
pollEnsureSignedIn();
