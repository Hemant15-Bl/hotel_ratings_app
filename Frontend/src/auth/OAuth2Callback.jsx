import React, { useContext, useEffect } from 'react'
import { data, useNavigate } from 'react-router-dom';
import { loadUserByAuth } from '../services/User-service';
import UserContext from '../Context/UserContext';
import { Spinner } from 'reactstrap';


const OAuth2Callback = () => {

    const navigate = useNavigate();
    const { setUser } = useContext(UserContext);

    useEffect(() => {
        // const param = new URLSearchParams(window.location.hash.replace('#', '?'));
        // const token = param.get("access_token");

        // if (token) {
        //     localStorage.setItem("access_token", token);
        // }

        //-------- load user details from database
        loadUserByAuth().then(userData => {

            //setting user data in provider
            setUser({ data: userData, login: true, loading: false });

            // redirect with role (ROLE BASED ACCESS CONTROL)
            const isAdmin = userData.roles?.some(role => role.name === "ROLE_ADMIN");

            if (isAdmin) {
                navigate("/admin/dashboard");
            } else {
                navigate("/user/dashboard");
            }

        }).catch(err => {
            console.log("Session expired or invalid token");
            console.log("Auth failed!!:", err);
            navigate("/");
        });
    }, [navigate]);
    return (
        <div
            className='d-flex flex-column align-items-center justify-content-center text-center'
            style={{ minHeight: '100vh', gap: '1rem', backgroundColor: '#f5f7f8' }}
            role='status'
            aria-live='polite'
        >
            <Spinner style={{ width: '3rem', height: '3rem', color: '#0fabca' }}>
                <span className='visually-hidden'>Loading</span>
            </Spinner>

            <p className='text-muted mb-0'>
                Just a moment...
            </p>
        </div>
    )
}

export default OAuth2Callback;