package com.crossover.trial.weather;

import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.crossover.trial.weather.RestWeatherQueryEndpoint.*;

/**
 * A REST implementation of the WeatherCollector API. Accessible only to airport
 * weather collection sites via secure VPN.
 *
 * @author code test administrator
 */

@Path("/collect")
public class RestWeatherCollectorEndpoint implements WeatherCollector {
    // CR: restrict access of member or instance variable and reorder for better
    // readability
    private static final Logger LOGGER = Logger.getLogger(RestWeatherCollectorEndpoint.class.getName());

    /** shared gson json to object factory */
    // CR: restrict access of member or instance variable and reorder for better
    // readability
    private static final Gson gson = new Gson();

    static {
	init();
    }

    @GET
    @Path("/ping")
    @Override
    public Response ping() {
	return Response.status(Response.Status.OK).entity("ready").build();
    }

    @POST
    @Path("/weather/{iata}/{pointType}")
    @Override
    public Response updateWeather(@PathParam("iata") String iataCode, @PathParam("pointType") String pointType,
	    String datapointJson) {
	try {
	    addDataPoint(iataCode, pointType, gson.fromJson(datapointJson, DataPoint.class));
	} catch (WeatherException e) {
	    // CR: log the error - avoid stack trace to stdout in production
	    LOGGER.fine(e.getMessage());

	}
	return Response.status(Response.Status.OK).build();
    }

    @GET
    @Path("/airports")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getAirports() {
	Set<String> retval = new HashSet<>();
	for (AirportData ad : airportData) {
	    retval.add(ad.getIata());
	}
	return Response.status(Response.Status.OK).entity(retval).build();
    }

    @GET
    @Path("/airport/{iata}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getAirport(@PathParam("iata") String iata) {
	AirportData ad = findAirportData(iata);
	return Response.status(Response.Status.OK).entity(ad).build();
    }

    @POST
    @Path("/airport/{iata}/{lat}/{long}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Override
    public Response addAirport(@PathParam("iata") String iata, @PathParam("lat") String latString,
	    @PathParam("long") String longString) {
	
	Double lat = null;
	Double log = null;

	try{
	    
	   lat = Double.valueOf(latString);
	   log = Double.valueOf(latString);
	   
	   if(iata.length() != 3 ||iata.length() != 4){
	      return Response.status(Response.Status.NOT_ACCEPTABLE).build();
	   }
	    
	}catch(Exception e){
	    return Response.status(Response.Status.NOT_ACCEPTABLE).build();
	}
	
	addAirport(iata, lat, log);
	return Response.status(Response.Status.OK).build();
    }

    @DELETE
    @Path("/airport/{iata}")
    @Override
    public Response deleteAirport(@PathParam("iata") String iata) {
	deleteAirportData(iata);
	return Response.status(Response.Status.OK).build();
    }

    //
    // Internal support methods
    //

    /**
     * Update the airports weather data with the collected data.
     *
     * @param iataCode
     *            the 3 letter IATA code
     * @param pointType
     *            the point type {@link DataPointType}
     * @param dp
     *            a datapoint object holding pointType data
     *
     * @throws WeatherException
     *             if the update can not be completed
     */
    public void addDataPoint(String iataCode, String pointType, DataPoint dp) throws WeatherException {
	int airportDataIdx = getAirportDataIdx(iataCode);
	AtmosphericInformation ai = atmosphericInformation.get(airportDataIdx);
	updateAtmosphericInformation(ai, pointType, dp);
    }

    /**
     * update atmospheric information with the given data point for the given
     * point type
     *
     * @param ai
     *            the atmospheric information object to update
     * @param pointType
     *            the data point type as a string
     * @param dp
     *            the actual data point
     */
    public void updateAtmosphericInformation(AtmosphericInformation ai, String pointType, DataPoint dp)
	    throws WeatherException {
	// CR: Remove unused declaration
	// CR: too many if-else conditions creates complexity
	if (pointType.equalsIgnoreCase(DataPointType.WIND.name()) && dp.getMean() >= 0) {
	    ai.setWind(dp);
	    ai.setLastUpdateTime(System.currentTimeMillis());
	    return;

	}

	if (pointType.equalsIgnoreCase(DataPointType.TEMPERATURE.name())
		&& (dp.getMean() >= -50 && dp.getMean() < 100)) {
	    ai.setTemperature(dp);
	    ai.setLastUpdateTime(System.currentTimeMillis());
	    return;

	}

	if (pointType.equalsIgnoreCase(DataPointType.HUMIDTY.name()) && (dp.getMean() >= 0 && dp.getMean() < 100)) {

	    ai.setHumidity(dp);
	    ai.setLastUpdateTime(System.currentTimeMillis());
	    return;

	}

	if (pointType.equalsIgnoreCase(DataPointType.PRESSURE.name()) && (dp.getMean() >= 650 && dp.getMean() < 800)) {

	    ai.setPressure(dp);
	    ai.setLastUpdateTime(System.currentTimeMillis());
	    return;

	}

	if (pointType.equalsIgnoreCase(DataPointType.CLOUDCOVER.name()) && (dp.getMean() >= 0 && dp.getMean() < 100)) {

	    ai.setCloudCover(dp);
	    ai.setLastUpdateTime(System.currentTimeMillis());
	    return;

	}

	if (pointType.equalsIgnoreCase(DataPointType.PRECIPITATION.name())
		&& (dp.getMean() >= 0 && dp.getMean() < 100)) {

	    ai.setPrecipitation(dp);
	    ai.setLastUpdateTime(System.currentTimeMillis());
	    return;

	}

	throw new IllegalStateException("couldn't update atmospheric data");
    }

    /**
     * Add a new known airport to our list.
     *
     * @param iataCode
     *            3 letter code
     * @param latitude
     *            in degrees
     * @param longitude
     *            in degrees
     *
     * @return the added airport
     */
    public static AirportData addAirport(String iataCode, double latitude, double longitude) {
	AirportData ad = new AirportData();
	ad.setIata(iataCode);
	ad.setLatitude(latitude);
	ad.setLatitude(longitude);
	airportData.add(ad);
	
	return ad;
    }

    /**
     * Delete an Airport data from the list
     * 
     * @param iata
     */
    private void deleteAirportData(String iata) {
	List<AirportData> list = airportData.stream().filter(e -> e.getIata().equals(iata))
		.collect(Collectors.toList());
	if (!list.isEmpty()) {
	    // if the airport data exists, delete it, otherwise skip it
	    airportData.removeAll(list);

	}
    }

    /**
     * A dummy init method that loads hard coded data
     */
    protected static void init() {
	airportData.clear();
	atmosphericInformation.clear();
	requestFrequency.clear();
	InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("airports.dat");
	BufferedReader br = new BufferedReader(new InputStreamReader(is));
	String l = null;

	try {
	    while ((l = br.readLine()) != null) {
		String[] split = l.split(",");
		addAirport(split[0], Double.valueOf(split[1]), Double.valueOf(split[2]));
	    }
	} catch (IOException e) {
	    // CR: log the error - avoid stack trace to stdout in production
	    LOGGER.fine(e.getMessage());
	}
    }

}
