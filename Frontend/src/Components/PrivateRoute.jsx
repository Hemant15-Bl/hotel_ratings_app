import React, { useContext } from 'react'
import { Navigate, Outlet } from 'react-router-dom'
import UserContext from '../Context/UserContext'
import { Spinner } from 'reactstrap';

const PrivateRoute = () => {

  const {user} = useContext(UserContext);

  // const token = localStorage.getItem("access_token");

  if (user.loading) {
    return (<div className='d-flex justify-content-center py-5'><h4>Loading Session...</h4> <Spinner>...</Spinner></div>);
  }

  return user.login ? <Outlet /> : <Navigate to={"/"} replace />
}

export default PrivateRoute;