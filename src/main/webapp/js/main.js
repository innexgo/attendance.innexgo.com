function thisUrl(){
				return window.location;
}

function baseUrl(url) {
				return url.protocol + "//" + url.host + "/" + url.pathname.split('/')[1];
}

function expandStudent(id) {
				var url = baseUrl(thisUrl())+"students/"+id+"/";
				var request = new XMLHttpRequest();
				request.open('GET', url, true);
				request.onload = function () {
								if (request.status >= 200 && request.status < 400) {
												var data = JSON.parse(this.response);

								} else {
												console.log('error');
								}
				}
				request.send();
}


function addHistoryTableEntry(name, roomNumber, timestamp)
{
				document.getElementById("history-table").innerHTML =
								'<tr>' + 
								'<td><i class="fa fa-sign-in text-red large"></i></td>' +
								'<td class="yellow">' +
								'<strong>' + name + '</strong>' +
								'signed-in to Room' +
								'<strong>' + roomNumber + '</strong>' + 
								'on' + date + 
								'</td> + 
								'<td><i>5 secs ago</i></td>' + 
								'</tr>';
}



// Get the Sidebar
var mySidebar = document.getElementById("mySidebar");

// Get the DIV with overlay effect
var overlayBg = document.getElementById("myOverlay");

// Toggle between showing and hiding the sidebar, and add overlay effect
function w3_open() {
				if (mySidebar.style.display === 'block') {
								mySidebar.style.display = 'none';
								overlayBg.style.display = "none";
				} else {
								mySidebar.style.display = 'block';
								overlayBg.style.display = "block";
				}
}

// Close the sidebar with the close button
function w3_close() {
				mySidebar.style.display = "none";
				overlayBg.style.display = "none";
}
