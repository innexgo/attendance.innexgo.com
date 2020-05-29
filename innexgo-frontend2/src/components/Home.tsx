import React from 'react';
import { Link } from 'react-router-dom';

import '../css/homepage.css';

function Home() {
  return (
    <div>
      <p> Hi Home </p>
      <Link to="/terms_of_service">Terms of Service</Link>
    </div>
  )
}

export default Home;
