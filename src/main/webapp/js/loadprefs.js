/* global getLocalJson */
"use strict"

function validPrefs() {
  var isValid = true;
  try {
    var prefcookie = getLocalJson('prefs');
    var validColorSchemes = ['dark', 'default', 'blue'];
    if (!validColorSchemes.includes(prefcookie.colorScheme)) {
      console.log('BAD COLORSCHEME');
      throw Error('Invalid Prefs');
    }
  } catch (e) {
    isValid = false;
  }
  return isValid;
}

function loadPref() {
  var prefs = getLocalJson('prefs');
  var colorScheme = prefs.colorScheme;
  var palette = document.createElement("link");

  palette.rel = "stylesheet"
  palette.type = "text/css"

  switch (colorScheme) {
    case "default":
      palette.href = "../css/palettes/default.css";
      break;
    case "dark":
      palette.href = "../css/palettes/dark.css";
      break;
    case "blue":
      palette.href = "../css/palettes/blue.css";
      break;
  }
  document.getElementsByTagName('head')[0].appendChild(palette);
}

$(document).ready(function () {
  if (validPrefs()) {
    loadPref();
  } else {
    console.log('reset theme');
    setLocalJson(
      'prefs',
      {
        colorScheme: 'default',
      }
    )
    loadPref();
  }
});
