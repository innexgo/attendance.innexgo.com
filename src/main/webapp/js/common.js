"use strict"

$(document).ready(function(){
  var colour = "dark";
  var sidebarStyle = "augmented"
  var palette = document.createElement("link");
  palette.rel = "stylesheet"
  palette.type = "text/css"
  switch(colour){
    case "dark":
      palette.href = "../css/palettes/dark.css?version=1.1";
    break;
    case "light":
      palette.href = "../css/palettes/light.css?version=1";
    break;
  }
  document.getElementsByTagName('head')[0].appendChild(palette);
});

function isEmpty(str) {
    return (!str || 0 === str.length);
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


// TODO later need to actually set

var period_dbg = 1;
var periodStartTime_dbg = 1;
function getPeriod(time) {
  return period_dbg
}

// TODO debugging only
function setPeriod(period) {
  period_dbg = period;
}

function getPeriodStart(periodNum) {
  return periodStartTime_dbg;
}

// TODO debugging only
function setPeriodStart(periodTime) {
  periodStartTime_dbg = periodTime;
}
