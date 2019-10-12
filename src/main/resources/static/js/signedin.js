// moves to login page on cookie expiration
function ensureSignedIn() {
  // check if sign in cookie exists and is logged in
  var apiKey = Cookies.getJSON('apiKey');
  if (apiKey == null) {
    window.location.replace(staticUrl() + '/login.html');
    return;
  }

  // now check if the cookie is expired
  if (apiKey.expirationTime < moment().unix()) {
    alert('Session has expired');
    window.location.replace(staticUrl() + '/login.html');
    return;
  }

  // make test request, on failure delete the cookies
  // usually means something went wrong with server
  var url = apiUrl() + '/validate/?apiKey=' + apiKey.key;
  request(url,
    // on success
    function (xhr) { },
    // on failure
    function (xhr) {
      alert('Current session invalid, refresh the page.');
      Cookies.remove('apiKey');
    }
  );
}

function userInfo() {
  var apiKey = Cookies.getJSON('apiKey');
  if (apiKey != null) {
    request(apiUrl() + '/period/' +
      '?time=' + moment().valueOf() +
      '&apiKey=' + apiKey.key,
      function (xhr) {
        var period = JSON.parse(xhr.responseText)[0];
        // if class has ended, or not yet begun, delete the relevant cookies
        if (period == null) {
          Cookies.remove('period');
          console.log('could not determine period: school not in session');
          return;
        } else {
          Cookies.set('period', period);
        }
      },
      //failure
      function (xhr) {
        console.log('error has no school');
        return;
      }
    );
  }
}

$(document).ready(function () {
  ensureSignedIn();
  userInfo();
  var apiKey = Cookies.getJSON('apiKey');
  var period = Cookies.getJSON('period');
  doTimer(apiKey.expirationTime - moment().valueOf(), 1, function () {
    if (apiKey.expirationTime - moment().valueOf() < 0) {
      ensureSignedIn();
    };
  }, ensureSignedIn());

  setInterval(function () {
    if (period == null) {
      userInfo();
    }
    else if (period.endTime - moment().valueOf() < 0) {
      userInfo();
    };
  }, 10000);
});
