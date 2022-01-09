"use strict"

/* global 
 moment getLocalJson
 linkRelative
 */

function displayInfo() {
  let info_time = document.getElementById('info-time');
  let info_period = document.getElementById('info-period');
  let info_location = document.getElementById('info-location');


  info_time.innerHTML = moment().format('dddd (MM/DD/YYYY)');

  let period = getLocalJson('period');
  let courses = getLocalJson('courses');
  if(period == null || courses == null) {
    document.getElementById('info-period').innerHTML = '';
    document.getElementById('info-location').innerHTML = '';
    return;
  }

  if(period.kind == 'CLASS') {
    document.getElementById('info-period').innerHTML = 'Period ' + period.numbering;
  } else {
    document.getElementById('info-period').innerHTML = period.kind;
  }

  let course = courses.filter(c => c.period == period.numbering)[0];

  if(course == null) {
    document.getElementById('info-location').innerHTML = '';
    return;
  }

  document.getElementById('info-location').innerHTML =
    linkRelative(course.location.name, '/locationprofile.html?locationId=' + course.location.id);
}

$(document).ready(function () {
  let apiKey = getLocalJson('apiKey');
  let prefs = getLocalJson('prefs');

  // Set name
  document.getElementById('info-name').innerHTML = linkRelative(apiKey.user.name, '/userprofile.html?userId=' + apiKey.user.id);

  // Set links in the document dependent on user permissions
  if (apiKey.user.ring == 0) {
    document.getElementById('my-overview').href = '/adminoverview.html';
  } else {
    document.getElementById('my-overview').href = '/overview.html';
  }

  // Add sidebar tags
  let sidebarItems = $('.sidebar-item').addClass('list-group-item');
  sidebarItems.not('.sidebar-info-list').addClass('list-group-item-action');

  // Now setup color
  let sidebar = prefs.sidebarStyle;
  let color = prefs.colorScheme;

  // Post image on top
  let brandImage = document.createElement('img');
  brandImage.src = '/img/innexgo_logo.png';
  $('.navbar-palette').addClass('text-light').addClass('bg-dark');

});

// Repeatedly display signin data
$(document).ready(function () {
  displayInfo();
  setInterval(displayInfo, 10000);
});
