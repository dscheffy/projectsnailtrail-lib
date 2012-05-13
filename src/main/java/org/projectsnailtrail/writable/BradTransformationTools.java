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
	
	//these are constants for stuffing the version of the transform into the 7th and 8th last bits of a long
	final private static long TRANSFORM_MASK = 0x00000000000000C0L; // binary ...0000 1100 0000
	final private static int TRANSFORM_NONE = 0x0000000000000000; // binary ...0000 0000 0000
	final private static int TRANSFORM_UP_RIGHT = 0x0000000000000040; // binary ...0000 0100 0000
	final private static int TRANSFORM_DOWN_LEFT = 0x0000000000000080; // binary ...0000 1000 0000

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
	public static long add(long interleave1, long interleave2){
		//for adding and subtracting, we need to handle the latitudes and longitudes one at a time, masking
		//out the opposing values during each sub operation.  specifically for addition, it is important that
		//the masked bits in one value be set to 1 and the masked bits for the other be set to 0 -- this way
		//any bits that need to be carried by the addition will pass up to the next bit.  For subtraction, both
		//values need to be 0 so that the negative one value will be carried past the bit.
		return
			(((interleave1 & LATITUDE_BITS) + //only the latitude bits of interleave 1, all longitude bits are zero
			(interleave2 | LONGITUDE_BITS))  //all the latitude bits of interelave2 with all longitude bits as one (turned on)
			& LATITUDE_BITS)  // this takes only the latitude bits of the summation results which is the sum of the latitudes
			|
			(((interleave1 & LONGITUDE_BITS) +
			(interleave2 | LATITUDE_BITS))
			& LONGITUDE_BITS);
	}
	public static long subtract(long interleave1, long interleave2){
		//for adding and subtracting, we need to handle the latitudes and longitudes one at a time, masking
		//out the opposing values during each sub operation.  specifically for addition, it is important that
		//the masked bits in one value be set to 1 and the masked bits for the other be set to 0 -- this way
		//any bits that need to be carried by the addition will pass up to the next bit.  For subtraction, both
		//values need to be 0 so that the negative one value will be carried past the bit.
		return
			(((interleave1 & LATITUDE_BITS) - //only the latitude bits of interleave 1, all longitude bits are zero
			(interleave2 & LATITUDE_BITS))  //only the latitude bits of interelave2, all longitude bits are zero
			& LATITUDE_BITS)  // this takes only the latitude bits of the summation results which is the sum of the latitudes
			|
			(((interleave1 & LONGITUDE_BITS) -
			(interleave2 & LONGITUDE_BITS))
			& LONGITUDE_BITS);
		
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
	public static long smallestBoundingBox(long interleave1, long interleave2){
		int commonBits = Long.numberOfLeadingZeros(interleave1 ^ interleave2);
		int transform=TRANSFORM_NONE;
		long boundingBox=interleave1;
		if(commonBits<56){
			int temp;
			long tempInterleave1=shiftUpRight(interleave1);
			long tempInterleave2=shiftUpRight(interleave2);
			temp = Long.numberOfLeadingZeros(tempInterleave1 ^ tempInterleave2);
			if(temp > commonBits){
				commonBits=temp;
				transform=TRANSFORM_UP_RIGHT;
				boundingBox=tempInterleave1;
			}
			if(commonBits<56){
				tempInterleave1=shiftDownLeft(interleave1);
				tempInterleave2=shiftDownLeft(interleave2);
				temp = Long.numberOfLeadingZeros(tempInterleave1 ^ tempInterleave2);
				if(temp > commonBits){
					commonBits=temp;
					transform=TRANSFORM_DOWN_LEFT;
					boundingBox=tempInterleave1;
				}
			}
		}
		if(commonBits>56)commonBits=56;  //leave at minimum 8 bits at the end for significant digits and transform
		boundingBox = boundingBox & (Long.MIN_VALUE >> (commonBits-1)); //zero out all bits after commonBits -- long.min_value is 1 followed by all zeros
		boundingBox = boundingBox | commonBits; //since common bits is never greater than 56, it will never take up more than last 6 bits
		boundingBox = boundingBox | transform;
		return boundingBox;
	}
}
