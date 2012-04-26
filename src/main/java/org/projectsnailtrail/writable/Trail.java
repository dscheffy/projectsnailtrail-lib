package org.projectsnailtrail.writable;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Trail {
	private final static int INITIAL_CAPACITY = 100;
	private final static int CAPACITY_MARGIN = 5;
	private int currentCapacity;
	private int size;
	private int[] latitude;
	private int[] longitude;
	private long[] timestamp;
	private int[]  accuracy;
	private byte[] provider;
	public static int GPS_PROVIDER = 1;
	public static int NETWORK_PROVIDER = 2;
	
	public Trail(){
		size=0;
		latitude = new int[INITIAL_CAPACITY];
		longitude = new int[INITIAL_CAPACITY];
		timestamp = new long[INITIAL_CAPACITY];
		accuracy = new int[INITIAL_CAPACITY];
		provider = new byte[INITIAL_CAPACITY];
	}
	public void clear(){
		size=0;
	}
	public boolean isNearCapacity(){
		return size > currentCapacity - CAPACITY_MARGIN;
	}
	public boolean add(TrackPoint tp){
		if(size==currentCapacity){
			return false; //for now let's just return false to indicate the track point wasn't added
			//do something -- either grow or throw an exception
		}
		latitude[size]=TrackPoint.convertDoubleToInt(tp.getLatitude());
		longitude[size]=TrackPoint.convertDoubleToInt(tp.getLongitude());
		timestamp[size]=tp.getTimestamp();
		accuracy[size]=tp.getAccuracy();
		if(tp.isGps()) {provider[size]=(byte)GPS_PROVIDER;} else {provider[size]=(byte)NETWORK_PROVIDER;}
		size++;
		return true;
	}
	

	public int getLatitude(int index) {
		if(index>=size) throw new OutOfBoundsException(index);
		return latitude[index];
	}
	public int getLongitude(int index){
		if(index>=size) throw new OutOfBoundsException(index);
		return longitude[index];
	}
	public static int convertToMicroDegrees(int geohash){
		return (int)(TrackPoint.convertIntToDouble(geohash)*1000000);
	}
	public void addTrackPoint(TrackPoint tp){
		
	}
	public void write(OutputStream os) throws IOException {
		DataOutputStream dos = new DataOutputStream(os);
		dos.writeInt(size);
		for(int i=0;i<size;i++){
			dos.writeInt(latitude[i]);
			dos.writeInt(longitude[i]);
			dos.writeLong(timestamp[i]);
			dos.writeInt(accuracy[i]);
		}
		
	}
	public class OutOfBoundsException extends RuntimeException{
		OutOfBoundsException(int index){
			super("The index: " + index + " is greater than the size of the trail: " + size);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = -8966311467093935690L;
		
	}
}
