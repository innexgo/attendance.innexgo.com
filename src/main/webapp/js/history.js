"use strict"

function addQueryEntry(encounter)
{
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
    '<td>' + getDateString(encounter.time) + '</td>' + 
    '</tr>');
}

function clearResultTable()
{
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
  var url = thisUrl() + '/encounter/?' +
    (isNaN(encounterId) ?       '' : '&encounterId='+encounterId) +
    (isNaN(userId) ?            '' : '&userId='+userId) +
    (!isValidString(userName) ? '' : '&userName='+userName) +
    (isNaN(locationId) ?        '' : '&locationId='+locationId) +
    (!isValidString(type) ?     '' : '&type='+encodeURIComponent(type)) +
    (isNaN(minDate.getTime()) ? '' : '&minDate='+minDate.getTime()/1000) +
    (isNaN(maxDate.getTime()) ? '' : '&maxDate='+maxDate.getTime()/1000) +
    (isNaN(count) ?             '' : '&count='+count);
  console.log('making request to: ' + url);
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

function onQueryClick() {
  var encounterId = undefined; //TODO add query box
  var userId = parseInt(document.getElementById('userId').value, 10);
  var userName = document.getElementById('userName').value;
  var locationId = undefined;//document.getElementById('locationId').value;
  var type = undefined;
  var minDate = new Date(document.getElementById('minDate').value);
  var maxDate = new Date(document.getElementById('maxDate').value);
  var count = 100;
  submitQuery(encounterId, userId, userName, locationId, type, minDate, maxDate, count);
}
