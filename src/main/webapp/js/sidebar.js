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

  var sidebar = "fixed";
  var colour = "dark";

  $('.sidebar-link').addClass('list-group-item').addClass('list-group-item-action');

  switch(sidebar){
    case "collapsable":
      $("#sidebar").addClass("sidebar-collapsable");
    break;

    case "fixed":
      $("#sidebar").addClass("sidebar-fixed");
      $('.card-deck').css('margin-left', '20%');
      $('.sidebar-button').remove();
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

      $('.navbar-palette').addClass('text-dark').addClass('sidebar-light')
    break;
  }
});

function displayInfo() {
  var apiKey = Cookies.getJSON('apiKey');
  var course = Cookies.getJSON('course');

  document.getElementById('info-time').innerHTML = moment().format('dddd, MMMM D');
  document.getElementById('info-name').innerHTML = apiKey.user.name;
  if(course != null) {
    document.getElementById('info-period').innerHTML = 'Period ' + course.period;
    document.getElementById('info-location').innerHTML = course.location.name;
  } else {
    document.getElementById('info-period').innerHTML = '';
    document.getElementById('info-location').innerHTML = '';
  }
}

$(document).ready(function() {
  displayInfo();
  setInterval(displayInfo, 10000);
})
