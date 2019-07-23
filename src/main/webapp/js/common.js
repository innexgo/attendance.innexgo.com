"use strict"

function openSidebar() {
  document.getElementById("sidebar").style.width = "250px";
  document.getElementById("overlay").style.display = "block"
}

function closeSidebar() {
  document.getElementById("sidebar").style.width = "0";
  document.getElementById("overlay").style.display = "none";
}


function isBlank(str) {
  return str == null || str=="";
}

function thisUrl(){
  return window.location.protocol  + "//" + window.location.host;
}

function escapeHtml(unsafe) {
    return unsafe
         .replace(/&/g, "&amp;")
         .replace(/</g, "&lt;")
         .replace(/>/g, "&gt;")
         .replace(/"/g, "&quot;")
         .replace(/'/g, "&#039;");
 }

function request(url, functionOnLoad, functionOnError) {
  var xhr = new XMLHttpRequest();
  xhr.open('GET', url, true);
  xhr.onload = function() {
    if (xhr.readyState == 4 && xhr.status == 200) {
      functionOnLoad(xhr);
    } else if(xhr.readyState == 4 && xhr.status != 200) {
      functionOnError(xhr);
    }
  };
  xhr.send();
}

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

function ordinal_suffix_of(i) {
    var j = i % 10,
        k = i % 100;
    if (j == 1 && k != 11) {
        return i + "st";
    }
    if (j == 2 && k != 12) {
        return i + "nd";
    }
    if (j == 3 && k != 13) {
        return i + "rd";
    }
    return i + "th";
}


// TODO later need to do request
function lookupPeriod(date) {
  return 1
}
