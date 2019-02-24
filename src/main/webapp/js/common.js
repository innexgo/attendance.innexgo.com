"use strict"

function isWhitespace(str) {
  return /\S/.test(myString);
}

function isValidString(str) {
  return str !== 'undefined' && !isWhitespace(str);

function thisUrl(){
  return window.location.protocol  + "//" + window.location.host;
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

//Get the Sidebar
var mySidebar = document.getElementById("mySidebar");

//Get the DIV with overlay effect
var overlayBg = document.getElementById("myOverlay");

//Toggle between showing and hiding the sidebar, and add overlay effect
function w3_open() {
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
	mySidebar.style.display = "none";
	overlayBg.style.display = "none";
}
