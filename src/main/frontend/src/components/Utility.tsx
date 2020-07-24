import React from 'react';
import { OverlayTrigger, Card } from 'react-bootstrap';
import { Help } from '@material-ui/icons';

interface UtilityProps {
  title: string
  overlay: React.ReactElement
}

function Utility(props: React.PropsWithChildren<UtilityProps>) {
  return <Card>
    <Card.Body>
      <div className="d-flex justify-content-between">
        <Card.Title >{props.title}</Card.Title>
        <OverlayTrigger
          overlay={props.overlay}
          placement="auto"
        >
          <button type="button" className="btn btn-sm">
            <Help />
          </button>
        </OverlayTrigger>
      </div>
      {props.children}
    </Card.Body>
  </Card>
}

export default Utility;
