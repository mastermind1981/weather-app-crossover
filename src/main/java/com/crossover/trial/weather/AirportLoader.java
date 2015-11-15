package com.crossover.trial.weather;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import java.io.*;

/**
 * A simple airport loader which reads a file from disk and sends entries to the
 * webservice
 *
 * TODO: Implement the Airport Loader
 * 
 * @author code test administrator
 */
public class AirportLoader {

    /** end point for read queries */
    private WebTarget query;

    /** end point to supply updates */
    private WebTarget collect;

    private WebTarget airport;

    public AirportLoader() {
	Client client = ClientBuilder.newClient();
	query = client.target("http://localhost:8080/query");
	collect = client.target("http://localhost:8080/collect");
	airport = client.target("http://localhost:8080/collect/airport");
    }

    public void upload(InputStream airportDataStream) throws IOException {
	BufferedReader reader = new BufferedReader(new InputStreamReader(airportDataStream));
	String l = null;
	while ((l = reader.readLine()) != null) {
	    String[] split = l.replaceAll("\"", "").split(",");
	    if (split != null && split.length == 11) {
		try {
		    AirportData airportData = new AirportData(split[4], Double.valueOf(split[6]),
			    Double.valueOf(split[7]));
		    airportData.setId(Integer.valueOf(split[0]));
		    airportData.setName(split[1]);
		    airportData.setCity(split[2]);
		    airportData.setCountry(split[3]);
		    airportData.setFaaCode(split[5]);
		    airportData.setAltitude(Double.valueOf(split[8]));
		    airportData.setTimezone(Double.valueOf(split[9]));
		    airportData.setDst(split[10]);
		    String path = "/".concat(split[4]).concat("/").concat(split[6]).concat("/").concat(split[7]);
		   WebTarget target = airport.path(path);
		    
		   target.request().post(Entity.entity(airportData, "application/json"));

		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }

	}
    }

    public static void main(String args[]) throws IOException {
	File airportDataFile = new File(args[0]);
	if (!airportDataFile.exists() || airportDataFile.length() == 0) {
	    System.err.println(airportDataFile + " is not a valid input");
	    System.exit(1);
	}

	AirportLoader al = new AirportLoader();
	al.upload(new FileInputStream(airportDataFile));
	System.exit(0);
    }
}
