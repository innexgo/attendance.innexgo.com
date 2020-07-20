import React from 'react';
import DashboardLayout from '../components/DashboardLayout';
import Utility from '../components/Utility';
import { Container, Popover, CardDeck } from 'react-bootstrap';


function Dashboard() {

  const informationTooltip = (<Popover id="information-tooltip"> When you're not teaching a class, your current classroom is unknown.
  	This means that the system doesn't know which classroom to display data for.
  	By setting a default location here, you can use the apps on this page,
  	such as Current Status, Manual Attendance, and Recent Activity even when you don't have a class active.
  	Note that the default location is overriden when there is a course in session.
  </Popover>);

  return (
    <DashboardLayout>
      <Container className="my-3 mx-3">
        <CardDeck>
        	<Utility title="Information" overlay={informationTooltip}>
        		<p>hi</p>
        	</Utility>
        </CardDeck>
      </Container>
    </DashboardLayout>
  )
}

export default Dashboard;

