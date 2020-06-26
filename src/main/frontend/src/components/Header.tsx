import React from 'react';
import { Link } from 'react-router-dom'
import { Navbar, Nav } from 'react-bootstrap';

function Header() {
  return (
    <Navbar collapseOnSelect expand="lg" bg="light" variant="light" sticky="top">
      <Navbar.Brand href="/">Innexgo</Navbar.Brand>
      <Navbar.Toggle aria-controls="responsive-navbar-nav" />
      <Navbar.Collapse id="responsive-navbar-nav">
        <Nav className="mr-auto">
          <Nav.Link href="#features">Features</Nav.Link>
          <Nav.Link href="#pricing">Pricing</Nav.Link>
        </Nav>
        <Nav>
          <Nav>
            <Link to="/about"> About</Link>
          </Nav>
          <Nav>
            <Link to="/login">Login</Link>
          </Nav>
        </Nav>
      </Navbar.Collapse>
    </Navbar>
  )
}

export default Header;
