/* global Cookies */
"use strict"

function validPrefs() {
  var isValid = true;
  try {
    var prefcookie = JSON.parse(Cookies.get('prefs'));
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
  var prefs = Cookies.getJSON('prefs');
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
    Cookies.set(
      'prefs',
      {
        colorScheme: 'default',
      }
    )
    loadPref();
  }
});
