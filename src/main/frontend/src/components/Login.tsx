import React from 'react';
import { Button, Card, Form } from 'react-bootstrap'
import moment from 'moment';

import ExternalLayout from '../components/ExternalLayout';
import { fetchApi } from '../utils/utils';

import innexgo_logo from '../img/innexgo_logo_dark.png';

import blurred_bg from '../img/homepage-bg.png';

interface LoginProps {
  setApiKey: (data: ApiKey | null) => void
}

function Login({ setApiKey }: LoginProps) {
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

  const errorStyle = {
    color: "#DC143C"
  }

  const [errorText, setErrorText] = React.useState("");
  const [userName, setUserName] = React.useState("");
  const [password, setPassword] = React.useState("");

  async function postLogin() {
    const apiKeyExpirationTime = moment().add(30, 'hours').valueOf();
    try {
      const apiKey = await fetchApi(`apiKey/new/?` + new URLSearchParams([
        ['userEmail', userName],
        ['userPassword', password],
        ['expirationTime', `${apiKeyExpirationTime}`],
      ])) as ApiKey;
      setApiKey(apiKey);
    } catch (e) {
      console.log(e);
      setErrorText("Your Username or Password did not match our records");
    }
  }

  return (
    <ExternalLayout fixed={false} transparentTop={false}>
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
                <Form.Control id="username" type="email" placeholder="Email"
                  onChange={e => {
                    setUserName(e.target.value);
                  }} />
                <br />
                <Form.Control id="password" type="password" placeholder="Password"
                  onChange={e => {
                    setPassword(e.target.value);
                  }} />
                <p className="form-text text-danger" id="error"></p>
              </Form.Group>
              <Button variant="primary" onClick={async () => postLogin()}>Login</Button>
              <p style={errorStyle}>{errorText}</p>
            </Form>
            <br />
          </Card.Body>
        </Card>
      </div>
    </ExternalLayout>
  )
}

export default Login;
