"use strict"


function addSignInOutFeedEntry(encounter)
{
  var table = document.getElementById('recent-activity-table');
  table.insertRow(0).innerHTML=
    ('<tr>' +
    '<td>' + encounter.user.name + '</td>' +
    '<td>' + encounter.type + '</td>' +
    '<td>' + moment(encounter.time).fromNow() + '</td>' +
    '<td>' + encounter.location.name + '</td>' +
    '</tr>');
}

function clearFeed()
{
  document.getElementById('recent-activity-table').innerHTML = '';
}

//gets new data from server and inserts it at the beginning
function updateFeed() {
  var url = thisUrl()+'/encounter/?count=100' +
    '&managerId='+Cookies.getJSON('apiKey').user.id+
    '&apiKey='+Cookies.getJSON('apiKey').key;
  request(url,
    function(xhr){
      var encounters = JSON.parse(xhr.responseText);
      clearFeed();
      //go backwards to maintain order
      for(var i = encounters.length-1; i >= 0; i--) {
        addSignInOutFeedEntry(encounters[i]);
      }
    },
    function(xhr) {
      console.log(xhr);
    }
  );
}

//actually sends http request to server
function newEncounter(userId, locationId, type) {
  var url = thisUrl()+
    '/encounter/new/?userId='+ userId+
    '&locationId='+ locationId+
    '&type='+type+
    '&apiKey='+Cookies.getJSON('apiKey').key;
  request(url,
    function(xhr){},
    function(xhr){});
}


//submits encounter to server and then refreshes the screen
function sendEncounter(id) {
  var checkBox = document.getElementById('sign-in-or-out-checkbox');
  newEncounter(id, 1, checkBox.checked ? 'out' : 'in');
  setTimeout(function() {
    updateFeed();
  }, 50);
}

function submitEncounter() {
  var textBox = document.getElementById('user-id-textbox');
  sendEncounter(textBox.value);
}


function grayOutOrangeButton(element) {
  element.classList().remove("deep-orange");
  element.classList().add("gray");
}

function orangeGrayButton(element) {
  element.classList().remove("gray");
  element.classList().add("deep-orange");
}

$(document).ready(function () {
  // display username
  displayUsername();
  //get data at page load
  updateFeed();
  //Initialize scanner selector
  $(document).scannerDetection(function(e, data) {
    sendEncounter(e);
  });
});


//update every second
setInterval(function(){
  updateFeed();
}, 1000);

// make sure they're signed in every 10 seconds
setInterval(function(){
  ensureSignedIn();
}, 10000);

//first make sure we're signed in
ensureSignedIn();
