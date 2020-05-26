"use strict"

/* global 
 moment Cookies
 linkRelative
 */

function displayInfo() {
  let apiKey = Cookies.getJSON('apiKey');
  let period = Cookies.getJSON('period');
  let courses = Cookies.getJSON('courses');

  if(apiKey == null) {
    return;
  }

  document.getElementById('#info-name').innerHTML = linkRelative(apiKey.user.name, `/userprofile.html?userId=${apiKey.user.id}`);

  $('#info-time')[0].innerHTML = moment().format('dddd (MM/DD/YYYY)');

  if(period == null || courses == null) {
    document.getElementById('info-period').innerHTML = '';
    document.getElementById('info-location').innerHTML = '';
    return;
  }

  if(period.type == 'Class Period') {
    document.getElementById('info-period').innerHTML = 'Period ' + period.number;
  } else {
    document.getElementById('info-period').innerHTML = period.type;
  }

  let course = courses.filter(c => c.period == period.number)[0];

  if(course == null) {
    document.getElementById('info-location').innerHTML = '';
    return;
  }

  document.getElementById('info-location').innerHTML =
    linkRelative(course.location.name, '/locationprofile.html?locationId=' + course.location.id);
}

// Repeatedly display signin data
$(document).ready(function () {
  displayInfo();
  setInterval(displayInfo, 10000);
});
