"use strict"

/*
 global moment
 apiUrl fetchJson linkRelative INT32_MAX ordinal_suffix_of
 giveTempSuccess givePermError
 */

let course = null;
let period = null;


async function loadClassProfile() {
  let apiKey = getLocalJson('apiKey');

  if (apiKey == null) {
    console.log('not signed in');
    return;
  }

  if (course == null || period == null) {
    console.log('Page not loaded properly!');
    return;
  }

  let table = $('#classprofile-table');

  try {
    let students = await fetchJson(`${apiUrl()}/misc/registeredForCourse/?courseId=${course.id}&time=${period.startTime}&apiKey=${apiKey.key}`);
    let irregularities = await fetchJson(`${apiUrl()}/irregularity/?courseId=${course.id}&periodStartTime=${period.startTime}&offset=0&count=${INT32_MAX}&apiKey=${apiKey.key}`);

    table.innerHTML = '';

    const makeEntry = (student, bgcolor, fgcolor, faClass, toolTip) => `<tr>
         <td>${linkRelative(student.name, '/studentprofile.html?studentId=' + student.id)}</td>
         <td>${student.id}</td>
         <td title="${toolTip}" style="background-color:${bgcolor};color:${fgcolor}">
             <span class="fa ${faClass}"></span>
         </td>
       </tr>`;


    for (var i = 0; i < students.length; i++) {
      let student = students[i];

      let irregularity = irregularities.filter(i => i.student.id == student.id).pop();
      let kind = irregularity === undefined ? null : irregularity.kind;

      switch (kind) {
        case 'ABSENT': {
          table.append(makeEntry(student, '#ffcccc', '#ff0000', 'fa-times', 'Student was supposed to attend, but hasn\'t signed in yet.'));
          break;
        }
        case 'TARDY': {
          table.append(makeEntry(student, '#ffffcc', '#cccc00', 'fa-check', 'Student was supposed to attend and signed in late.'));
          break;
        }
        case 'LEAVE_NORETURN': {
          table.append(makeEntry(student, '#ccffff', '#00cccc', 'fa-times', 'This student was supposed to attend, but has signed out of your classroom early.'));
          break;
        }
        case 'LEAVE_RETURN': {
          table.append(makeEntry(student, '#ccffff', '#00cccc', 'fa-check', 'This student left in the middle of class, but has now signed back in.'));
          break;
        }
        default: {
          table.append(makeEntry(student, '#ccffcc', '#00ff00', 'fa-check', 'Student is present.'));
          break;
        }
      }

    }
  } catch (err) {
    console.log(err);
    givePermError('Failed to get data for students');
  }
}

async function initialize() {
  let apiKey = getLocalJson('apiKey');

  if (apiKey == null) {
    console.log('not signed in');
    return;
  }

  let semester = getLocalJson('semester');
  if (semester == null) {
    console.log('No semester');
    return;
  }

  let searchParams = new URLSearchParams(window.location.search);

  if (!searchParams.has('courseId') || !searchParams.has('periodStartTime')) {
    console.log('Page loaded with invalid parameters.');
    return;
  }

  let courseId = searchParams.get('courseId');
  let periodStartTime = searchParams.get('periodStartTime');

  let text = document.getElementById('classprofile-text');

  try {
    let courses = await fetchJson(`${apiUrl()}/course/?courseId=${courseId}&offset=0&count=${INT32_MAX}&apiKey=${apiKey.key}`);
    let periods = await fetchJson(`${apiUrl()}/period/?periodStartTime=${periodStartTime}&offset=0&count=${INT32_MAX}&apiKey=${apiKey.key}`);

    course = courses[0];
    period = periods[0];

    text.innerHTML = 'View students who attended ' +
      linkRelative(course.subject, '/courseprofile.html?courseId=' + course.id) +
      ' (' + linkRelative(course.teacher.name, '/userprofile.html?userId=' + course.teacher.id) + ') on ' +
      moment(period.startTime).format('dddd, MMMM Do YYYY') + ' ' + ordinal_suffix_of(course.period) + ' period.';
  } catch (err) {
    console.log(err);
    givePermError('Could not get course information');
  }
}


$(document).ready(async function() {
  await initialize();
  await loadClassProfile();
})

//Bootstrap Popover - Alert Zones/Quick help for Card(s)
$(document).ready(function() {
  $('[data-toggle="popover"]').popover({
    trigger: 'hover'
  });
});
