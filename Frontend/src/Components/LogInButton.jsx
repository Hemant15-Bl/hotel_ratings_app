import React from 'react'
import { Button } from 'reactstrap';

const LogInButton = () => {

    const handleLoginOauth2 = () =>{
        window.location.href = "http://localhost:8084/oauth2/authorization/my-gateway-client";
    }
  return (
    <Button onClick={handleLoginOauth2} className='px-5 mb-2 text-white' color='info' >Sign In</Button>
  )
}

export default LogInButton;