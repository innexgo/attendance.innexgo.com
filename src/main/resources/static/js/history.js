"use strict"

window.onload = function() {
  var ID = document.getElementById("ID-input");
  var name = document.getElementById("name-input");

  name.addEventListener("keydown", function(event) {
    if (event.keyCode === 13) {
      event.preventDefault();
      onQueryClick();
    }
  });

  ID.addEventListener("keydown", function(event) {
    if (event.keyCode === 13) {
      event.preventDefault();
      onQueryClick();
    }
  });
}

$(document).ready(function(){
  var minDateField = document.getElementById("min-date");
  var maxDateField = document.getElementById("max-date");

  var minTimeField = document.getElementById("min-time");
  var maxTimeField = document.getElementById("max-time");

  var calcDate = moment().format("YYYY-MM-DD");

  minDateField.value = calcDate;
  maxDateField.value = calcDate;

  $("#min-date").change(function() {
    checkValidRange("minField", minDateField, maxDateField, "date");
  });

  $("#max-date").change(function() {
    checkValidRange("maxField", minDateField, maxDateField, "date");
  });

  $("#min-time").change(function() {
    if(minDateField.value == maxDateField.value) {
      checkValidRange("minField", minTimeField, maxTimeField, "time");
    }
  });

  $("#max-time").change(function() {
    if(minDateField.value == maxDateField.value) {
      checkValidRange("maxField", minTimeField, maxTimeField, "time");
    }
  });
});

var calcDate = moment().format("YYYY-MM-DD");

function checkValidRange(which, minField, maxField, type){
  if (!isEmpty(minField.value) && !isEmpty(maxField.value)) {
    if (minField.value > maxField.value) {
      switch (which) {
        case "minField":
          maxField.value = minField.value;
          break;

        case "maxField":
          minField.value = maxField.value;
          break;
      }
    }
    if (type == "date"){
      if ((maxField.value > calcDate) || (minField.value > calcDate)) {
        maxField.value = calcDate;
        minField.value = calcDate;
      }
      if (minField.value == maxField.value) {
        checkValidRange("maxField", minTimeField, maxTimeField, "time");
      }
    }
  }
}

function onQueryClick() {
  var encounterId = undefined; //TODO add query box
  var userId = parseInt(document.getElementById("ID-input").value, 10);
  var userName = document.getElementById("name-input").value;
  var locationId = undefined;//document.getElementById('locationId').value;
  var type = undefined;
  var minDate = new Date(document.getElementById("min-date").value+"T"+document.getElementById("min-time").value+":00"); //TODO there's gotta be a better way then this
  var maxDate = new Date(document.getElementById("max-date").value+"T"+document.getElementById("max-time").value+":00");
  var count = 100;
  submitQuery(encounterId, userId, userName, locationId, type, minDate, maxDate, count);
}

function normTimestamp(time){
  return moment(time).format("MM/DD/YYYY HH:mm");
}

//gets new data from server and inserts it at the beginning
function submitQuery(encounterId, userId, userName, locationId, type, minDate, maxDate, count) {
  var apiKey = Cookies.getJSON('apiKey');
  var course = Cookies.getJSON('course');

  if(apiKey == null || course == null) {
    console.log('not enough cookies for recentActivity');
    return;
  }

  var url = thisUrl() + '/encounter/?apiKey=' + Cookies.getJSON('apiKey').key +
    (isNaN(encounterId) ?       '' : '&encounterId='+encounterId) +
    (isNaN(userId) ?            '' : '&userId='+userId) +
    (isEmpty(userName) ?        '' : '&userName='+userName) +
    (isNaN(locationId) ?        '' : '&locationId='+locationId) +
    (isEmpty(type) ?            '' : '&type='+encodeURIComponent(type)) +
    (isEmpty(minDate) ?         '' : '&minTime='+moment(minDate).unix()) +
    (isEmpty(maxDate) ?         '' : '&maxTime='+moment(maxDate).unix()) +
    (isNaN(count) ?             '' : '&count='+count);
  request(url,
    function(xhr){
      // clear table
      var table = document.getElementById('history-table');
      table.innerHTML = '';

      var encounters = JSON.parse(xhr.responseText);
      //go backwards to maintain order
      for(var i = encounters.length-1; i >= 0; i--) {
        var encounter = encounters[i];
        table.insertRow(0).innerHTML=
          ('<tr>' +
            '<td>' + encounter.student.name + '</td>' +
            '<td>' + encounter.type + '</td>' +
            '<td>' + moment.unix(encounter.time).fromNow() + '</td>' +
            '<td>' + encounter.location.name + '</td>' +
            '</tr>');
      }
    },
    function(xhr) {
      console.log(xhr);
    }
  );
}
