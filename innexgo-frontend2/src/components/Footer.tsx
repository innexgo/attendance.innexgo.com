import React from 'react';

import { Navbar, Nav } from 'react-bootstrap'

import innexgo_logo from '../img/innexgo_transparent_icon.png';

function Footer() {
  return (
    <Navbar bg="dark" variant="dark">
      <Navbar.Brand href="#home">
        <img
          alt="Innexgo Logo"
          src={innexgo_logo}
          width="30"
          height="30"
          className="d-inline-block align-top"
        />{' '}
        Innexgo
    </Navbar.Brand>
      <Nav className="mr-auto">
        <Nav.Link>&copy; Innexgo LLC, 2020</Nav.Link>
        <Nav.Link href="/terms_of_service">Terms of Service</Nav.Link>
        <Nav.Link href="/cookie_policy">Cookie Policy</Nav.Link>
      </Nav>
    </Navbar>
  );
}
/*
function Footer() {
  return (
    <footer className="footer">
      <div className="container-fluid py-1">
        <img className="m-2 logoicon" alt="Innexgo Logo" src={innexgo_logo} />
        <p className="text-light d-inline float-right mr-3 mt-3"></p>
        <p className="d-inline float-right mr-3 mt-3 text-light">
          <a href="/terms_of_service">Terms of Service</a>
        </p>
        <p className="d-inline float-right mr-3 mt-3 text-light">
          <a href="/cookie_policy">Cookie Policy</a>
        </p>
      </div>
    </footer>
  )
}
*/
export default Footer;
