"use strict"

async function loadData() {
  let apiKey = Cookies.getJSON('apiKey');
  if(apiKey == null) {
    console.log('Not signed in');
    return;
  }

  let semester = Cookies.getJSON('semester');
  if(semester == null) {
    console.log('No semester, bailing');
    return;
  }

  let searchParams = new URLSearchParams(window.location.search);

  if (!searchParams.has('locationId')) {
    givePermError('Page loaded with invalid parameters.');
    return;
  }

  var locationId = searchParams.get('locationId');

  try {
    let location = (await fetchJson(`${apiUrl()}/location/?locationId=${locationId}&apiKey=${apiKey.key}`))[0]
    if(location == null) {
      throw new Error('Location Id undefined in database!');
    }
    document.getElementById('location-name').innerHTML = location.name;
  } catch(err) {
    console.log(err);
    givePermError('Page loaded with invalid location id.');
  }

  try {
    // One liner time
    (await fetchJson(`${apiUrl()}/course/?locationId=${locationId}&semesterStartTime=${semester.startTime}&apiKey=${apiKey.key}`))
      .sort((a, b) => (a.period > b.period) ? 1 : -1)
      .forEach(course => $('#location-courses').append(`
            <tr>
              <td>${course.period}</td>
              <td>${linkRelative(course.subject, '/courseprofile.html?courseId='+course.id)}</td>
              <td>${linkRelative(course.teacher.name, '/userprofile.html?userId='+course.teacher.id)}</td>
            </tr>`));
  } catch(err) {
    console.log(err);
    givePermError('Error fetching courses.');
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

