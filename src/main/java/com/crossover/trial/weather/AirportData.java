package com.crossover.trial.weather;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Basic airport information.
 *
 * @author code test administrator
 */
public class AirportData {

    Integer id;

    String name;

    String city;

    String country;

    String faaCode;

    /** the three letter IATA code */
    String iata;

    /** latitude value in degrees */
    double latitude;

    /** longitude value in degrees */
    double longitude;
    
    double altitude;

    double timezone;

    String dst;

    public AirportData() {
	super();
    }

    public AirportData(String iata, double latitude, double longitude) {
	this.iata = iata;
	this.latitude = latitude;
	this.longitude = longitude;
    }

    public String getIata() {
	return iata;
    }

    public void setIata(String iata) {
	this.iata = iata;
    }

    public double getLatitude() {
	return latitude;
    }

    public void setLatitude(double latitude) {
	this.latitude = latitude;
    }

    public double getLongitude() {
	return longitude;
    }

    public void setLongitude(double longitude) {
	this.longitude = longitude;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getCity() {
	return city;
    }

    public void setCity(String city) {
	this.city = city;
    }

    public String getCountry() {
	return country;
    }

    public void setCountry(String country) {
	this.country = country;
    }

    public String getFaaCode() {
	return faaCode;
    }

    public void setFaaCode(String faaCode) {
	this.faaCode = faaCode;
    }

    public double getTimezone() {
	return timezone;
    }

    public void setTimezone(double timezone) {
	this.timezone = timezone;
    }

    public String getDst() {
	return dst;
    }

    public void setDst(String dst) {
	this.dst = dst;
    }

    public Integer getId() {
	return id;
    }

    public void setId(Integer id) {
	this.id = id;
    }
    

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    // CR: Add @override if a method is overridden from base
    @Override
    public String toString() {
	return ReflectionToStringBuilder.toString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }

    // CR: equals method must override with hash code method and @override
    // annotation
    @Override
    public boolean equals(Object other) {
	if (other instanceof AirportData) {
	    return ((AirportData) other).getIata().equals(this.getIata());
	}

	return false;
    }

    @Override
    public int hashCode() {
	return this.hashCode();
    }

}
