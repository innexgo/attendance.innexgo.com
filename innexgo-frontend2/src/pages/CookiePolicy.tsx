import React from 'react';

import ExternalLayout from '../components/ExternalLayout';
import { Container } from 'react-bootstrap';

function CookiePolicy() {
  return (
    <ExternalLayout>
      <section>
        <Container>
          <h5>Our Cookie Policy</h5>
          <p>
            Innexgo LLC uses cookies on www.innexgo.com. By using the Service, you consent to the use of cookies.
            This cookies policy explains what cookies are, how we use cookies, how third-parties we may
            partner with may use cookies on the Service, your
            choices regarding cookies and further information about cookies.
      </p>
          <h5>What are cookies?</h5>
          <p>
            Cookies are small pieces of text sent by your web browser by a website you visit. A cookie file is stored in your
            web browser and allows the Service or a third-party to recognize you and make your next visit easier and the Service more useful
            to you. Cookies can be &quot;persistent&quot; or &quot;session&quot; cookies.
      </p>
          <h5>How Innexgo LLC uses cookies</h5>
          <p >
            When you use and access the Service, we may place a number of cookies files in your web browser.
            We use cookies for the following purposes: to enable certain functions of the Service and to
            store your preferences. We use essential cookies to authenticate
            users and prevent fraudulent use of user accounts.
      </p>
          <h5>Third-party cookies</h5>
          <p>The Service uses no third party cookies.</p>
          <h5>What are your choices regarding cookies?</h5>
          <p>
            If you&apos;d like to delete cookies or instruct your web browser to delete or refuse cookies, please visit the help pages of
            your web browser. Please note, however, that if you delete cookies or refuse to accept them, you might not be able to use
            all of the features we offer, you may not be able to store your preferences, and some of our pages might not display properly.
      </p>
        </Container>
      </section>
    </ExternalLayout>
  )
}

export default CookiePolicy;
