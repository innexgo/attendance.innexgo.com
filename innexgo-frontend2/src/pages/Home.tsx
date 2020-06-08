import React from 'react';
import { Media, Jumbotron, Container, Row, Card, CardDeck } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faThumbsUp, faShieldAlt, faChartLine } from '@fortawesome/free-solid-svg-icons'

import ExternalLayout from "../components/ExternalLayout";

import heroBg from "../img/homepage-bg.png"

function Home() {
  const jumboStyle = {
    backgroundImage: `linear-gradient(rgba(0, 0, 0, 0.5),rgba(0, 0, 0, 0.3)), url(${heroBg})`,
    height: "100vh",
    alignItems: "center",
    backgroundAttachment: "fixed",
    backgroundPosition: "center",
    backgroundRepeat: "no-repeat",
    backgroundSize: "cover",
    display: "flex",
    color: "#fff",
    justifyContent: "center"
  };

  const iconStyle = {
    textAlign: "center" as const,
    fontSize: "65px",
    width: "120px",
    height: "120px",
    lineHeight: "120px",
    display: "inline-block",
    borderRadius: "1000px",
    color: "#fefefe",
    background: "#990000ff",
    padding: "10px",
    margin: "10px"
  };

  const testimonialItemStyle = {
    display: "inline-block",
    padding: "0",
    position: "relative" as const,
    marginTop: "25px",
    marginBottom: "15px",
    width: "100%"
  };

  const testimonialItemOccupationStyle = {
    color: "#aaa",
    display: "block",
    position: "relative" as const,
  }

  const testimonalItemAuthorStyle = {
    display: "block",
    color: "#444",
    fontWeight: "bold" as const,
    marginTop: "20px",
  }


  return (
    <ExternalLayout>
      <Jumbotron fluid style={jumboStyle}>
        <Container>
          <h1> Academics, Achievement, Attendance first. </h1>
        </Container>
      </Jumbotron>
      <section>
        <Container>
          <Row>
            <Media>
              <FontAwesomeIcon style={iconStyle} icon={faThumbsUp} />
              <Media.Body>
                <h5>Easy to Use</h5>
                <p> Increases teaching time by automating attendance in every classroom and decreasing teacher responsibilities. </p>
              </Media.Body>
            </Media>
            <Media>
              <FontAwesomeIcon style={iconStyle} icon={faShieldAlt} />
              <Media.Body>
                <h5>Secure Campus</h5>
                <p> Ensures schoolwide safety by recording student entrances and exits and preventing chronic absenteeism in integrated classrooms. </p>
              </Media.Body>
            </Media>
            <Media>
              <FontAwesomeIcon style={iconStyle} icon={faChartLine} />
              <Media.Body>
                <h5>Detailed Reporting</h5>
                <p> Analyzes attendance data to provide extensive administrator reports on in-session campus safety and attendance. </p>
              </Media.Body>
            </Media>
          </Row>
          <hr />
          <Row>
            <h2>Our Strategy</h2>
            <CardDeck>
              <Card>
                <Card.Body>
                  <Card.Title> Data Collection  </Card.Title>
                  <Card.Text>
                    The process begins with our RFID technology. All classrooms will have a scanner that captures scan-in/out data
                    from students whenever an ID card is detected
                  </Card.Text>
                </Card.Body>
              </Card>
              <Card>
                <Card.Body>
                  <Card.Title> Data Processing </Card.Title>
                  <Card.Text>
                    Data from the RFID-driven scanners are transmitted to the Innexgo database
                    where this data is sorted into categorizations such as class periods, classrooms, and teachers.
                  </Card.Text>
                </Card.Body>
              </Card>
              <Card>
                <Card.Body>
                  <Card.Title>Data Analysis</Card.Title>
                  <Card.Text>
                    Innexgo displays the attendance data through our analytics dashboard where
                    teachers and administrators can monitor student attendance records and access numerous charts and reports.
                  </Card.Text>
                </Card.Body>
              </Card>
            </CardDeck>
          </Row>
          <hr />
          <Row>
            <h2>What people say about us</h2>
            <div style={testimonialItemStyle}>
              <p>&quot;Less time on trying to check who&apos;s there and more time for teaching.&quot;</p>
              <span style={testimonalItemAuthorStyle}>Channy Cornejo</span>
              <span style={testimonialItemOccupationStyle}>Math Department Chair</span>
            </div>
            <div style={testimonialItemStyle}>
              <p>&quot;It holds students accountable for their attendance habits.&quot;</p>
              <span style={testimonalItemAuthorStyle}>Carole Ng</span>
              <span style={testimonialItemOccupationStyle}>Computer Science Teacher</span>
            </div>
          </Row>
        </Container>
      </section>
    </ExternalLayout>
  )
}

export default Home;
