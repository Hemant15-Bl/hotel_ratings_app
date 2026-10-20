import { Badge, Button, Card, CardBody, CardSubtitle, CardText, CardTitle, Col, Row } from 'reactstrap';
import { Link } from 'react-router-dom';
import { getHotelImage } from '../services/Hotel-service';
import { useEffect, useState } from 'react';
import "./hotel.css"
import { getHotelRatingSummary } from '../services/Rating-service';

// Highest score a guest can give (your stored ratings go up to 10)
const MAX_RATING = 10;

/**
 * rating (optional): { status: 'loading' | 'ready' | 'error', average, count }
 * The dashboard passes it in so all ratings load in one request.
 * If it is not passed, the card loads its own hotel's rating.
 */
const Hotel = ({ hotel, userId, rating }) => {

    const themeColor = "#12b0cfff";

    const [imageUrl, setImageUrl] = useState(null);
    const [ownSummary, setOwnSummary] = useState({ status: 'loading', average: null, count: 0 });

    const hasRatingProp = rating !== undefined;

    // ---------------- Image ----------------
    useEffect(() => {

        if (!hotel || !hotel.imageName) {
            return;
        }

        let active = true;
        let objectUrl = null;


        getHotelImage(hotel.imageName).then(url => {
            if (active) {
                objectUrl = url;
                setImageUrl(url);
            } else if (url) {
                URL.revokeObjectURL(url);
            }
        })
            .catch(err => console.error(err));

        // frees the blob URL when the card goes away or the image changes    
        return () => {
            active = false;
            if (objectUrl) URL.revokeObjectURL(objectUrl);
        }
    }, [hotel?.imageName]);


    // ---------------- Rating (only when the page did not pass one) ----------------
    useEffect(() => {
        if (hasRatingProp || !hotel?.hotelId) {
            return;
        }

        let active = true;
        setOwnSummary({ status: 'loading', average: null, count: 0 });

        getHotelRatingSummary(hotel.hotelId)
            .then(summary => { if (active) setOwnSummary(summary); })
            .catch(err => {
                console.error("Could not load rating for hotel", hotel.hotelId, err);
                if (active) setOwnSummary({ status: 'error', average: null, count: 0 });
            });

        return () => { active = false; };
    }, [hasRatingProp, hotel?.hotelId]);

    // What the badge shows
    const { status, average, count } = hasRatingProp ? rating : ownSummary;
    let badgeText;
    let badgeTitle;

    if (status === 'loading') {
        badgeText = '⭐ …';
        badgeTitle = 'Loading rating';
    } else if (status === 'error') {
        badgeText = '⭐ –';
        badgeTitle = 'Rating unavailable';
    } else if (count === 0) {
        badgeText = 'New';
        badgeTitle = 'No ratings yet';
    } else {
        badgeText = `⭐ ${average.toFixed(1)}`;
        badgeTitle = `${average.toFixed(1)} out of ${MAX_RATING} from ${count} review${count === 1 ? '' : 's'}`;
    }

    return (

        <>
            <Col className='mb-4' lg={4} md={6} sm={12}>
                <Card className='border-0 shadow-sm h-100 hotel-card' style={{ borderRadius: '15px', overflow: 'hidden', transition: 'transform 0.3s ease' }}>
                    {/* ---------- Image Section --------------- */}
                    <div style={{ position: 'relative', height: '200px', overflow: 'hidden' }}>
                        <img alt={hotel?.name} src={imageUrl} style={{ height: '100%', width: '100%', objectFit: 'cover' }} />
                        <Badge color='light'
                            className='position-absolute text-dark'
                            title={badgeTitle}
                            style={{ top: '10px', right: '10px', opacity: '0.9' }} >
                            {badgeText}
                            {status === 'ready' && count > 0 && (
                                <span className='text-muted ms-1'>({count})</span>
                            )}
                        </Badge>
                    </div>

                    <CardBody className='d-flex flex-column'>
                        <CardTitle tag="h5" className='fw-bold mb-1' style={{ color: '#2c3e50' }}>
                            {hotel.name}
                        </CardTitle>

                        <CardSubtitle className="mb-3 text-muted small" tag="h5">
                            <i className='bi bi-geo-alt-fill'></i> {hotel.location}
                        </CardSubtitle>

                        <CardText className="text-secondary small flex-grow-1" dangerouslySetInnerHTML={{ __html: hotel.about.substring(0, 40) + "..." }} />

                        <div className="mt-3 d-flex justify-content-between align-items-center">

                        </div>
                        <Button tag={Link} to={`/viewhotel/${hotel.hotelId}/${userId}`} className='btn-sm border-0 px-3' style={{ backgroundColor: themeColor, borderRadius: "8px" }}>
                            View Details
                        </Button>
                    </CardBody>
                </Card>

            </Col>



        </>

    )
}

export default Hotel;