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
  if (!isBlank(minField.value) && !isBlank(maxField.value)) {
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
  var minDate = new Date(document.getElementById("min-date").value+"T"+document.getElementById("min-time").value+":00");
  var maxDate = new Date(document.getElementById("max-date").value+"T"+document.getElementById("max-time").value+":00");
  var count = 100;
  submitQuery(encounterId, userId, userName, locationId, type, minDate, maxDate, count);
}

function normTimestamp(time){
  return moment(time).format("MM/DD/YYYY HH:mm");
}

function addQueryEntry(encounter) {
  var table = document.getElementById('result-table');
  var signInOrSignOutText = encounter.type == 'in' ?
    '<i class="fa fa-sign-in text-red"></i> Signed-In' :
    '<i class="fa fa-sign-out text-blue"></i> Signed-Out';
  if(table.rows.length < 1) {
    clearResultTable();
  }
  table.insertRow(1).innerHTML=

    ('<tr>' +
      '<td>' + encounter.user.name+ '</td>' +
      '<td>' + signInOrSignOutText + '</td>' +
      '<td>' + encounter.location.name + '</td>' +
      '<td>' + normTimestamp(encounter.time) + '</td>' +
      '</tr>');
}

function clearResultTable() {
  document.getElementById('result-table').innerHTML =
    '<tr class="dark-gray">'+
    '<td>Name</td>'+
    '<td>In/Out</td>'+
    '<td>Location</td>'+
    '<td>Time</td>'+
    '</tr>';
}

//gets new data from server and inserts it at the beginning
function submitQuery(encounterId, userId, userName, locationId, type, minDate, maxDate, count) {
  var url = thisUrl() + '/encounter/?apiKey=' + Cookies.getJSON('apiKey').key +
    (isNaN(encounterId) ?       '' : '&encounterId='+encounterId) +
    (isNaN(userId) ?            '' : '&userId='+userId) +
    (isBlank(userName) ?        '' : '&userName='+userName) +
    (isNaN(locationId) ?        '' : '&locationId='+locationId) +
    (isBlank(type) ?            '' : '&type='+encodeURIComponent(type)) +
                                     '&minTime='+moment(minDate).unix() +
                                     '&maxTime='+moment(maxDate).unix() +
    (isNaN(count) ?             '' : '&count='+count);
  request(url,
    function(xhr){
      var encounters = JSON.parse(xhr.responseText);
      clearResultTable();
      for(var i = 0; i < encounters.length; i++) {
        addQueryEntry(encounters[i]);
      }
    },
    function(xhr)
    {
      console.log(xhr);
    }
  );
}

// make sure they're signed in every 10 seconds
setInterval(function(){
  ensureSignedIn();
}, 10000);
ensureSignedIn();
