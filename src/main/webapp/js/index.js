"use strict"

function thisUrl(){
  return window.location.protocol  + "//" + window.location.host;
}

function timeSince(date) {
  var seconds = Math.floor((new Date().getTime() - Date.parse(date)) / 1000);
  var interval = Math.floor(seconds / 31536000);

  if (interval > 1) {
    return interval + " years";
  }
  interval = Math.floor(seconds / 2592000);
  if (interval > 1) {
    return interval + " months";
  }
  interval = Math.floor(seconds / 86400);
  if (interval > 1) {
    return interval + " days";
  }
  interval = Math.floor(seconds / 3600);
  if (interval > 1) {
    return interval + " hours";
  }
  interval = Math.floor(seconds / 60);
  if (interval > 1) {
    return interval + " minutes";
  }
  return Math.floor(seconds) + " seconds";
}


function getDateString(d) {
  var date = new Date(Date.parse(d));
  return date.toLocaleTimeString('en-US') + " on " + date.toDateString();
}


function request(url, functionOnLoad, functionOnError) {
  var xhr = new XMLHttpRequest();
  xhr.open('POST', url, true);
  xhr.onload = function() {
    if (xhr.readyState == 4 && xhr.status == 200) {
      functionOnLoad(xhr);
    } else if(xhr.readyStat == 4 && xhr.status != 200) {
      functionOnError(xhr);
    }
  };
  xhr.send();
}

function addSignInOutFeedEntry(isSignIn, studentName, userId, placeName, timestamp)
{
  var table = document.getElementById(isSignIn ? "sign-in-feed" : "sign-out-feed");
  var signInOrSignOutText = isSignIn ? 'in to' : 'out of';
  if(table.rows.length < 1) {
    clearFeed();
  }
  table.insertRow(1).innerHTML=
    ('<tr>' + 
    '<td>' + studentName + '</td>' +
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
              '<td>Id</td>' +
              '<td>Time</td>' +
            '</tr>';
}

//gets new data from server and inserts it at the beginning
function updateFeed() {
  request(thisUrl()+'/encounter/', 
    function(xhr){
      var encounters = JSON.parse(xhr.responseText);
      clearFeed();
      for(var i = 0; i < encounters.length; i++) {
        var user = user[1]
        addSignInOutFeedEntry(encounters[i].type == "in", encounters[i].name, encounters[i].id, encounters[i].location.name, encounters[i].time);
      }
    },
    function(xhr) 
    {
      console.log(xhr);
    }
  );
}

//animates the switch to swap depending on whether it's pressed or not
function toggleSignInOrOut() {
  var icon = document.getElementById("sign-in-or-out-icon");
  var checkBox = document.getElementById("sign-in-or-out-checkbox");
  icon.innerHTML = checkBox.checked ? '<i class="fa fa-sign-in xxxlarge"></i>' : '<i class="fa fa-sign-out xxxlarge"></i>';
}

//actually sends http request to server
function newEvent(userId, locationId, type) {
  var url = thisUrl()+'/encounter/new/?userId='+userId+'&locationId='+locationId+'&type='+type;
  console.log('making request to: ' + url);
  request(url,
    function(xhr){}, 
    function(xhr){});
}


//submits event to server and then refreshes the screen
function sendEvent(id) {
  var checkBox = document.getElementById('sign-in-or-out-checkbox');
  newEvent(id, 1, checkBox.checked ? 'out' : 'in');
  setTimeout(function() {
    updateFeed();
  }, 500);
}

function submitEvent() {
  var textBox = document.getElementById('student-id-textbox');
  sendEvent(textBox.value);
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
    sendEvent(e);
  });
});


//update every 5 seconds
setTimeout(function(){
  updateFeed();
}, 5 * 1000);

//get data at page load
updateFeed();
