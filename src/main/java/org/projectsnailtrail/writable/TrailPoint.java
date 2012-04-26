package org.projectsnailtrail.writable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class TrailPoint {
	
	private boolean gps=false;  //this is a temporary hack so I can keep track of location provider type until I come up with a better way of doing it
	
	private int geoLatitude;
	private int geoLongitude;
	private long timestamp;
	private int accuracy;

	public double getLatitude() {
		return convertIntToDouble(geoLatitude);
	}
	public int getLatitudeAsMicroDegrees() {
		return convertIntToMicroDegrees(geoLatitude);
	}
	public void setLatitude(double latitude) {
		geoLatitude = convertDoubleToInt(latitude);
	}

	public double getLongitude() {
		return convertIntToDouble(geoLongitude);
	}
	public int getLongitudeAsMicroDegrees(){
		return convertIntToMicroDegrees(geoLongitude);
	}
	public void setLongitude(double longitude) {
		geoLongitude = convertDoubleToInt(longitude);
	}

	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(int accuracy) {
		this.accuracy = accuracy;
	}

	public void setGps(boolean isGps){
		gps=isGps;
	}
	public boolean isGps(){
		return gps;
	}
	
	public void write(OutputStream os) throws IOException {
		DataOutputStream dos = new DataOutputStream(os);
		dos.writeInt(accuracy);
		dos.writeInt(geoLatitude);
		dos.writeInt(geoLongitude);
		dos.writeLong(timestamp);
		dos.flush(); //hmm, is it being a good citizen to do this here?
	}
	public void read(InputStream is) throws IOException {
		DataInputStream dis = new DataInputStream(is);
		accuracy = dis.readInt();
		geoLatitude = dis.readInt();
		geoLongitude = dis.readInt();
		timestamp = dis.readLong();
	}
		
	public String toString(){
		StringBuilder sb = new StringBuilder(200);
		sb.append(new Date(timestamp).toString());
		sb.append('\n');
		if(gps){
			sb.append("GPS\n");
		}else{
			sb.append("NETWORK\n");
		}
		sb.append(getLatitude());
		sb.append(", ");
		sb.append(getLongitude());
		sb.append('\n');
		sb.append(accuracy);
		
		return sb.toString();
	}
	
	public static int convertDoubleToInt(double d){
		if(d==180){
			d=-180;
		}
		if(d>180||d<-180){
			//need to handle this with some kind of out of bounds exception or pass back something that makes sense. 
			//not sure 270 would validly be convertable to -90...
			throw new RuntimeException("Value is out of bounds -- not a valid latitude or longitude: "+String.valueOf(d));
		}
		return (int)((-d / 180) * Integer.MIN_VALUE);

	}
	public static double convertIntToDouble(int i){
		//Don't need to worry about out of bounds situations here because every int maps to a specific double
		//well, that's not entirely true -- latitudes above 90 or below -90 aren't legitimate, but this is
		//a generic converter that handles longitudes as well.
		return -(((double)i)/Integer.MIN_VALUE * 180);
		
	}
	public static int convertIntToMicroDegrees(int internal){
		return (int)(convertIntToDouble(internal)*1000000);
	}
	public static Iterable<TrailPoint> iterate(final InputStream is){
		return new Iterable<TrailPoint>(){

			@Override
			public Iterator<TrailPoint> iterator() {
				
				return new Iterator<TrailPoint>() {
					boolean more;
					TrailPoint next;

					@Override
					public boolean hasNext() {
						if(!more) {
							next = new TrailPoint();
							try{
								next.read(is);
								more = true;
							}catch(IOException ioe){
								more =false;
								next =null;
								//assume we're done with the stream (although i suppose other problems could cause this)
								//I realize this is an expensive way to signal we're done reading the stream -- may refactor later on
							}
						}
						return more;
					}

					@Override
					public TrailPoint next() {
						if(!more){
							if(!hasNext()) throw new NoSuchElementException();
						}
						more=false;
						return next;
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException("remove is not supported by TrackPoint iterator");
						
					}
					
				};
			}
			
		};
		
	}
}
