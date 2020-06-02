import React from 'react';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faBars } from '@fortawesome/free-solid-svg-icons'


function Header() {
  return (
    <header className="header">
      <nav className="navbar navbar-expand-lg fixed-top py-3">
        <div className="container">
          <a className="navbar-brand font-weight-bold" href="/">Innexgo</a>
          <button className="navbar-toggler navbar-toggler-right" type="button" data-toggle="collapse" data-target="#navbarSupportedContent">
            <FontAwesomeIcon icon={faBars} />
          </button>
          <div className="collapse navbar-collapse"
            id="navbarSupportedContent">
            <div className="navbar-nav ml-auto">
              <a className="nav-item nav-link font-weight-bold" href="/about">About</a>
              <a className="nav-item nav-link font-weight-bold" href="/faq">FAQ</a>
              <a className="nav-item nav-link font-weight-bold" href="/login">Login</a>
            </div>
          </div>
        </div>
      </nav>
    </header>
  )
}

export default Header;
