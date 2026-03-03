import React from 'react';
import { Col, Card, CardBody } from 'reactstrap';
import "./hotel.css"

const HotelShimmer = () => {
  return (
    <Col md={4} sm={6} className="mb-4">
      <Card className="border-0 shadow-sm" style={{ borderRadius: '15px', overflow: 'hidden' }}>
        {/* Shimmering Image Area */}
        <div className="shimmer-effect" style={{ height: '200px', width: '100%' }}></div>
        <CardBody>
          {/* Shimmering Title */}
          <div className="shimmer-effect mb-2" style={{ height: '24px', width: '70%', borderRadius: '4px' }}></div>
          {/* Shimmering Location */}
          <div className="shimmer-effect mb-3" style={{ height: '16px', width: '40%', borderRadius: '4px' }}></div>
          {/* Shimmering Button */}
          <div className="shimmer-effect" style={{ height: '40px', width: '100%', borderRadius: '8px' }}></div>
        </CardBody>
      </Card>
    </Col>
  );
};

export default HotelShimmer;