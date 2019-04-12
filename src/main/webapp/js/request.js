"use strict"

function addRequest(request)
{
  var table = document.getElementById('request-table');
  if(table.rows.length < 1) {
    clearFeed();
  }

  var color = 'light-gray';
  if(request.reviewed) {
    if(request.authorized) {
      color = 'pale-green';
    }
    else if(!request.authorized) {
      color = 'pale-red';
    }
  }

  table.insertRow(1).outerHTML =
    '<tr class="'+color+'">' + 
    '<td>' + escapeHtml(request.user.name) + '</td>' +
    '<td>' + request.user.id + '</td>' +
    '<td>' + getDateString(request.creationDate) + '</td>' +
    '<td>' + getDateString(request.target.minTime) + '</td>' + 
    '<td>' + escapeHtml(request.reason) + '</td>' + 
    '<td>' +
    '<a type="button" onclick="authorizeRequest('+request.id+',true); return false;"  href=""><i class="fa fa-check xlarge" style="text-align: left; color: #228B22;"></i></a>' +
    '<a type="button" onclick="authorizeRequest('+request.id+',false); return false;" href=""><i class="fa fa-times xlarge" style="text-align: right; color: #DC143C;"></i></a>' +
    '</td>' +
    '</tr>';
}

function clearFeed()
{
  document.getElementById('request-table').innerHTML = 
    '<tr class="dark-gray">'+
    '<td>Name</td>'+
    '<td>ID</td>'+
    '<td>Time Requested</td>'+
    '<td>Tutorial Date</td>'+
    '<td>Reason</td>'+
    '<td>Accept?</td> ' +
    '</tr>';
}

//gets new data from server and inserts it at the beginning
function updateFeed() {
  request(thisUrl()+'/request/?authorizerId='+1, 
    function(xhr){
      var requests = JSON.parse(xhr.responseText);
      clearFeed();
      //go backwards to maintain order
      for(var i =requests.length-1; i >= 0; i--) { 
        addRequest(requests[i])
      }
    },
    function(xhr) 
    {
      console.log(xhr);
    }
  );
}

//actually sends http request to server
function authorizeRequest(requestId, authorized) {
  var url = thisUrl()+'/request/authorize/?requestId='+requestId+'&authorized='+authorized;
  console.log('making request to: ' + url);
  request(url,
    function(xhr){}, 
    function(xhr){});
  setTimeout(function() {
    updateFeed();
  }, 50);
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
setInterval(function(){
  updateFeed();
  console.log('updating feed');
}, 500);

//get data at page load
updateFeed();
