import React, { useContext, useEffect } from 'react'
import { data, useNavigate } from 'react-router-dom';
import { loadUserByAuth } from '../services/User-service';
import UserContext from '../Context/UserContext';
import { Spinner } from 'reactstrap';


const OAuth2Callback = () => {

    const navigate = useNavigate();
    const { setUser } = useContext(UserContext);

    useEffect(() =>{
        const param = new URLSearchParams(window.location.hash.replace('#', '?'));
        const token = param.get("access_token");

        if (token) {
            localStorage.setItem("access_token", token);
        }

        //-------- load user details from database
        loadUserByAuth().then(userData =>{
           
            //setting user data in provider
            setUser({data: userData, login: true, loading: false});

            // redirect with role (ROLE BASED ACCESS CONTROL)
            const isAdmin = userData.roles?.some(role => role.name === "ROLE_ADMIN");

            if (isAdmin) {
                navigate("/admin/dashboard");
            }else{
                navigate("/user/dashboard");
            }

        }).catch(err =>{
            console.log("Auth failed!!:", err);
            navigate("/");
        });
    },[navigate]);
    return (
        <div className='d-flex justify-content-center mt-5'><h4>Logging in...</h4>
        <Spinner />
        </div>
    )
}

export default OAuth2Callback;