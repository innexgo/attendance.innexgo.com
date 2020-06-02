import React from 'react';

import Footer from './Footer';
import Header from './Header';

// Fons
import "typeface-ubuntu";
import "typeface-exo";

import '../css/homepage.css';

// Bootstrap CSS
import 'bootstrap/dist/css/bootstrap.min.css';

function Home() {
  return (
    <div>
      <Header />
      <button className="btn">Button</button>
      
      <p> Hi Home </p>

      <Footer/>
    </div>
  )
}

export default Home;
