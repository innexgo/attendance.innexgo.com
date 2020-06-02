import React from 'react';

import innexgo_logo from '../img/innexgo_logo.png';

function Footer() {
  return (
    <footer className="footer">
      <div className="container-fluid py-1"><img className="m-2 logoicon" alt="Innexgo Logo" src={innexgo_logo} />
        <p className="text-light d-inline float-right mr-3 mt-3">&copy; Innexgo LLC, 2020</p>
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

export default Footer;
