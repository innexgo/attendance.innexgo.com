"use strict"

document.addEventListener("DOMContentLoaded", function(event) { 
  var colour = "dark";
  var palette = document.createElement("link");
  palette.rel = "stylesheet"
  palette.type = "text/css"
  switch(colour){
    case "dark":
      palette.href = "../css/palettes/dark.css";
    break;
    case "light":
      palette.href = "../css/palettes/light.css";
    break;
  }
  document.getElementsByTagName('head')[0].appendChild(palette);
});