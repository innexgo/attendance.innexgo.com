"use strict"

/* global Cookies isEmpty */

$(document).ready(function () {
  const idToPref = {
    "color-scheme": "colorScheme",
  }

  let prefs = Cookies.getJSON('prefs');
  let options = $('ul.segmented-buttons li');

  for (let option of options) {
    let prefName = prefs[
      idToPref[
      $(option)
        .parent()[0]
        .id]
    ];

    if (!(isEmpty(option.id))) {
      if (option.id == prefName) {
        option.classList.add('selected');
      }
    }
    else {
      if (option.innerHTML.toLowerCase() == prefName) {
        option.classList.add('selected');
      }
    }
  }


  $('.segmented-buttons').click(function (event) {

    let innerElements = $('li', this);
    let selectedElement = innerElements.filter(event.target)[0];

    innerElements
      .removeClass('selected')
      .filter(event.target)
      .addClass('selected');

    let prefVal = isEmpty(selectedElement.id)
      ? selectedElement.innerHTML.toLowerCase()
      : selectedElement.id;

    changePref(
      idToPref[this.id],
      prefVal
    );
  });
});

function changePref(prefName, value) {
  var prefs = Cookies.getJSON('prefs');
  prefs[prefName] = value;
  console.log(prefs);
  Cookies.set('prefs', prefs);
  document.location.reload();
}
