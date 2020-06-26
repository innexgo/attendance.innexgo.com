'use strict';

/* global moment */

const INT32_MAX = 0xffffffff;

/**
 * Returns a promise that will be resolved in some milliseconds
 * use await sleep(some milliseconds)
 * @param {int} ms milliseconds to sleep for
 * @return {Promise} a promise that will resolve in ms milliseconds
 */
function sleep(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

/**
 * Checks if a string is null or has no length
 * @param {String} str the string to check
 * @return {boolean} false if the string is null or it has zero length
 */
function isEmpty(str) {
  return (!str || 0 === str.length);
}

function staticUrl() {
  return window.location.protocol + "//" + window.location.host;
}

function apiUrl() {
  return staticUrl() + '/api';
}

function escapeHtml(unsafe) {
  return unsafe
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
}

function readableTimestamp(ms) {
  let diff = Date.now() - ms;
  if(diff < 0) {
    return moment(ms).format('h:mma M/D/YY');
  } else if(diff < 30*1000) {
    return 'a few seconds ago';
  } else if (diff < 60*1000) {
    return 'less than a minute ago';
  } else if (diff < 15*60*1000) {
    return `${Math.round(diff/(60*1000))} minutes ago`;
  } else {
    return moment(ms).format('h:mma M/D/YY');
  }
}

function standardizeString(unsafe) {
  return unsafe.trim().replace(/[^\w\s]/gi, '').toUpperCase();
}

function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
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

var alertCounter = 0;
function giveAlert(innerHTML, type, permanent) {
  var alertId = 'alert' + alertCounter;

  document.getElementById('alert-zone').innerHTML +=
    `<div id="${alertId}" class="alert alert-dismissable ${type}" role="alert">
        ${innerHTML}
        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
    </div>`;

  if (!permanent) {
    $('#' + alertId).fadeTo(3000, 500).slideUp(500, function () {
      $('#' + alertId).slideUp(500);
      this.parentNode.removeChild(this);
    });
  }
  alertCounter++;
}

function givePermError(innerHTML) {
  giveAlert(innerHTML, 'alert-danger', true);
}

function giveTempError(innerHTML) {
  giveAlert(innerHTML, 'alert-danger', false);
}

function giveTempSuccess(innerHTML) {
  giveAlert(innerHTML, 'alert-success', false);
}

function givePermSuccess(innerHTML) {
  giveAlert(innerHTML, 'alert-success', true);
}

function giveTempInfo(innerHTML) {
  giveAlert(innerHTML, 'alert-info', false);
}

function linkAbsolute(text, url) {
  return '<a style="display: inline-block;" href="' + url + '">' + text + '</a>';
}

function linkRelative(text, url) {
  return linkAbsolute(text, staticUrl() + url);
}

// Fetches json given a URL
async function fetchJson(url, params={}) {
  let response = await fetch(url, params);
  if (!response.ok) {
    throw Error(await response.text());
  }
  return response.json();
}
