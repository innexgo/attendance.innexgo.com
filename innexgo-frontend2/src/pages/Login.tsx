import React from 'react';
import { Button, Card, Form } from 'react-bootstrap'
import ExternalLayout from '../components/ExternalLayout';

import innexgo_logo from '../img/innexgo_logo_dark.png';

import blurred_bg from '../img/homepage-bg.png';

function Login() {
  const bgStyle = {
    backgroundImage: `radial-gradient(rgba(0, 0, 0, 0.9),rgba(0, 0, 0, 0.1)), url(${blurred_bg})`,
    height: "100vh",
    alignItems: "center",
    backgroundPosition: "center",
    backgroundRepeat: "no-repeat",
    backgroundSize: "cover",
    display: "flex",
    justifyContent: "center"
  };

  return (
    <ExternalLayout>
      <div style={bgStyle}>
          <Card>
            <Card.Body>
              <Card.Title>
                <h4><img
                  alt="Innexgo Logo"
                  src={innexgo_logo}
                  width="30"
                  height="30"
                  className="d-inline-block align-top"
                />{' '}
                  Innexgo</h4>
              </Card.Title>
              <p>Login to Dashboard</p>
              <Form>
                <Form.Group>
                  <Form.Control id="username" type="email" placeholder="Email" />
                  <br />
                  <Form.Control id="password" type="password" placeholder="Password" />
                  <p className="form-text text-danger" id="error"></p>
                </Form.Group>
                <Button variant="primary" type="submit">Login</Button>
              </Form>
              <br />
            </Card.Body>
          </Card>
      </div>
    </ExternalLayout>
  )
}

export default Login;
