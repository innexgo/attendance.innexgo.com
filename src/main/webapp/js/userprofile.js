"use strict"

/* global
 Cookies
 fetchJson apiUrl linkAbsolute linkRelative INT32_MAX
 givePermError
*/

const position = {
  0:'Administrator',
  1:'Teacher'
}

async function loadData() {
  let apiKey = Cookies.getJSON('apiKey');
  let semester = Cookies.getJSON('semester');

  if(apiKey == null || semester == null) {
    console.log('Missing cookies');
    return;
  }

  let searchParams = new URLSearchParams(window.location.search);

  if (!searchParams.has('userId')) {
    givePermError('Page loaded with invalid parameters.');
    return;
  }

  let userId = searchParams.get('userId');

  try {
    let user = (await fetchJson(`${apiUrl()}/user/?userId=${userId}&offset=0&count=1&apiKey=${apiKey.key}`))[0];
    if(user == null) {
      throw new Error('Failed to find user with this ID');
    }

    document.getElementById('user-name').innerHTML = user.name;
    document.getElementById('user-email').innerHTML = 'Email: ' + linkAbsolute(user.email, 'mailto:' + user.email);
    document.getElementById('user-position').innerHTML = position[user.ring];

    (await fetchJson(`${apiUrl()}/course/?semesterStartTime=${semester.startTime}&userId=${userId}&offset=0&count=${INT32_MAX}&apiKey=${apiKey.key}`))
        .sort((a, b) => (a.period > b.period) ? 1 : -1)
        .forEach(course => $('#user-courses').append(`
            <tr>
              <td>${course.period}</td>
              <td>${linkRelative(course.subject, '/courseprofile.html?courseId='+course.id)}</td>
              <td>${linkRelative(course.location.name, '/locationprofile.html?locationId='+course.location.id)}</td>
            </tr>`));
  } catch(err) {
    console.log(err);
    givePermError('Page loaded with invalid user id.');
  }
}

//Bootstrap Popover - Alert Zones/Quick help for Card(s)
$(document).ready(function(){
  $('[data-toggle="popover"]').popover({
      trigger : 'hover'
  });
});

$(document).ready(function() {
  loadData();
})
