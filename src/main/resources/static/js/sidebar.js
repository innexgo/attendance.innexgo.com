"use strict"

function openSidebar() {
  document.getElementById("sidebar").style.width = "20%";
  document.getElementById("overlay").style.display = "block"
}

function closeSidebar() {
  document.getElementById("sidebar").style.width = "0%";
  document.getElementById("overlay").style.display = "none";
}

$(document).ready(function(){
  var prefs = Cookies.getJSON('prefs');
  var userType = Cookies.getJSON('apiKey').user.ring;
  var sidebar = prefs.sidebarStyle;
  var colour = prefs.colourTheme;
  var sidebarInfo = prefs.sidebarInfo;

  if (userType == 0) {
    document.getElementById('my-overview').href = 'adminoverview.html';
    document.getElementById('my-managestudent').href = 'adminmanagestudent.html';
  } else {
    document.getElementById('my-overview').href = 'overview.html';
    document.getElementById('my-managestudent').href = 'managestudent.html';
  }

  var sidebarItems = $('.sidebar-item').addClass('list-group-item');
  sidebarItems.not('.sidebar-info-list').addClass('list-group-item-action');

  switch(sidebar){
    case "collapsable":
      $("#sidebar").addClass("sidebar-collapsable");
    break;

    case "fixed":
      $("#sidebar").addClass("sidebar-fixed");
      $('#not-sidebar').css('padding-left', '20%');
      $('.sidebar-button').remove();
      document.getElementById('overlay').remove();
    break;
  }

  switch(colour){
    case "dark":
      var brandImage = document.createElement('img');
      brandImage.src = "../assets/innexo_logo.png";

      document.getElementById('navbar-brand').appendChild(brandImage);
      document.getElementById('navbar-brand').append(" Innexo");

      $('.navbar-palette').addClass('text-light').addClass('bg-dark');
    break;

    case "light":
      var brandImage = document.createElement('img');
      brandImage.src = "../assets/innexo_logo_dark.png";

      document.getElementById('navbar-brand').appendChild(brandImage);
      document.getElementById('navbar-brand').append(" Innexo");

      $('.navbar-palette').addClass('text-dark').addClass('sidebar-light');
    break;

    case "blue":
      var brandImage = document.createElement('img');
      brandImage.src = "../assets/innexo_logo.png";

      document.getElementById('navbar-brand').appendChild(brandImage);
      document.getElementById('navbar-brand').append(" Innexo");

      $('.navbar-palette').addClass('text-light').addClass('sidebar-blue')
  }

  switch(sidebarInfo){
    case "augmented":
    break;

    case "slim":
      $('.sidebar-item.sidebar-info-list').hide();
    break;
  }

});

function displayInfo() {
  var apiKey = Cookies.getJSON('apiKey');
  var course = Cookies.getJSON('course');
  var period = Cookies.getJSON('period');

  document.getElementById('info-time').innerHTML = moment().format('dddd, MMMM D');
  document.getElementById('info-name').innerHTML = apiKey.user.name;

  document.getElementById('info-period').innerHTML =
    period == null ? '' : 'Period ' + period.period;

  document.getElementById('info-location').innerHTML =
    course == null ? '' : course.location.name;
}

$(document).ready(function() {
  displayInfo();
  setInterval(displayInfo, 10000);
})
