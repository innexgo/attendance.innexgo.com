"use strict"

function openSidebar() {
  document.getElementById("sidebar").style.width = "20%";
  document.getElementById("overlay").style.display = "block"
}

function closeSidebar() {
  document.getElementById("sidebar").style.width = "0%";
  document.getElementById("overlay").style.display = "none";
}

document.addEventListener("DOMContentLoaded", function(event) { 

  var apiKey = Cookies.getJSON('apiKey');
  var schedule = Cookies.getJSON('apiKey');
  var sidebar = "fixed";
  var colour = "dark";
  document.getElementById('info-username').innerHTML = ("Hi, "+apiKey.user.name);

  var i = 0;
  // Easier to manipulate later.
  var sidebarLinks = document.getElementsByClassName('sidebar-link');
  for (i = 0; i < sidebarLinks.length; i++ ) {
    sidebarLinks[i].className += ' list-group-item list-group-item-action';
  }
  
  switch(sidebar){
    case "collapsable":
      document.getElementById('sidebar').className += " sidebar-collapsable";
    break;

    case "fixed":
      document.getElementById('sidebar').className += " sidebar-fixed";

      var cardDecks = document.getElementsByClassName('card-deck');
      for (i = 0; i < cardDecks.length; i++ ) {
        cardDecks[i].style = 'margin: 1em; margin-left:20%;';
      }

      var sidebarButtons = document.getElementsByClassName('sidebar-button');
      for (i = 0; i < sidebarButtons.length ; i++ ) {
        sidebarButtons[i].parentNode.removeChild(sidebarButtons[i]);
      }
    break;
  }

  var navBarItems = document.getElementsByClassName('navbar-palette');
  switch(colour){

    case "dark":
      var brandImage = document.createElement('img');
      brandImage.src = "../assets/innexo_logo.png";

      document.getElementById('navbar-brand').appendChild(brandImage);
      document.getElementById('navbar-brand').append(" Innexo");
      
      for (i = 0; i < navBarItems.length; i++ ) {
        navBarItems[i].classList.add('sidebar-dark');
      }

    break;

    case "light":
      var brandImage = document.createElement('img');
      brandImage.src = "../assets/innexo_logo_dark.png";

      document.getElementById('navbar-brand').appendChild(brandImage);
      document.getElementById('navbar-brand').append(" Innexo");

      for (i = 0; i < navBarItems.length; i++ ) {
        navBarItems[i].classList.add('sidebar-light');
      }
    break;
  }
});
