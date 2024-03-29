package com.crossover.trial.weather;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * A reference implementation for the weather client. Consumers of the REST API can look at WeatherClient
 * to understand API semantics. This existing client populates the REST endpoint with dummy data useful for
 * testing.
 *
 * @author code test administrator
 */
public class WeatherClient {

    private static final String BASE_URI = "http://localhost:8080";
    /** end point for read queries */
    private WebTarget query;

    /** end point to supply updates */
    private WebTarget collect;
    
    private WebTarget airport;

    public WeatherClient() {
        Client client = ClientBuilder.newClient();
        query = client.target(BASE_URI + "/query");
        collect = client.target("http://localhost:8080/collect");
        airport = client.target("http://localhost:8080/collect" + "/airport");
    }

    public void pingCollect() {
        WebTarget path = collect.path("/ping");
        Response response = path.request().get();
        System.out.print("collect.ping: " + response.readEntity(String.class) + "\n");
    }

    public void pingQuery() {
        WebTarget path = query.path("/ping");
        Response response = path.request().get();
        System.out.println("query.ping: " + response.readEntity(String.class));
    }

    public void populate() {
        WebTarget path = collect.path("/weather/BOS/wind");
        DataPoint dp = new DataPoint.Builder()
                .withFirst(0).withLast(10).withMean(4).withMedian(4).withCount(10)
                .build();
        Response post = path.request().post(Entity.entity(dp, "application/json"));
    }

    public void query() {
        WebTarget path = query.path("/weather/BOS/0");
        Response response = path.request().get();
        System.out.println("query.get:" + response.readEntity(String.class));
    }
    
    public void getAirport(){
	WebTarget path = airport.path("/MMU");
	Response response = path.request().get();
	 System.out.println("query.get:" + response.readEntity(String.class));
    }
    
    public void addAirport(){
	WebTarget path = airport.path("/TES/-1/-1");
	
	AirportData airportData = new AirportData("TES", -0.1, 2323.0);
	
	Response response = path.request().post(Entity.entity(airportData, "application/json"));
	 System.out.println("query.get:" + response.readEntity(String.class));
    }

    public void deleteAirport(){
	WebTarget path = airport.path("/TES");
	
	Response response = path.request().delete();
	 System.out.println("query.get:" + response.readEntity(String.class));
    }
    
    public static void main(String[] args) {
        WeatherClient wc = new WeatherClient();
        //wc.pingCollect();
       // wc.populate();
        //wc.query();
        //wc.pingQuery();
        wc.getAirport();
        wc.addAirport();
        wc.getAirport();
        //wc.deleteAirport();
        wc.getAirport();
        System.out.print("complete");
        System.exit(0);
    }
}
