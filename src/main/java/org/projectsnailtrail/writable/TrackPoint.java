package org.projectsnailtrail.writable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class TrackPoint {

	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		hasLatitude=true;
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		hasLongitude=true;
		this.longitude = longitude;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		hasTimestamp=true;
		this.timestamp = timestamp;
	}
	public int getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(int accuracy) {
		hasAccuracy=true;
		this.accuracy = accuracy;
	}
	public boolean hasLatitude() {
		return hasLatitude;
	}
	public boolean hasLongitude() {
		return hasLongitude;
	}
	public boolean hasTimestamp() {
		return hasTimestamp;
	}
	public boolean hasAccuracy() {
		return hasAccuracy;
	}
	public void setGps(boolean isGps){
		gps=isGps;
	}
	public boolean isGps(){
		return gps;
	}
	private boolean gps=false;  //this is a temporary hack so I can keep track of location provider type until I come up with a better way of doing it
	
	private double latitude;
	private double longitude;
	private long timestamp;
	private int accuracy;
	private boolean hasLatitude;
	private boolean hasLongitude;
	private boolean hasTimestamp;
	private boolean hasAccuracy;
	private final static byte VERSION = 1;
	
	public void write(OutputStream os) throws IOException {
		//one day I'll make this more efficient, for  now it'll just live as V1
		//really the booleans only need a bit each instead of a byte, four bits would suffice for accuracy if I make it a logrythmic and version could probably be limited if need be
		DataOutputStream dos = new DataOutputStream(os);
		dos.writeByte(VERSION);
		dos.writeBoolean(hasAccuracy);
		dos.writeBoolean(hasLatitude);
		dos.writeBoolean(hasLongitude);
		dos.writeBoolean(hasTimestamp);
		if(hasAccuracy) dos.writeInt(accuracy);
		if(hasLatitude) dos.writeInt(convertDoubleToInt(latitude));
		if(hasLongitude) dos.writeInt(convertDoubleToInt(longitude));
		if(hasTimestamp) dos.writeLong(timestamp);
		dos.flush(); //hmm, is it being a good citizen to do this here?
	}
	public void read(InputStream is) throws IOException {
		DataInputStream dis = new DataInputStream(is);
		byte version = dis.readByte();
		if(version>VERSION) throw new IOException("Input contains a version of serialization that is higher than the current one supported: " + version + ">" + VERSION);
		if(version==1){
			hasAccuracy = dis.readBoolean();
			hasLatitude = dis.readBoolean();
			hasLongitude = dis.readBoolean();
			hasTimestamp = dis.readBoolean();
			if(hasAccuracy) accuracy = dis.readInt();
			if(hasLatitude) latitude = convertIntToDouble(dis.readInt());
			if(hasLongitude) longitude = convertIntToDouble(dis.readInt());
			if(hasTimestamp) timestamp = dis.readLong();
		}
		
	}
	public String toString(){
		StringBuilder sb = new StringBuilder(200);
		if(hasTimestamp) {
			sb.append(new Date(timestamp).toString());
			sb.append('\n');
		}
		if(gps){
			sb.append("GPS\n");
		}else{
			sb.append("NETWORK\n");
		}
		if(hasLatitude){
			sb.append(latitude);
			sb.append(", ");
		}
		if(hasLongitude){
			sb.append(longitude);
		}
		if(hasAccuracy){
			sb.append('\n');
			sb.append(accuracy);
		}
		
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
	public static Iterable<TrackPoint> iterate(final InputStream is){
		return new Iterable<TrackPoint>(){

			@Override
			public Iterator<TrackPoint> iterator() {
				
				return new Iterator<TrackPoint>() {
					boolean more;
					TrackPoint next;

					@Override
					public boolean hasNext() {
						if(!more) {
							next = new TrackPoint();
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
					public TrackPoint next() {
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
