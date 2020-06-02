import React, { Component } from 'react';

// Fonts
import "typeface-ubuntu";
import "typeface-exo";

// Bootstrap CSS & JS
import 'bootstrap/dist/css/bootstrap.min.css'
import 'jquery/dist/jquery.min.js'
import 'bootstrap/dist/js/bootstrap.min.js'

import '../css/homepage.css';

import Footer from './Footer';
import Header from './Header';


class ExternalLayout extends Component {
  render() {
    return (
      <div>
        <Header />
        <div className="content">
          {this.props.children}
        </div>
        <Footer />
      </div>
    )
  }
}

export default ExternalLayout;
