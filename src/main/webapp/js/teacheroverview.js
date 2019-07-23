"use strict"

function updateCurrentStatus() {
  var apiKey = Cookies.getJSON('apiKey');
  var schedule = Cookies.getJSON('schedule');

  // get students
  var getStudentListUrl = thisUrl() + '/schedule/' +
    '?locationId=' + schedule.location.id +
    '&period=' + schedule.period +
    '&managerId=' + schedule.user.id +
    '&apiKey=' + apiKey.key;
  request(getStudentListUrl,
    function(xhr) {
      // TODO decide how to represent people in a schedule/ who teaches who

      // select people who have a student permission
      var studentschedules = JSON.parse(xhr.responseText);

      // now we request all the encounters which occur at this location since the period started
      var getEncounterListUrl = thisUrl() + '/encounter/' +
        '?type=in' +
        '&locationId=' + schedule.location.id +
        '&managerId=' + schedule.user.id +
        '&minDate=' + getPeriodStart(schedule.period)  + //TODO what if someone signs in early
        '&apiKey=' + apiKey.key;
      request(getEncounterListUrl,
        // success
        function(xhr) {
          var table = document.getElementById('current-status-table');
          //blank table
          table.innerHTML='';

          // now we must compare to check if each one of these works
          var studentencounters = JSON.parse(xhr.responseText);

          for(var i = 0; i < studentschedules.length; i++) {
            var schedstudent = studentschedules[i];
            var text = '<span class="fa fa-times"></span>';
            var bgcolor = '#ffcccc';
            var fgcolor = '#ff0000';
            // if we can find it
            if(studentencounters.filter(e=>e.user.id==schedstudent.user.id).length > 0) {
              text =  '<span class="fa fa-check"></span>'
              bgcolor = '#ccffcc';
              fgcolor = '#00ff00';
            }

            table.insertRow(0).innerHTML=
              ('<tr>' +
              '<td>' + schedstudent.user.name + '</td>' +
              '<td>' + schedstudent.user.id + '</td>' +
              '<td style="background-color:'+bgcolor+';color:'+fgcolor+'">' + text + '</td>' +
              '</tr>');
          }
        },
        //failure
        function(xhr) {
          return;
        }
      );
    },
    //failure
    function(xhr) {
      return;
    }
  );
}

function addSignInOutFeedEntry(encounter)
{
  var table = document.getElementById('recent-activity-table');
  table.insertRow(0).innerHTML=
    ('<tr>' +
    '<td>' + encounter.user.name + '</td>' +
    '<td>' + encounter.type + '</td>' +
    '<td>' + moment.unix(encounter.time).fromNow() + '</td>' +
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
  var checkBox = document.getElementById('sign-in-or-out');
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
  // displayInfo();
  // get data at page load
  updateFeed();
  updateCurrentStatus();
  // Initialize scanner selector
  $(document).scannerDetection(function(e, data) {
    sendEncounter(e);
  });
});

//update every second
setInterval(function() {
  //displayInfo();
  updateFeed();
  updateCurrentStatus();
}, 1000);

//when enter key is pressed in the student ID field.
window.onload = function() {
  var buttonA = document.getElementById("user-id-textbox");

  buttonA.addEventListener("keydown", function(event) {
    if (event.keyCode === 13) {
      submitEncounter();
    }
  });
}
