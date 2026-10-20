import React, { useContext, useEffect, useState } from 'react'
import Hotel from './Hotel';
import Base from './Base';
import { getAllHotels } from '../services/Hotel-service';
import { Button, Container, Input, InputGroup, Row, Spinner } from 'reactstrap';
import UserContext from '../Context/UserContext';
import { toast } from 'react-toastify';
import HotelShimmer from './HotelShimmer';
import { getHotelRatingSummary } from '../services/Rating-service';

// Loads the average rating of every hotel: { [hotelId]: { status, average, count } }
// One hotel failing only affects that hotel's badge.
const loadRatings = async (hotelList) => {
  const results = await Promise.allSettled(
    hotelList.map(h => getHotelRatingSummary(h.hotelId))
  );

  const map = {};
  results.forEach((res, i) => {
    map[hotelList[i].hotelId] = res.status === 'fulfilled'
      ? res.value
      : { status: 'error', average: null, count: 0 };
  });
  return map;
};

const DashBoard = () => {

  const [hotels, setHotels] = useState([]);
  const [ratingMap, setRatingMap] = useState(null);        // null = ratings still loading
  const [searchTerm, setSearchTerm] = useState("");
  const [isLoading, setIsLoading] = useState(true);

  const { user } = useContext(UserContext);

  useEffect(() => {
    let active = true;
    setIsLoading(true);

    getAllHotels().then(data => {
      if (!active) return;
      setHotels(data);
      setIsLoading(false);

      // Hotels are on screen now; fill in the ratings
      loadRatings(data).then(map => { if (active) setRatingMap(map); });
    })
      .catch(err => {
        console.error("Error Loading All Hotels: ", err)
        toast.error("Something went wrong! Try again!!");
        if (active) setIsLoading(false);
      });

    return () => { active = false; };

  }, []);

// The rating object each card needs
  const ratingFor = (hotelId) => {
    if (!ratingMap) return { status: 'loading', average: null, count: 0 };
    return ratingMap[hotelId] ?? { status: 'error', average: null, count: 0 };
  };

  const filterHotels = hotels.filter((hotel) => {
    const searchLower = searchTerm.toLowerCase();
    return (
      (hotel.name ?? "").toLowerCase().includes(searchLower) ||
      (hotel.location ?? "").toLowerCase().includes(searchLower)
    );
  });

  return (
    <Base>
      {/*---------------------- Hero Header Section ---------------- */}
      <div className='' style={{ backgroundColor: "#12b0cfff", color: "white", padding: "60px 0", marginBottom: '40px' }}>
        <Container className='text-center'>
          <h1 className='display-4 fw-bold'>Find Your Next Stay</h1>
          <p>Explore the best hotels with top-rated services.</p>

          {/* Mock Search Bar */}
          <div className="mx-auto mt-4" style={{ maxWidth: '600px' }}>
            <InputGroup className='shadow-lg'>
              <Input placeholder='Search by hotel name or location ...' className='border-0 p-3' style={{ borderRadius: "10px 0 0 10px" }}
                value={searchTerm} onChange={(e) => { setSearchTerm(e.target.value) }}
              />
              <Button color='dark' className='px-4' style={{ borderRadius: "0 10px 10px 0" }}>
                {searchTerm ? 'Searching...' : 'Search'}
              </Button>
            </InputGroup>
          </div>
        </Container>
      </div>

      <Container>
        <div className="d-flex justify-content-between align-items-center mb-4">
          <h2 className='fw-bold' style={{ color: '#2c3e50' }}>Featured Hotels</h2>
          {!isLoading && <span className='text-muted'>{filterHotels.length} hotels found</span>}
        </div>

        <Row>

          {isLoading ? (Array(6).fill(0).map((_, i) => <HotelShimmer key={i} />))
            : filterHotels.length > 0 ? (filterHotels.map(hotel => (
              <Hotel
                hotel={hotel}
                userId={user?.data?.userId}
                rating={ratingFor(hotel.hotelId)}
                key={hotel.hotelId}
              />
            ))
            ) : (
              <div className="text-center w-100 py-5">
                <img src="https://cdn-icons-png.flaticon.com/512/6134/6134065.png" alt="not found" style={{ width: '100px', opacity: 0.5 }} />
                <h3 className="text-muted mt-3">No hotels match your search "{searchTerm}"</h3>
                <Button color="link" onClick={() => setSearchTerm("")}>Clear all filters</Button>
              </div>
            )}

        </Row>
      </Container>
    </Base>
  )
}

export default DashBoard;