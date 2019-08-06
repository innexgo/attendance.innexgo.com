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
  var url = thisUrl() + '/validate/?apiKey=' + apiKey.key;
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
    var getPeriodUrl = thisUrl() + '/period/' +
      '?minTime=' + moment().unix() +
      '&maxTime=' + moment().unix() +
      '&apiKey='  + apiKey.key;

    request(getPeriodUrl,
      function(xhr) {
        var period = JSON.parse(xhr.responseText)[0];
        // if class has ended, or not yet begun, delete the relevant cookies
        if(period == null) {
          Cookies.remove('period');
          Cookies.remove('course');
          console.log('school not in session');
          return;
        } else {
          Cookies.set('period', period);

          // now grab the current course
          var getCourseUrl = thisUrl() +
            '/course/' +
            '?teacherId=' + apiKey.user.id +
            '&period=' + period.period +
            '&apiKey=' + apiKey.key;
          request(getCourseUrl,
            //success
            function(xhr) {
              var course = JSON.parse(xhr.responseText)[0];
              if(course != null) {
                Cookies.set('course', course)
              } else {
                console.log('has no class');
                Cookies.remove('course');
              }
            },
            //failure
            function(xhr) {
              console.log('error has no class');
              return;
            }
          );
        }
      },
      //failure
      function(xhr) {
        console.log('error has no school');
        return;
      }
    );
  }
}


$(document).ready(function(){
  ensureSignedIn();
  userInfo();
  // make sure they're signed in every 10 seconds
  setInterval(function(){
    ensureSignedIn();
    userInfo();
  }, 1000);
});




