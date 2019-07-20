"use strict"

function displayInfo() {
  var apiKey = Cookies.getJSON('apiKey');

  document.getElementById('info-username').innerHTML = apiKey.user.name;

  var url = thisUrl() + '/schedule/' +
    '?userId='+ encodeURIComponent(apiKey.user.id) +
    '&period=' + encodeURIComponent(lookupPeriod(new Date())) +
    '&apiKey=' + encodeURIComponent(apiKey.key)

  request(url,
    //success
    function(xhr) {
      var schedules = JSON.parse(xhr.responseText);
      var currentPeriod = lookupPeriod(new Date());
      var locstr = 'Unknown Location';
      document.getElementById('info-period').innerHTML = ordinal_suffix_of(currentPeriod) + ' Period';
      for(var i = 0; i < schedules.length; i++) {
        if(schedules[i].period == currentPeriod) {
          locstr = schedules[i].location.name;
        }
      }
      document.getElementById('info-location').innerHTML = locstr;
    },
    // failure
    function(xhr) {
      return;
    }
  );
}


