"use strict"
$(document).ready(function () {
  $('.segmented-buttons').click(function (event) {

    var target = $('li', this).filter(event.target);

    target.hasClass('selected') ? target.removeClass('selected') : target.addClass('selected')
  });
});