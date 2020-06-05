import React from 'react';
import {Container, Row, Card, CardDeck} from 'react-bootstrap' ;

import ExternalLayout from "../components/ExternalLayout";

import heroBg from "../img/homepage-bg.png"

const heroStyle = {
  backgroundImage: `linear-gradient(rgba(0, 0, 0, 0.5),rgba(0, 0, 0, 0.3)), url(${heroBg})`
}

type FeatureProps = {
  title: String,
  children: React.ReactNode
}

function Feature(props: FeatureProps) {
  return (
    <div className="span">
      <div className="features">
        <div className="icon">
          <i className="fas fa-thumbs-up icon-bg-dark icon-circled icon-5x"></i>
        </div>
        <div className="features_content">
          <h2>{props.title}</h2>
          <p>{props.children}</p>
        </div>
      </div>
    </div>
  )
}
function Home() {
  return (
    <ExternalLayout>
      <section className="hero" style={heroStyle}>
        <div className="hero-inner">
          <h1>Academics, Achievement, Attendance first.</h1>
        </div>
      </section>
      <section id="maincontent">
        <Container>
          <Row>
            <Feature title="Easy to Use">
              Increases teaching time by automating attendance in every classroom and decreasing teacher responsibilities.
            </Feature>
            <Feature title="Secure Campus">
              Ensures schoolwide safety by recording student entrances and exits and preventing chronic absenteeism in integrated classrooms.
            </Feature>
            <Feature title="Detailed Reporting">
              Analyzes attendance data to provide extensive administrator reports on in-session campus safety and attendance.
            </Feature>
          </Row>
          <hr/>
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
          <hr/>
          <Row>
            <h2>What people say about us</h2>
            <div className="testimonial_item">
              <p>&quot;Less time on trying to check who&apos;s there and more time for teaching.&quot;</p>
              <span className="author">Channy Cornejo</span>
              <span className="occupation">Math Department Chair</span>
            </div>
            <div className="testimonial_item">
              <p>&quot;It holds students accountable for their attendance habits.&quot;</p>
              <span className="author">Carole Ng</span>
              <span className="occupation">Computer Science Teacher</span>
           </div>
          </Row>
        </Container>
      </section>
    </ExternalLayout>
  )
}

export default Home;
