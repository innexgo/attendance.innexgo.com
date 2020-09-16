import React from 'react';

import ExternalLayout from '../components/ExternalLayout';

import { Container, Row, Col } from 'react-bootstrap';

const teamMemberImgs: Map<String, any> = (function() {
  const r = require.context('../img/team/');
  // initial value is a map
  // reduces array by adding each looked up key to the map
  // for more help check index.d.ts of @types/webpack-env
  return r.keys().reduce((map, key) => map.set(key, r(key)), new Map());
})();

type TeamMemberProps = {
  name: String,
  role: String,
};

function TeamMember(props: TeamMemberProps) {
  return (
    <Col xs style={{ textAlign: "center" as const }}>
      <img style={{ borderRadius: '50%' }} src={teamMemberImgs.get(`./${props.name.split(' ').join('')}.jpg`)} alt='' />
      <h5>{props.name}</h5>
      <p>{props.role}</p>
    </Col>
  )
}

function About() {
  return (
    <ExternalLayout fixed={false} transparentTop={false}>
      <section>
        <Container>
          <div className="py-5">
            <h2>About</h2>
            <p>
              Innexgo started as a project in October 2018. Four students entered MESA&rsquo;s National Engineering
              Design Challenge (NEDC) with the idea of creating a system to monitor student movements in school
              to improve the administration&rsquo;s attendance tracking abilities. The system will improve accountability
              of students being in the right classes at the right time and will benefit the school by ensuring
              attendance-based funding is maintained satisfactorily. As our team progressed through competitions,
              many teachers gave positive feedback regarding our system, and quite a few teachers commented that
              they would love to have the system implemented in their school, including our very own teachers here
              at Santa Teresa. Thus, Innexgo LLC was born.
            </p>
            <p>
              Innexgo LLC now strives to help schools and school districts tackle attendance inconsistencies. We are
              committed to collaborating with community and education leaders to optimize our attendance system
              to the localized needs of all clients. As we move forward, Innexgo LLC aims to bring our technology to
              other academic institutions across many communities in the United States.
            </p>
          </div>
        </Container>
      </section>
      <section>
        <Container>
          <Row>
            <TeamMember name='Govind Pimpale' role='Founding Technology Officer' />
            <TeamMember name='Richard Le' role='Founding Financial Officer' />
            <TeamMember name='Ganesh Pimpale' role='Founding Engineer' />
          </Row>
          <Row>
            <TeamMember name='Marek Pinto' role='Founding Engineer' />
            <TeamMember name='Henry Chen' role='Software Developer' />
            <TeamMember name='Emily Park' role='Hardware Manufacturing' />
          </Row>
          <Row>
            <TeamMember name='Joshua Sah' role='Hardware Installation' />
            <TeamMember name='Jordan Nguyen' role='Hardware Installation' />
            <TeamMember name='Jason Ly' role='Hardware Installation' />
          </Row>
        </Container>
      </section>
    </ExternalLayout >
  )
}

export default About;
