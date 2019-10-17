"use strict"

$(document).ready(function(){
  const idToPref = {
    "color-scheme": "colorScheme",
  }

  var prefs = Cookies.getJSON('prefs');
  var options = $('ul.segmented-buttons li');

  for (var option of options) {
    var prefName = prefs[
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

    var innerElements = $('li', this);
    var selectedElement = innerElements.filter(event.target)[0];

    innerElements
      .removeClass('selected')
      .filter(event.target)
      .addClass('selected');

    if (!(isEmpty(selectedElement.id))) {
      var prefVal = selectedElement.id;
    }
    else {
      var prefVal = selectedElement.innerHTML.toLowerCase();
    }
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
