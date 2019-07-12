"use strict"



function isWhitespace(str) {
  return !(/\S/.test(str));
}

function isValidString(str) {
  return typeof(str) !== 'undefined' && !isWhitespace(str);
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

function timeSince(d) {
  var seconds = Math.floor((Date.now() - Date.parse(d)) / 1000);
  var interval = Math.floor(seconds / 31536000);

  if (interval > 1) {
    return interval + " years";
  }
  interval = Math.floor(seconds / 2592000);
  if (interval > 1) {
    return interval + " months";
  }
  interval = Math.floor(seconds / 86400);
  if (interval > 1) {
    return interval + " days";
  }
  interval = Math.floor(seconds / 3600);
  if (interval > 1) {
    return interval + " hours";
  }
  interval = Math.floor(seconds / 60);
  if (interval > 1) {
    return interval + " minutes";
  }
  return Math.floor(seconds) + " seconds";
}

function epochTime(d) {
  return Math.floor(d.getTime()/1000);
}

function getDateString(d) {
  var date = new Date(Date.parse(d))
  return date.toLocaleTimeString('en-US') + " on " + date.toDateString();
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

//Toggle between showing and hiding the sidebar, and add overlay effect
function w3_open() {

  //Get the Sidebar
  var mySidebar = document.getElementById("mySidebar");

  //Get the DIV with overlay effect
  var overlayBg = document.getElementById("myOverlay");

  if (mySidebar.style.display === 'block') {
    mySidebar.style.display = 'none';
    overlayBg.style.display = "none";
  } else {
    mySidebar.style.display = 'block';
    overlayBg.style.display = "block";
  }
}

//Close the sidebar with the close button
function w3_close() {
  var mySidebar = document.getElementById("mySidebar");
  var overlayBg = document.getElementById("myOverlay");
  mySidebar.style.display = "none";
  overlayBg.style.display = "none";
}

// moves to login page on cookie expiration
function ensureSignedIn() {
  // check if sign in cookie exists and is logged in
  var expiry = Cookies.get('apiKeyExpirationTime');
  if(expiry == null) {
    window.location.replace(thisUrl() + "/login.html");
    return;
  }

  // now check if the cookie is expired
  if(expiry < epochTime(new Date())) {
    alert("Session has expired");
    window.location.replace(thisUrl() + "/login.html");
    return;
  }

  // make test request, on failure delete the cookies 
  // usually means something went wrong with server
  var url = thisUrl() + "/location/?apiKey=" + Cookies.get('apiKey');
  request(url,
    // on success
    function(xhr) {
      console.log("user is signed in");
    },
    // on failure
    function(xhr) {
      alert("Current session invalid");
      console.log("user is not signed in");
      Cookies.remove('apiKey');
      Cookies.remove('apiKeyExpirationTime');
      Cookies.remove('userName');
      Cookies.remove('userId');
      window.location.replace(thisUrl() + "/login.html");
    }
  );
}
