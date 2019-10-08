//load palette

function loadPref() {
  var prefs = Cookies.getJSON('prefs');
  var colour = prefs.colourTheme;
  var palette = document.createElement("link");

  palette.rel = "stylesheet"
  palette.type = "text/css"

  switch (colour) {
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
    Cookies.set(
      'prefs',
      JSON.stringify({
        colourTheme: 'dark',
        sidebarStyle: 'fixed'
      })
    )
    loadPref();
  }
});