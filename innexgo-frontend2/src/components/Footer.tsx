import React from 'react';
import { Link } from 'react-router-dom';

import innexgo_logo from 'img/innexgo_logo.png';

function Footer() {
  return (
    <footer className="footer">
      <div className="container-fluid py-1"><img className="m-2 logoicon" src={innexgo_logo} />
        <p className="text-light d-inline float-right mr-3 mt-3">&copy; Innexgo LLC, 2020</p>
        <p className="d-inline float-right mr-3 mt-3 text-light">
          <Link to="/terms_of_service">Terms of Service</Link>
        </p>
        <p className="d-inline float-right mr-3 mt-3 text-light">
          <Link to="/cookie_policy">Cookie Policy</Link>
        </p>
      </div>
    </footer>
  )
}

export default Footer;
