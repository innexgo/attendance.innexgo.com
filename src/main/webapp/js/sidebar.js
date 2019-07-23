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

  document.getElementById("sidebar").style.width = "20%";
  document.getElementById("sidebar").style.zIndex = 1;
  document.getElementById("sidebar").style.position = "fixed";
  document.getElementById("sidebar").style.margin = "0px";
  document.getElementById("overlay").style.display = "none";
  $('.card-deck').css('margin-left', '20%');
  $('.sidebar-button').remove()
  var apiKey = Cookies.getJSON('apiKey');
  var schedule = Cookies.getJSON('apiKey');
  var colour = "dark"
  document.getElementById('info-username').innerHTML = ("Hi, "+apiKey.user.name);

  $('.sidebar-link').addClass('list-group-item').addClass('list-group-item-action');

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

// function displayInfo() {
//   var apiKey = Cookies.getJSON('apiKey');

//   document.getElementById('info-username').innerHTML = apiKey.user.name;

//   var url = thisUrl() + '/schedule/' +
//     '?userId='+ encodeURIComponent(apiKey.user.id) +
//     '&period=' + encodeURIComponent(lookupPeriod(new Date())) +
//     '&apiKey=' + encodeURIComponent(apiKey.key)

//   request(url,
//     //success
//     function(xhr) {
//       var schedules = JSON.parse(xhr.responseText);
//       var currentPeriod = lookupPeriod(new Date());
//       var locstr = 'Unknown Location';
//       document.getElementById('info-period').innerHTML = ordinal_suffix_of(currentPeriod) + ' Period';
//       for(var i = 0; i < schedules.length; i++) {
//         if(schedules[i].period == currentPeriod) {
//           locstr = schedules[i].location.name;
//         }
//       }
//       document.getElementById('info-location').innerHTML = locstr;
//     },
//     // failure
//     function(xhr) {
//       return;
//     }
//   );
// }


