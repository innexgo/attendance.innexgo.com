"use strict"


function addSignInOutFeedEntry(isSignIn, userName, userId, placeName, timestamp)
{
  var table = document.getElementById(isSignIn ? "sign-in-feed" : "sign-out-feed");
  var signInOrSignOutText = isSignIn ? 'in to' : 'out of';
  if(table.rows.length < 1) {
    clearFeed();
  }
  table.insertRow(1).innerHTML=
    ('<tr>' +
    '<td>' + userName + '</td>' +
    '<td>' + userId  + '</td>' +
    '<td>' + getDateString(timestamp) + '</td>' +
    '</tr>');
}

function clearFeed()
{
  document.getElementById('sign-in-feed').innerHTML =
            '<tr class="dark-gray">' +
              '<td>Name</td>' +
              '<td>ID</td>' +
              '<td>Time</td>' +
            '</tr>';
  document.getElementById('sign-out-feed').innerHTML =
            '<tr class="dark-gray">' +
              '<td>Name</td>' +
              '<td>ID</td>' +
              '<td>Time</td>' +
            '</tr>';
}

//gets new data from server and inserts it at the beginning
function updateFeed() {
  var url = thisUrl()+'/encounter/?count=100' +
    '&apiKey='+Cookies.get('apiKey');
  request(url,
    function(xhr){
      var encounters = JSON.parse(xhr.responseText);
      clearFeed();
      //go backwards to maintain order
      for(var i = encounters.length-1; i >= 0; i--) {
        addSignInOutFeedEntry(encounters[i].type == 'in',
          encounters[i].user.name,
          encounters[i].user.id,
          encounters[i].location.name,
          encounters[i].time);
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
    '&apiKey='+Cookies.get('apiKey');
  console.log('making request to: ' + url);
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
  //Initialize scanner selector
  $(document).scannerDetection(function(e, data) {
    sendEncounter(e);
  });
});


//update every second
setInterval(function(){
  updateFeed();
  console.log('updating feed');
}, 1000);

// make sure they're signed in every 10 seconds
setInterval(function(){
  ensureSignedIn();
}, 10000);

//first make sure we're signed in
ensureSignedIn();
//get data at page load
updateFeed();
