import { privateAxios } from "./Helper"

export const getAllRatings = async ()=>{
    try {
        const resp = await privateAxios.get("/api/v3/rating/allratings");
        return resp.data;
    } catch (error) {
        if (error.response && error.response.status === 503) {
            console.warn("Service is down. Returning client-side data.");
            
            const dummyRatingsData = [
                {
                    ratingId:"FALLBACK-001",
                    userId:"Dummy User",
                    hotelId:"System Offline",
                    rating:0,
                    feedback:"Rating data is temprarory unavailable."
                }
            ]

            return dummyRatingsData;
        } else if(error.response && error.response.status === 403){
            console.error("Access Denied! Admin required!!");
            throw error;
        }else{
            throw error;
        }
    }
};

export const getRatingsByHotelId = async (hotelId)=>{
    try {
        const resp = await privateAxios.get(`/api/v3/rating/hotels/${hotelId}`);;
        return resp.data;
    } catch (error) {
        if (error.response && error.response.status === 503) {
            console.warn("Service is down. Returning client-side data.");
            
            const dummyRatingsData = [
                {
                    ratingId:"FALLBACK-001",
                    userId:"Dummy User",
                    hotelId:"System Offline",
                    rating:0,
                    feedback:"Rating data is temprarory unavailable."
                }
            ]

            return dummyRatingsData;
        } else if(error.response && error.response.status === 403){
            console.error("Access Denied! Admin required!!");
            throw error;
        }else{
            throw error;
        }
    }
};

export const saveRatings =(addRating)=>{
    return privateAxios.post(`/api/v3/rating/add`, addRating).then(resp => resp.data);
};

export const deleteRating = async (ratingId) =>{
    try {
        const resp = await privateAxios.delete(`/api/v3/rating/delete/${ratingId}`);
        return resp.data;
    } catch (error) {
        throw error;
    }
};

/**
* New: Average rating and review count for hotel
* Return { status: 'ready', average: count} (average is null when there is no reviews)
* Throws when the rating service is down( like fallback no real rating)
**/
export const getHotelRatingSummary = async (hotelId)=>{
    const list = await getRatingsByHotelId(hotelId);
    const all = Array.isArray(list) ? list : [] ;
    const real = all.filter( r => !String(r.ratingId).startsWith("FALLBACK"));

    if(all.length > 0 && real === 0){
        throw new Error("Rating service unavailable");
    }
    
    const count = real.length;
    const total = real.reduce((sum, r) => sum + Number(r.rating || 0), 0);
    return {status: 'ready', average: count> 0 ? total / count : null, count}
};