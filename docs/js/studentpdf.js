"use strict"

/* global */

const fonts = {
  Roboto: {
    normal: 'fonts/Roboto-Regular.ttf',
    bold: 'fonts/Roboto-Medium.ttf',
    italics: 'fonts/Roboto-Italic.ttf',
    bolditalics: 'fonts/Roboto-MediumItalic.ttf'
  }
};

let student1 = null;
let studentId = null;
let grades = null;
let currentGrade = null;
let courses = null;
let initialSemesterTime = null;
let apiKey = null;

async function getStudentInfo() {
  apiKey = Cookies.getJSON('apiKey');
  console.log(apiKey.key);
  if (apiKey == null) {
    console.log('not signed in');
    return;
  }
  let searchParams = new URLSearchParams(window.location.search);
  if (!searchParams.has('studentId')) {
    console.log('Page not loaded with right params');
    givePermError('Page loaded with invalid parameters. Please go back to reports page and re-enter student name.');
    return;
  }
  studentId = searchParams.get('studentId');

  console.log(studentId);
  try {
    student1 = (await fetchJson(`${apiUrl()}/student/?studentId=${studentId}&offset=0&count=1&apiKey=${apiKey.key}`))[0];
    try {
      grades = await fetchJson(`${apiUrl()}/grade/?studentId=${studentId}&offset=0&count=${INT32_MAX}&apiKey=${apiKey.key}`);
      let semester = Cookies.getJSON('semester');
      currentGrade = grades.filter(g => g.semester.startTime == semester.startTime)[0];
    } catch (err) {
      console.log(err);
      givePermError('Failed to load student info');
    }
    initialSemesterTime = $('#studentprofile-grades').val();

  }
  catch (err) {
    console.log(err);
    givePermError('Page loadeded with invalid student ID.');
  }
  createPDF();
}

let docDefinition = null;
async function createPDF() {
  docDefinition = {
    footer:
      function (currentPage, pageCount) {
        return 'Page ' + currentPage.toString() + ' of ' + pageCount;
      },
    header: {
      text: 'Student Report for ' + student1.name, style: 'header'
    },
    content: [
      {
        columns: [
          {
            width: '*',
            text: 'Student ID: ' + studentId, fontSize: 16, alignment: 'center', margins: 0
          },
          {
            width: '*',
            text: 'Grade: ' + currentGrade.number, fontSize: 16, alignment: 'center', margins: 10
          },
        ],
        columnGap: 20,
      },
      {text: 'Course Schedule', style: 'header'},
      {
        layout: 'lightHorizontalLines',
        table: {
          headerRows: 1,
          widths: ['10%', '35%', '35%', '20%'],

          body: [['Period', 'Course', 'Teacher', 'Location']]
            .concat(
              (await fetchJson(`${apiUrl()}/course/?studentId=${studentId}&semesterStartTime=${initialSemesterTime}&offset=0&count=${INT32_MAX}&apiKey=${apiKey.key}`))
                .sort((a, b) => (a.period > b.period) ? 1 : -1) // Sort in order
                .map(c => [c.period, c.subject, c.teacher.name, c.location.name])
            ),
        },
      },
      {text: 'Recent Irregularities', style: 'header'},
      {
        layout: 'lightHorizontalLines',
        table: {
          headerRows: 1,
          widths: ['20%', '20%', '30%', '30%'],
          body: [
            ['Date', 'Time', 'Class', 'Type']]
            .concat(
              (await fetchJson(`${apiUrl()}/irregularity/?studentId=${studentId}&irregularityMinTime=${0}&irregularityMaxTime=${moment().valueOf()}&offset=0&count=${INT32_MAX}&apiKey=${apiKey.key}`))
                .sort((a, b) => (a.time > b.time) ? -1 : 1) // sort by time descending
                .map(i => [moment(i.time).format('MMM Do, YYYY'), moment(i.time).format('h:mm A'), i.course.subject, i.type])
            ),
        },
      },

    ],
    styles: {
      header: {
        fontSize: 22,
        bold: true,
        alignment: 'center',
        margin: 10
      },
    }
  };

}


async function generateStudentPDF() {
  pdfMake.createPdf(docDefinition).open();
}
$(document).ready(async function () {
  await getStudentInfo();
});

