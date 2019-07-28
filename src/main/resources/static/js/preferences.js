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
    //Gets the right item, by class of element above and onclick value
    document.querySelector('.'+prefNameToClass[prefName]
      +" a[onclick=\"changePref('"+prefName+"', '"+pref+"')"+'"]').classList.add('active');
  };

  $('.nav-link:not(.tab):not(.active)').addClass('text-dark').addClass('palette-border');
});

// Sets the preferences cookie and updates the server
function setPrefs(prefs) {
  var apiKey = Cookies.getJSON('apiKey');

  var url = thisUrl() + '/user/updatePrefs/' +
    '?userId=' + encodeURIComponent(apiKey.user.id) +
    '&prefstring=' + encodeURIComponent(JSON.stringify(prefs)) +
    '&apiKey=' + encodeURIComponent(apiKey.key);

  request(url,
    //success
    function() {
      Cookies.set('prefs', prefs);
      document.location.reload();
    },
    //failure
    function() {
      alert('Failed to send preferences to server')
    }
  );
}

function changePref(prefName, value) {
  var prefs = Cookies.getJSON('prefs');
  prefs[prefName] = value;
  setPrefs(prefs);
}
