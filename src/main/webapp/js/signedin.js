"use strict"

/* global
 setLocalJson moment
 modalAlert apiUrl staticUrl fetchJson sleep INT32_MAX
 givePermError
 */



// Removes credentials and moves to login page
function logOut() {
  window.localStorage.clear();
  window.location.replace(staticUrl() + '/login.html');
}

// moves to login page on cookie expiration
async function ensureSignedIn() {
  // check if sign in cookie exists and is logged in
  let apiKey = getLocalJson('apiKey');
  if (apiKey == null) {
    logOut();
    return;
  }

  // now check if the cookie is expired
  if (apiKey.expirationTime < moment().valueOf()) {
    modalAlert('Session has expired');
    logOut();
  }

  // make test request, on failure delete the cookies
  // usually means something went wrong with server
  await fetch(`${apiUrl()}/misc/validate/?apiKey=${apiKey.key}`)
    .then(function(response) {
      if (!response.ok) {
        alert('Current session invalid, refresh the page.');
        logOut();
      }
    });
}

async function userInfo() {
  let apiKey = getLocalJson('apiKey');
  if (apiKey == null) {
    console.log('No ApiKey!');
    return;
  }

  let period = await fetchJson(`${apiUrl()}/misc/getPeriodByTime/?time=${moment().valueOf()}&apiKey=${apiKey.key}`);
  setLocalJson('period', period);

  let nextPeriod = await fetchJson(`${apiUrl()}/misc/getNextPeriod/?apiKey=${apiKey.key}`);
  setLocalJson('nextPeriod', nextPeriod);

  let semester = await fetchJson(`${apiUrl()}/misc/getSemesterByTime/?time=${moment().valueOf()}&apiKey=${apiKey.key}`);
  setLocalJson('semester', semester);

  let courses = [];
  if (semester != null) {
    courses = await fetchJson(`${apiUrl()}/course/?semesterStartTime=${semester.startTime}&userId=${apiKey.user.id}&offset=0&count=${INT32_MAX}&apiKey=${apiKey.key}`);
  }
  setLocalJson('courses', courses);
}

async function pollUserInfo() {
  while (true) {
    await userInfo();
    let nextPeriod = getLocalJson('nextPeriod');
    if (nextPeriod === null) {
      break;
    }
    await sleep(nextPeriod.startTime - moment().valueOf());
  }
}

async function pollEnsureSignedIn() {
  while (true) {
    await ensureSignedIn();
    let apiKey = getLocalJson('apiKey');
    await sleep(apiKey.expirationTime - moment().valueOf());
  }
}

pollUserInfo();
pollEnsureSignedIn();
