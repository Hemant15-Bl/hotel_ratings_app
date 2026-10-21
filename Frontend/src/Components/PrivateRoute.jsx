import React, { useContext } from 'react'
import { Navigate, Outlet } from 'react-router-dom'
import UserContext from '../Context/UserContext'
import { Spinner } from 'reactstrap';

const PrivateRoute = () => {

  const {user} = useContext(UserContext);

  if (user.loading) {
      return (
        <div
          className='d-flex flex-column align-items-center justify-content-center text-center'
          style={{ minHeight: '100vh', gap: '1rem', backgroundColor: '#f5f7f8' }}
          role='status'
          aria-live='polite'
        >
          <Spinner style={{ width: '3rem', height: '3rem', color: '#12b0cf' }}>
            <span className='visually-hidden'>Loading</span>
          </Spinner>
  
          {/* If your UserContext sets a flag when logout starts (for example user.loggingOut),
              this line can say "Signing you out..." instead */}
          <p className='text-muted mb-0'>
            {user.login ? 'Just a moment...' : 'Signing you out...'}
          </p>
        </div>
      );
    }
  
    return user.login ? <Outlet /> : <Navigate to={"/"} replace />
}

export default PrivateRoute;