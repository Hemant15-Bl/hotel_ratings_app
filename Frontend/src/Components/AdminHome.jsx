import React, { useContext, useEffect, useState } from 'react'
import { Button, Card, CardBody, CardTitle, Col, Container, Input, InputGroup, Row, Spinner, Table } from 'reactstrap';
import { getAllHotels } from '../services/Hotel-service';
import { loadAllUsers } from '../services/User-service';
import { toast } from 'react-toastify';
import UserContext from '../Context/UserContext';
import Sidebar from './Sidebar';
import Users from './Users';
import { Link, useParams } from 'react-router-dom';
import HotelsList from './HotelsList';
import { getActivities } from '../services/Activity-service';
import { getAllRatings } from '../services/Rating-service';
import Ratings from './Ratings';
import "./admin.css";

const AdminHome = () => {

  const [loading, setLoading] = useState(true);

  //-------------- component rendering ----------------------
  const { section } = useParams();
  const view = section || 'dashboard';

  //---------- Load hotels from backend -----------------
  const [hotels, setHotels] = useState([]);
  const [users, setUsers] = useState([]);
  const { user } = useContext(UserContext);
  const [ratings, setRatings] = useState([]);
  const [activities, setActivities] = useState([]);
  const [searchQuery, setSearchQuery] = useState("");


  //Load all hotels and users data
  useEffect(() => {
    document.title = "HRS | Dashboard";

    const loadData = async () => {
      setLoading(true);
      try {
        const [hotelsData, UsersData, ratingsData, activitiesData] = await Promise.all([getAllHotels(), loadAllUsers(), getAllRatings(), getActivities()]);

        setHotels(hotelsData);
        setUsers(UsersData);
        setRatings(ratingsData);
        setActivities(activitiesData);

        toast.success("Data synchronized successfully!")
      } catch (error) {
        toast.error("Failed to load dashboard data!!")
        console.error(error);
      }finally{
        setLoading(false);
      }
    };

    loadData();
  }, []);

  //Change color on activity-log
  const getActivityColor = (type) => {
    if (type.endsWith("_ADD")) return "#12b0cfff";
    if (type.endsWith("_EDIT")) return "#ffc107";
    if (type.endsWith("_DELETE")) return "#dc3545";

    return '#6c757d';
  };

  //Seach Queries
  const filterUsers = users.filter((u) => {
    return (
      u.name?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      u.email?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      u.address?.toLowerCase().includes(searchQuery.toLowerCase())
    );
  });

  const filterHotels = hotels.filter((h) => {
    return (
      h.name?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      h.location?.toLowerCase().includes(searchQuery.toLowerCase())
    );
  });

  const filterRatings = ratings.filter((r) => {
    const userName = users.find(u => u.userId === r.userId)?.name || "";
    const hotelName = hotels.find(h => h.hotelId === r.hotelId)?.name || "";
    return (
      userName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      hotelName.toLowerCase().includes(searchQuery.toLowerCase())
    );
  });

  if (loading) {
    return (
      <div className='d-flex flex-column justify-content-center align-items-center' style={{ height: '100vh', width: '100vw' }}>
        <Spinner color="info" style={{ width: '3rem', height: '3rem' }}>...</Spinner>
        <h4 className='mt-3 text-muted animate__animated animate__pulse animate__infinite'>
          Please Wait Synchronizing with Data...
        </h4>
      </div>
    );
  }
  return (

    <div className='d-flex' style={{ minHeight: '100vh', width: '100vw' }}>
      <Sidebar />

      {/* Main Content Area*/}
      <div className="flex-grow-1 bg-light" style={{ minHeight: '100vh' }}>

        {/* Header Area */}
        <div className="p-4">

          {/* CONDITIONALLY Rendering Content */}
          {view === 'dashboard' && (<>

            <div className="d-flex justify-content-between align-items-center mb-4">
              <h2 className='text-dark'>Hotel-Rating Overview</h2>
              <div className="text-muted">Welcome back, Admin</div>
            </div>
            <Row>
              <Col xs={12} sm={6} lg={4}>
                <Card className="border-0 shadow-sm h-100" style={{ borderRadius: '15px' }}>
                  <CardBody className="d-flex align-items-center">
                    <div className="rounded-circle d-flex align-items-center justify-content-center me-3"
                      style={{
                        width: '55px', height: '55px',
                        backgroundColor: 'rgba(18, 176, 207, 0.1)', // Subtle Cyan
                        color: '#12b0cfff'
                      }}>
                      <i className="bi bi-people-fill fs-3"></i>
                    </div>
                    <div>
                      <h6 className="text-muted mb-0">Total Users</h6>
                      <h2 className="fw-bold mb-0">{users.length}</h2>
                    </div>
                  </CardBody>
                </Card>
              </Col>

              <Col xs={12} sm={6} lg={4}>
                <Card className="border-0 shadow-sm h-100" style={{ borderRadius: '15px' }}>
                  <CardBody className="d-flex align-items-center">
                    <div className="rounded-circle d-flex align-items-center justify-content-center me-3"
                      style={{
                        width: '55px', height: '55px',
                        backgroundColor: 'rgba(40, 167, 69, 0.1)', // Subtle Cyan
                        color: '#12b0cfff'
                      }}>
                      <i className="bi bi-building-fill fs-3"></i>
                    </div>
                    <div>
                      <h6 className="text-muted mb-0">Hotels Available</h6>
                      <h2 className="fw-bold mb-0">{hotels.length}</h2>
                    </div>
                  </CardBody>
                </Card>
              </Col>

              <Col xs={12} sm={12} lg={4}>
                <Card className="border-0 shadow-sm h-100" style={{ borderRadius: '15px' }}>
                  <CardBody className="d-flex align-items-center">
                    <div className="rounded-circle d-flex align-items-center justify-content-center me-3"
                      style={{
                        width: '55px', height: '55px',
                        backgroundColor: 'rgba(255, 193, 7, 0.1)', // Subtle Cyan
                        color: '#12b0cfff'
                      }}>
                      <i className="bi bi-star-fill fs-3"></i>
                    </div>
                    <div>
                      <h6 className="text-muted mb-0">Total Ratings</h6>
                      <h2 className="fw-bold mb-0">{ratings.length}</h2>
                    </div>
                  </CardBody>
                </Card>
              </Col>
            </Row>

            <div className="bg-white p-4 rounded-4 shadow-sm border-0 mt-4">
              <h5 className="fw-bold mb-4 text-dark">Recent Activity</h5>
              <div className="timeline" style={{ borderLeft: '2px solid #e9ecef', marginLeft: '10px', paddingLeft: '20px', maxHeight: '70vh', overflowY: 'auto' }}>
                {activities.map(act => (
                  <div key={act.activityId} className="mb-4 position-relative">
                    {/* The Dot */}
                    <div style={{
                      position: 'absolute', left: '-22px', top: '4px',
                      width: '12px', height: '12px', borderRadius: '50%',
                      backgroundColor: getActivityColor(act.actionType),
                      border: '2px solid #fff',
                      boxShadow: '0 0 0 2px #f8f9fa' // Extra ring for professional look
                    }}></div>

                    <div className="ps-2">
                      <div className="d-flex align-items-center">
                        {/* Optional: Tiny Tag based on Category */}
                        <span className="badge me-2" style={{
                          fontSize: '0.65rem',
                          backgroundColor: act.actionType.startsWith('USER') ? '#e3f2fd' :
                            act.actionType.startsWith('HOTEL') ? '#e8f5e9' : '#fff3e0',
                          color: '#444'
                        }}>
                          {act.actionType.split('_')[0]}
                        </span>
                        <span className="fw-semibold text-dark" style={{ fontSize: '0.9rem' }}>{act.message}</span>
                      </div>
                      <small className="text-muted"><i className="bi bi-clock me-1"></i>{new Date(act.timestamp).toLocaleTimeString()}</small>
                    </div>
                  </div>
                ))}
              </div>
            </div>

          </>)}

          {/* -------------- Search bar -------------------------------------------------------------------- */}
          {view !== 'dashboard' && (
            <div className="mb-4">
              <InputGroup className="shadow-sm border-0">
                <Input
                  placeholder={`Search ${view.split('-')[1]}...`}
                  className="border-0 p-3"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  style={{ borderRadius: '10px 0 0 10px' }}
                />
                <Button color="white" className="border-0 bg-white" style={{ borderRadius: '0 10px 10px 0' }}>
                  <i className="bi bi-search text-muted"></i>
                </Button>
              </InputGroup>
            </div>
          )}

          <div className="mx-auto" style={{ maxWidth: '1200px' }}>
            {/* -------------------------- Users html --------------------------------------------------------------- */}

            {view === 'manage-users' && (

              <div className="animate__animated animate__fadeIn">
                {/* Fallback Alert */}
                {users.length > 0 && users[0]?.userId === "FALLBACK-001" && (
                  <div className="alert alert-warning border-0 shadow-sm mb-4">
                    <strong>Service Offline:</strong> Viewing local user cache.
                  </div>
                )}

                <div className="d-flex align-items-center justify-content-between mb-4">
                  <div>
                    <h2 className='fw-bold mb-0' style={{ color: '#2c3e50' }}>User Directory</h2>
                    <p className="text-muted small">Manage system access and profiles</p>
                  </div>
                  <Button

                    style={{ backgroundColor: '#12b0cfff', border: 'none' }}
                    className='border-0 shadow-sm d-flex align-items-center btn-cyan-admin'
                    tag={Link}
                    to={"/admin/add-user"}
                    disabled={users[0]?.userId === "FALLBACK-001"}
                  >
                    <i className="bi bi-person-plus-fill me-2"></i>Add New User
                  </Button>
                </div>

                {/* Using a Row/Col grid */}
                <Row style={{ maxHeight: '70vh', overflowY: 'auto' }}>
                  {filterUsers.length > 0 ? (
                    filterUsers.map(user => (
                      <Users
                        user={user}
                        key={user.userId}
                        refreshUser={(id) => setUsers(users.filter(u => u.userId !== id))}
                        isFallback={user.userId === "FALLBACK-001"}
                      />
                    ))
                  ) : (
                    <div className="text-center w-100 py-5">
                      <img src="https://cdn-icons-png.flaticon.com/512/6134/6134065.png" alt="not found" style={{ width: '100px', opacity: 0.5 }} />
                      <h3 className="text-muted mt-3">No users match your search "{searchQuery}"</h3>
                      <Button color="link" onClick={() => setSearchQuery("")}>Clear all filters</Button>
                    </div>
                  )}
                </Row>
              </div>
            )}
            {/* ------------------------------------------------------------------------------------------------------------------- */}

            {/* -------------------------------- Hotels html ---------------------------------------------------------------------- */}
            {view === 'manage-hotels' && (
              <div className="animate__animated animate__fadeIn">
                {/* Fallback Alert stays as is - it's good for UX */}
                {hotels.length > 0 && hotels[0].hotelId?.startsWith("FALLBACK") && (
                  <div className="alert alert-warning border-0 shadow-sm mb-4">
                    <strong>Offline Mode:</strong> Viewing cached data. Actions are restricted.
                  </div>
                )}

                <div className="d-flex align-items-center justify-content-between mb-4">
                  <div>
                    <h2 className='fw-bold mb-0' style={{ color: '#2c3e50' }}>Hotel Management</h2>
                    <p className="text-muted small">Total Hotels: {hotels.length}</p>
                  </div>
                  <Button
                    color='success'
                    className='border-0 shadow-sm d-flex align-items-center btn-cyan-admin'
                    style={{ backgroundColor: '#12b0cfff' }}
                    tag={Link}
                    to={"/admin/add-hotel"}
                    disabled={hotels[0]?.hotelId === "FALLBACK-001"}
                  >
                    <i className="bi bi-plus-lg me-2"></i>Add New Hotel
                  </Button>
                </div>

                {/* The List Container */}
                <div className="pe-2" style={{ maxHeight: '70vh', overflowY: 'auto' }}>
                  {filterHotels.length > 0 ? (
                    filterHotels.map(hotel => (
                      <HotelsList
                        hotel={hotel}
                        key={hotel.hotelId}
                        refreshHotel={(id) => setHotels(hotels.filter(h => h.hotelId !== id))}
                        user={user}
                        isFallback={hotel.hotelId === "FALLBACK-001"}
                      />
                    ))
                  ) : (
                    <div className="text-center w-100 py-5">
                      <img src="https://cdn-icons-png.flaticon.com/512/6134/6134065.png" alt="not found" style={{ width: '100px', opacity: 0.5 }} />
                      <h3 className="text-muted mt-3">No hotels match your search "{searchQuery}"</h3>
                      <Button color="link" onClick={() => setSearchQuery("")}>Clear all filters</Button>
                    </div>
                  )}
                </div>
              </div>
            )}

            {/* ------------------------------------------------------------------------------------------------------------------- */}

            {/* -------------------------------- Ratings html ---------------------------------------------------------------------- */}
            {view === 'manage-ratings' &&
              (
                <div className="mx-auto" style={{ maxWidth: '1200px' }}>
                  <h2 className='text-dark mb-4'>User Feedbacks & Ratings</h2>

                  {/* Quick Stat Row */}
                  <Row className='mb-4' >
                    <Col md={4}>
                      <Card className='shadow-sm border-0 bg-white text-center p-3'>
                        <h6 className='text-muted'>Average Rating</h6>
                        <h2 className='text-warning'>
                          {(ratings.reduce((acc, r) => acc + r.rating, 0) / ratings.length).toFixed(1)} / 10
                        </h2>

                      </Card>
                    </Col>

                    <Col md={8}>
                      <Card className='shadow-sm border-0 bg-white text-center p-3'>
                        <div className="d-flex justify-content-around">
                          <div className="text-center">
                            <div className="text-success fw-bold">Positive</div>
                            <div>{ratings.filter(r => r.rating >= 7).length}</div>
                          </div>

                          <div className="text-center">
                            <div className="text-warning fw-bold">Neutral</div>
                            <div>{ratings.filter(r => r.rating >= 4 && r.rating < 7).length}</div>
                          </div>

                          <div className="text-center">
                            <div className="text-danger fw-bold">Critical</div>
                            <div>{ratings.filter(r => r.rating < 4).length}</div>
                          </div>
                        </div>
                      </Card>
                    </Col>
                  </Row>

                  {/* Detailed Table */}
                  <Row style={{ maxHeight: '70vh', overflowY: 'auto' }}>
                    {filterRatings.length > 0 ? filterRatings.map(r => {
                      //find user and hotel name
                      const userName = users.find(u => u.userId === r.userId)?.name || "Unknown User";
                      const hotelName = hotels.find(h => h.hotelId === r.hotelId)?.name || "Unknown Hotel";

                      return (<Ratings rating={r} userName={userName} hotelName={hotelName} key={r.ratingId} />)
                    }
                    ) : (
                      <div className="text-center w-100 py-5">
                        <img src="https://cdn-icons-png.flaticon.com/512/6134/6134065.png" alt="not found" style={{ width: '100px', opacity: 0.5 }} />
                        <h3 className="text-muted mt-3">No ratings match your search "{searchQuery}"</h3>
                        <Button color="link" onClick={() => setSearchQuery("")}>Clear all filters</Button>
                      </div>
                    )}
                  </Row>

                </div>
              )
            }
          </div>
        </div>
      </div>
    </div>
  )
}

export default AdminHome;