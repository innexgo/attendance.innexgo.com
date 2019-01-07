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

function addSignInOutFeedEntry(isSignIn, studentName, studentId, placeName, timestamp)
{
  var table = document.getElementById(isSignIn ? "sign-in-feed" : "sign-out-feed");
  var signInOrSignOutText = isSignIn ? 'in to' : 'out of';
  table.insertRow(1).innerHTML=
    ('<tr>' + 
    '<td>' + studentName + '</td>' +
    '<td>' + studentId  + '</td>' +
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
  document.getElementById('sign-in-feed').innerHTML = 
            '<tr class="dark-gray">' +
              '<td>Name</td>' +
              '<td>Id</td>' +
              '<td>Time</td>' +
            '</tr>';
}

//gets new data from server and inserts it at the beginning
function updateFeed() {
  request(thisUrl()+'/events/', 
    function(xhr){
      var events = JSON.parse(xhr.responseText);
      for(var i = 0; i < events.length; i++) {
        addSignInOutFeedEntry(events[i].type == "sign-in", events[i].student.name, events[i].student.id, events[i].location.name, events[i].time);
      }
    },
    function(xhr) 
    {
      console.log(xhr.responseText);
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
function newEvent(studentId, locationId, type) {
  request(thisUrl()+'/events/new/?studentId='+studentId+'&locationId='+locationId+'&type='+type,
    function(xhr){}, 
    function(xhr)
    {
      console.log(xhr.responseText);
    });
}


//submits event to server and then refreshes the screen
function submitEvent() {
  var textBox = document.getElementById("student-id-textbox");
  var checkBox = document.getElementById("sign-in-or-out-checkbox");
  newEvent(textBox.value, 1, checkBox.checked ? "sign-out" : "sign-in");
  setTimeout(function() {
    clearFeed();
    updateFeed();
  }, 5000);
}


function grayOutOrangeButton(element) {
  element.classList().remove("deep-orange");
  element.classList().add("gray");
}

function orangeGrayButton(element) {
  element.classList().remove("gray");
  element.classList().add("deep-orange");
}


//update every 5 seconds
setTimeout(function(){
  clearFeed();
  updateFeed();
}, 5 * 1000);

//get data at page load
updateFeed();
