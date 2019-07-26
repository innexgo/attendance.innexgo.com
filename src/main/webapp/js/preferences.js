"use strict"

$(document).ready(function(){
  var prefs = Cookies.getJSON('prefs');
  const prefNameToClass = {
    colourTheme: "colour-theme",
    sidebarStyle: "sidebar-style",
    sidebarInfo: "sidebar-info",
  };

  $('.active:not(.tab)').removeClass('active');

  for (var [prefName, pref] of Object.entries(prefs)) {
    try {
      //Gets the right item, by class of element above and onclick value
      document.querySelector('.'+prefNameToClass[prefName]+" a[onclick=\"changePref('"+prefName+"', '"+pref+"')"+'"]').classList.add('active');
    }
    catch (TypeError) {
      document.querySelector('.'+prefNameToClass[prefName]+" a[onclick=\"changePref('"+prefName+"','"+pref+"')"+'"]').classList.add('active');
    }
  };

  $('.nav-link:not(.tab):not(.active)').addClass('text-dark').addClass('palette-border');
});

function changePref(whichPref, style) {
  var prefs = Cookies.getJSON('prefs');
  var apiKey = Cookies.getJSON('apiKey');

  prefs[whichPref] = style;

  var url = thisUrl() +
    '/user/updatePrefs/?apiKey=' + encodeURIComponent(apiKey.key) +
    '&prefstring=' + encodeURIComponent(JSON.stringify(prefs)) +
    '&userId=' + encodeURIComponent(apiKey.user.id);

  request(url, function() {
      Cookies.set('prefs', prefs);
      document.location.reload();
    },
    alert('Failed to send preferences to server')
  );
}