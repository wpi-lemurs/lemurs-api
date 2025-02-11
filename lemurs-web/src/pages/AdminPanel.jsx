import React, { useState, useContext } from 'react';

import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';
import { TokenContext } from '../components/token/TokenContext';

export default function AdminPanel() {
  const {token} = useContext(TokenContext)
  const [participantEmail, setParticipantEmail] = useState("")
  const [umassID, setUmassID] = useState("")
  const [adminEmail, setAdminEmail] = useState("")
  const [adminRole, setAdminRole] = useState(-1)

  const authorizePariticipantEmail = () => {
      fetch(
          `${process.env.REACT_APP_LEMURS_SERVER_HOST}/user/authorize`,
          {
              method: "POST",
              headers: new Headers({ Authorization: token, "content-type": "application/json"}),
              body: JSON.stringify({"umassId": umassID, "email": participantEmail})
          }
          ).then(async (response) => {
          if (!response.ok) {
              throw response.status;
          }
      });
  }

  const authorizeAdminRole = () => {
    fetch(
        `${process.env.REACT_APP_LEMURS_SERVER_HOST}/user/authorize/admin`,
        {
            method: "POST",
            headers: new Headers({ Authorization: token, "content-type": "application/json"}),
            body: JSON.stringify({"email": adminEmail, "role": adminRole})
        }
        ).then(async (response) => {
        if (!response.ok) {
            throw response.status;
        }
    });
}

  return (
      <div className="App">
          <h2 className="card-title">LEMURS Admin Panel</h2>
          <br/>
          {(token === "") ? (
              <h5 className="card-title">Please sign in to use the LEMURS admin panel.</h5>
          ) : (
              <>
                  <div style={{width: "50%", margin: "auto"}}>
                      <br/>
                      <h5 className="card-title">Add Participants</h5>
                      <Form>
                          <Form.Group className="mb-3" controlId="authEmailForm.EmailInput">
                              <Form.Label>Participant email address</Form.Label>
                              <Form.Control type="email" placeholder="name@example.com" onChange={(e) => {setParticipantEmail(e.target.value)}}/>
                          </Form.Group>
                          <Form.Group className="mb-3" controlId="authEmailForm.UmassIDInput">
                              <Form.Label>Participant umass ID (use name-test for testing)</Form.Label>
                              <Form.Control type="text" onChange={(e) => {setUmassID(e.target.value)}}/>
                          </Form.Group>
                          <Button variant="primary" type="button" onClick={authorizePariticipantEmail}>
                              Authorize Participant
                          </Button>
                      </Form>
                  </div>
                  <br/>
                  <br/>
                  <div style={{width: "50%", margin: "auto"}}>
                      <br/>
                      <h5 className="card-title">Assign Admin Role</h5>
                      <Form>
                          <Form.Group className="mb-3" controlId="authEmailForm.EmailInput">
                              <Form.Label>Admin email address</Form.Label>
                              <Form.Control type="email" placeholder="name@example.com" onChange={(e) => {setAdminEmail(e.target.value)}}/>
                          </Form.Group>
                          <Form.Group className="mb-3" controlId="authEmailForm.UmassIDInput">
                              <Form.Label>New admin role</Form.Label>
                              <Form.Select value={adminRole} onChange={(e) => {setAdminRole(e.target.value)}}>
                                    <option value={-1}>Select an admin role</option>
                                    <option value={1}>Researcher</option>
                                    <option value={2}>Staff</option>
                                    <option value={3}>Owner</option>
                                </Form.Select>
                          </Form.Group>
                          <Button variant="primary" type="button" onClick={authorizeAdminRole}>
                              Assign Role
                          </Button>
                      </Form>
                  </div>
              </>
          ) }
      </div>
  );
};