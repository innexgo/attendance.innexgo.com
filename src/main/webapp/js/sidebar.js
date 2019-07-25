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
  var displayedInfo = "augmented"

  $('.sidebar-item').addClass('list-group-item').addClass('list-group-item-action');

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

  switch(displayedInfo){
    case "augmented":
      
    break;

    case "slim":
      palette.href = "../css/palettes/light.css?version=1";
    break;
  }

});

function displayInfo() {
  var apiKey = Cookies.getJSON('apiKey');
  var schedule = Cookies.getJSON('schedule');

  document.getElementById('info-time').innerHTML = moment().format('dddd, MMMM D');
  document.getElementById('info-name').innerHTML = schedule.user.name;
  document.getElementById('info-period').innerHTML = 'Period ' + schedule.period;
  document.getElementById('info-location').innerHTML = schedule.location.name;
}

$(document).ready(function() {
  displayInfo();
  setInterval(displayInfo, 10000);
})
