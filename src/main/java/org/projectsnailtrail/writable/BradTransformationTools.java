package org.projectsnailtrail.writable;



public class BradTransformationTools {
	final  static int LAST_BIT_ONLY = 0x00000001;
	final private static long LATITUDE_BITS = 0xAAAAAAAAAAAAAAAAL; // binary 101010101010...
	final private static long LONGITUDE_BITS = 0x5555555555555555L; // binary 010101010101...

	//one third and two thirds longitude include the int values of one and two thirds interleaved with
	//zero followed by all ones.  The ones insure that when the one third portion is added to a number
	//where ever other digit is zero (where every digit is one for this value) that any carry digits will
	//be passed up to the next real digit to add.  The first digit is always zero to make sure we never overflow
	//the value of max long.
	//Two thirds is twice one third plus one to round up -- imagine .333 times 2 = .667 -- this makes sure that
	//any shift operation is reversible -- 1 - .333 = .667 -- in this case 1 is the same as x00000000 and xFFFFFFF 
	//would be similar to .999 for the purposes of this analogy
	final private static long ONE_THIRD_LONGITUDE = 0xBBBBBBBBBBBBBBBBL;  // binary 1011 1011 1011 1011...
	final private static long TWO_THIRDS_LONGITUDE = 0xEEEEEEEEEEEEEEEFL; // binary 1110 1110 1110 1110... 1111
	final private static long ONE_THIRD_LATITUDE = 0x7777777777777777L;  // binary 0111 0111 0111 0111...
	final private static long TWO_THIRDS_LATITUDE = 0xDDDDDDDDDDDDDDDFL; // binary 1101 1101 1101 1101... 1111

	public static long interleave(int latitude, int longitude){
		//brute force approach -- will optimize later
		long result= 0x0000000000000000;
		int backwardsLat = Integer.reverse(latitude);
		int backwardsLong = Integer.reverse(longitude);
		for(int i=0;i<32;i++){
			result <<= 1;
			result = result | (long)(backwardsLat & LAST_BIT_ONLY);
			result <<= 1;
			result = result | (long)(backwardsLong & LAST_BIT_ONLY);
			backwardsLong >>= 1;
			backwardsLat >>=1;

		}
		return result;
	}

	public static long shiftUpRight(long interleave){
		//masking out every other bit, adding a third, then recombining with the other half
		return (((interleave & LATITUDE_BITS) + ONE_THIRD_LATITUDE) & LATITUDE_BITS) |  //this adds one third to the latitude
				(((interleave & LONGITUDE_BITS) + ONE_THIRD_LONGITUDE) & LONGITUDE_BITS); // this add one third to the longitude
	}
	public static long shiftDownLeft(long interleave){
		//masking out every other bit, adding a third, then recombining with the other half
		return (((interleave & LATITUDE_BITS) + TWO_THIRDS_LATITUDE) & LATITUDE_BITS) |  //this adds two thirds to the latitude -- the same as subtracting one third
				(((interleave & LONGITUDE_BITS) + TWO_THIRDS_LONGITUDE) & LONGITUDE_BITS); // this add one third to the longitude -- the same as subtracting one third
		//we're adding two thirds rather than subtracting one third because for some reason subtraction isn't working
		// -- probably because I'd need to zero out every other digit rather than have it be one for subtraction to carry digits over
	}
}
