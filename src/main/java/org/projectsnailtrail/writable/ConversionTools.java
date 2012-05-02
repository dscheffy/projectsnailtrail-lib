package org.projectsnailtrail.writable;

public class ConversionTools {
	static int convertDegreesToBrad(double degrees){
		if(degrees==180){
			degrees=-180;
		}
		if(degrees>180||degrees<-180){
			//need to handle this with some kind of out of bounds exception or pass back something that makes sense. 
			//not sure 270 would validly be convertable to -90...
			throw new RuntimeException("Value is out of bounds -- not a valid latitude or longitude: "+String.valueOf(degrees));
		}
		return (int)((-degrees / 180) * Integer.MIN_VALUE);

	}
	public static double convertBradToDegrees(int brad){
		//Don't need to worry about out of bounds situations here because every int maps to a specific double
		//well, that's not entirely true -- latitudes above 90 or below -90 aren't legitimate, but this is
		//a generic converter that handles longitudes as well.
		return -(((double)brad)/Integer.MIN_VALUE * 180);
		
	}
	public static int convertBradToMicrodegrees(int brad){
		long temp = brad;
		temp*=360000000; //multiply by 360 million (distinct number of micro degrees)
		//It's a bit confusing since we're using signed ints in java, but all values should fit and
		//using the >>> instead of >> shift operator should insure we maintain sign and get back values
		//between -180 and 180 instead of 0 and 360
		
		temp >>>= 31; //divide by 2^32 (distinct number of binary radian values in a 32 bit int)
		temp++;  //this and the next line just make sure we round up rather than truncate
		temp>>>=1;
		return (int)temp;
		
	}
	public static int convertMicrodegreesToBrad(int md){
		
		long temp = md;
		temp <<=32;  //multiply by 2^32
		temp /=360000000L; //divide by 360 million
		return (int)temp;
	}

}
