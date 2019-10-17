//load palette

function validPrefs() {
  var isValid = true;
  try {
    var a = JSON.parse(Cookies.get('prefs'));
    var colours = ['dark', 'default', 'blue'];
    if (!colours.includes(a.colourTheme)) {
      console.log('colourPrefError');
      throw Error('Invalid Prefs');
    }
  } catch (e) {
    isValid = false;
  }
  try {
    var styles = ['collapsed', 'fixed'];
    if (!styles.includes(a.sidebarStyle)) {
      console.log('stylePrefError')
      throw Error('Invalid Prefs');
    }
  } catch (e) {
    isValid = false;
  }
  return isValid;
}

function loadPref() {
  var prefs = Cookies.getJSON('prefs');
  var colour = prefs.colourTheme;
  var palette = document.createElement("link");

  palette.rel = "stylesheet"
  palette.type = "text/css"

  switch (colour) {
    case "default":
      palette.href = "../css/palettes/default.css?version=1.0";
      break;
    case "dark":
      palette.href = "../css/palettes/dark.css?version=1.5";
      break;
    case "light":
      palette.href = "../css/palettes/light.css?version=1.1";
      break;
    case "blue":
      palette.href = "../css/palettes/blue.css?version=1.2";
      break;
  }
  document.getElementsByTagName('head')[0].appendChild(palette);
};

$(document).ready(function () {
  if (validPrefs()) {
    loadPref();
  } else {
    console.log('reset theme');
    Cookies.set(
      'prefs',
      {
        colourTheme: 'default',
        sidebarStyle: 'fixed'
      }
    )
    loadPref();
  }
});
