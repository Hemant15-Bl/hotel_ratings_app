import { useContext, useEffect, useState } from 'react'
import './App.css';
import { ToastContainer, toast } from 'react-toastify';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import Contact from './Components/Contact';
import About from './Components/About';
import LogIn from './Components/LogIn';
import Home from './Components/Home';
import Signup from './Components/Signup';
import Hotel from './Components/Hotel';
import PrivateRoute from './Components/PrivateRoute';
import DashBoard from './Components/DashBoard';
import ViewHotel from './Components/ViewHotel';
import ProfileInfo from './Components/ProfileInfo';
import AddHotel from './Components/AddHotel';
import UpdateUser from './Components/UpdateUser';
import AdminHome from './Components/AdminHome';
import OAuth2Callback from './auth/OAuth2Callback';
import UserContext from './Context/UserContext';
import ChatWidget from './Components/ChatWidget';


function App() {

  useEffect(() => {
    document.title = "HRS | Hotel-Rating-System";
  }, []);

  function ChatGate() {
  const { user } = useContext(UserContext);
  return user?.login === true ? <ChatWidget /> : null;
}

  const router = createBrowserRouter([

    //----------- Public Routes -------------------------
    { path: "/contact", element: <Contact /> },
    { path: "/login", element: <LogIn /> },
    { path: "/", element: <Home /> },
    { path: "/about", element: <About /> },
    { path: "/hotels", element: <Hotel /> },
    { path: "/oauth2/callback", element: <OAuth2Callback /> },


    //----------- Shared Protected Route (Both User and Admin) ----
    {
      path: '/', element: <PrivateRoute />, children: [
         { path: "viewhotel/:hotelId/:userId", element: <ViewHotel /> },
        { path: "profile-info", element: <ProfileInfo /> },
        { path: "profile-edit/:id", element: <UpdateUser /> },
      ]
    },


    //----------- Normal User Routes -------------------------
    {
      path: "/user", element: <PrivateRoute />, children: [
        { path: "dashboard", element: <DashBoard /> }
        
      ]
    },


    //----------- Admin Specific Routes -------------------------
    {
      path: "/admin", element: <PrivateRoute />, children: [

        { path: "edit-hotel/:hotelId", element: <AddHotel /> },
        { path: "add-hotel", element: <AddHotel /> },
        { path: "add-user", element: <Signup /> },
        { path: "profile-info/:userId", element: <ProfileInfo /> },
        { path: ":section", element: <AdminHome /> }, //this is magic route for capturing users, hotels, and ratings
      ]
    }


  ]);

  return (


    <div>
      <ToastContainer />
      <RouterProvider router={router} />
      <ChatGate />
    </div>

  )
}

export default App;
