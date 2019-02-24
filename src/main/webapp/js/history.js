"use strict"

function addQueryEntry(encounter)
{
  var table = document.getElementById(isSignIn ? "sign-in-feed" : "sign-out-feed");
  var signInOrSignOutText = encounter.type == 'in' ? 
              '<i class="fa fa-sign-in text-red"></i> Signed-In' :
              '<i class="fa fa-sign-out text-blue"></i> Signed-Out';
  if(table.rows.length < 1) {
    clearFeed();
  }
  table.insertRow(1).innerHTML=
    ('<tr>' + 
    '<td>' + encounter.user.name+ '</td>' +
    '<td>' + signInOrSignOutText + '</td>' +
    '<td>' + getDateString(timestamp) + '</td>' + 
    '</tr>');
}

function clearResultTable()
{
  document.getElementById('result-table').innerHTML = 
            '<tr class="dark-gray">'
              '<td>Name</td>'+
              '<td>In/Out</td>'+
              '<td>Location</td>'+
              '<td>Time</td>'+
            '</tr>';
}
//gets new data from server and inserts it at the beginning
function submitQuery(encounterId,userId, locationId, type, minDate, maxDate, count) {
  var url = thisUrl() + '/encounter/?' +
    (isNAN(encounterId) ?       '' : '&encounterId='+encounterId) +
    (isNAN(userId) ?            '' : '&userId='+userId) +
    (isNAN(locationId) ?        '' : '&locationId='+locationId) +
    (isValidString(type) ?      '' : '&type='+encodeURIComponent(type)) +
    (isNAN(minDate.getTime()) ? '' : '&minDate='+minDate.getTime()) +
    (isNAN(maxDate.getTime()) ? '' : '&maxDate='+maxDate.getTime()) +
    (isNAN(count) ?             '' : '&count='+count);
  console.log('making request to: ' + url);
  request(url, 
    function(xhr){
      var encounters = JSON.parse(xhr.responseText);
      clearFeed();
      for(var i = 0; i < encounters.length; i++) {
        addQueryEntry(encounter);
      }
    },
    function(xhr) 
    {
      console.log(xhr);
    }
  );
}

function onQueryClick() {
  var encounterId = null; //TODO add query box
  var userId = document.getElementById('userId').value;
  var = document.getElementById('userId').value;
  var userId = document.getElementById('userId').value;
}
