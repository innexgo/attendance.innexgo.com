// moves to login page on cookie expiration
function ensureSignedIn() {
  // check if sign in cookie exists and is logged in
  var apiKey = Cookies.getJSON('apiKey');
  if(apiKey == null) {
    window.location.replace(thisUrl() + '/login.html');
    return;
  }

  // now check if the cookie is expired
  if(apiKey.expirationTime < moment().unix()) {
    alert("Session has expired");
    window.location.replace(thisUrl() + '/login.html');
    return;
  }

  // make test request, on failure delete the cookies
  // usually means something went wrong with server
  var url = thisUrl() + '/validateTrusted/?apiKey=' + apiKey.key;
  request(url,
    // on success
    function(xhr) {},
    // on failure
    function(xhr) {
      alert('Current session invalid, refresh the page.');
      Cookies.remove('apiKey');
    }
  );
}

function userInfo() {
  var apiKey = Cookies.getJSON('apiKey');
  if(apiKey != null) {
    var url = thisUrl() +
      '/schedule/' +
      '?userId=' + apiKey.user.id +
      '&period=' + getPeriod(new Date()) +
      '&apiKey=' + apiKey.key;
    request(url,
      //success
      function(xhr) {
        var schedule = JSON.parse(xhr.responseText);
        if(schedule.length == 1) {
          Cookies.set('schedule', schedule[0])
        }
      },
      //failure
      function(xhr) {
        return;
      }
    );
  }
}


// make sure they're signed in every 10 seconds
setInterval(function(){
  ensureSignedIn();
  userInfo();
}, 10000);

//first make sure we're signed in
ensureSignedIn();
//add nice cookies
userInfo();
