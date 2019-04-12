"use strict"

function setCookie(cname, cval) {
  document.cookie = cname + "=" + cvalue + "; path=/";
}

function getCookie(cname) {
  var name = cname + "=";
  var ca = document.cookie.split(';');
  for(var i = 0; i < ca.length; i++) {
    var c = ca[i];
    while (c.charAt(0) == ' ') {
      c = c.substring(1);
    }
    if (c.indexOf(name) == 0) {
      return c.substring(name.length, c.length);
    }
  }
  return "";
}

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

function timeSince(date) {
  var seconds = Math.floor((new Date().getTime() - Date.parse(date)) / 1000);
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


function getDateString(d) {
  var date = new Date(Date.parse(d));
  return date.toLocaleTimeString('en-US') + " on " + date.toDateString();
}


function request(url, functionOnLoad, functionOnError) {
  var xhr = new XMLHttpRequest();
  xhr.open('POST', url, true);
  xhr.onload = function() {
    if (xhr.readyState == 4 && xhr.status == 200) {
      functionOnLoad(xhr);
    } else if(xhr.readyStat == 4 && xhr.status != 200) {
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
