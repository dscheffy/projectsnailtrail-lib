package org.projectsnailtrail.writable;

import static org.junit.Assert.*;

import org.junit.Test;

import static org.projectsnailtrail.tools.BradTransformationTools.*;

public class BradTransformationToolsTest {

	@Test
	public void testInterleave() {
		assertEquals("x00000000 and x00000000", 0x0000000000000000L, interleave(0x00000000, 0x00000000));
		assertEquals("x00000001 and x00000001", 0x0000000000000003L, interleave(0x00000001, 0x00000001));
		assertEquals("x10000001 and x10000001", 0x0300000000000003L, interleave(0x10000001, 0x10000001));
		assertEquals("x11111111 and x10101010", 0x0302030203020302L, interleave(0x11111111, 0x10101010));
		assertEquals("xFFFFFFFF and xFFFFFFFF", 0xFFFFFFFFFFFFFFFFL, interleave(0xFFFFFFFF, 0xFFFFFFFF));
		assertEquals("xFFFFFFFF and x00000000", 0xAAAAAAAAAAAAAAAAL, interleave(0xFFFFFFFF, 0x00000000));
		assertEquals("x00000000 and xFFFFFFFF", 0x5555555555555555L, interleave(0x00000000, 0xFFFFFFFF));
	}
	
	@Test
	public void testShiftUpRight(){
		//this method is supposed to add a third to the latitude and longitude without needing to 
		// "de-interleave", so the results should be the same as adding one third first, then interleaving
		
//		int latitude = 0x02468ACE;
//		int longitude = 0x13579BDF;
		int latitude = 1505482260;
		int longitude = -555555;
		int oneThird = 0x55555555; //binary 01010101...
		long interleave = interleave(latitude,longitude);
		
		assertEquals("x02468ACE and x13579BDF right", interleave(latitude+oneThird,longitude+oneThird), shiftUpRight(interleave));
		//shifting down and to the left should have the same effect as subtracting one third from each of the constituents
		assertEquals("x02468ACE and x13579BDF left", interleave(latitude-oneThird,longitude-oneThird), shiftDownLeft(interleave));
		assertEquals("shifting right should be reversible", interleave, shiftDownLeft(shiftUpRight(interleave)));
		assertEquals("shifting left should be reversible", interleave, shiftUpRight(shiftDownLeft(interleave)));
		
		latitude = 0x0;
		longitude = 0xFFFFFFFF;//FFFFFFFF;
		interleave = interleave(latitude,longitude);
		assertEquals("x00000000 and xFFFFFFFF right", interleave(latitude+oneThird,longitude+oneThird), shiftUpRight(interleave));
		assertEquals("x00000000 and xFFFFFFFF left", interleave(latitude-oneThird,longitude-oneThird), shiftDownLeft(interleave));
		assertEquals("shifting right should be reversible", interleave, shiftDownLeft(shiftUpRight(interleave)));
		assertEquals("shifting left should be reversible", interleave, shiftUpRight(shiftDownLeft(interleave)));

	}
	
	@Test
	public void testSmallestBoundingBox(){
		long interleave1 = 0x0;
		long interleave2 = 0xFFFFFFFFFFFFFFFFL;
		smallestBoundingBox(interleave1, interleave2);
	}

	@Test
	public void testAdd(){
		int lat1,lat2,long1,long2,latSum,longSum;
		long interleave1, interleave2, interleaveSum;

		lat1=1000;
		lat2=20234;
		long1=-3245;
		long2=59873;
		latSum=lat1+lat2;
		longSum=long1+long2;
		interleave1=interleave(lat1,long1);
		interleave2=interleave(lat2,long2);
		interleaveSum=interleave(latSum,longSum);
		
		assertEquals("sum of interleave should be interleave of sum", interleaveSum, add(interleave1, interleave2));
		assertEquals("add should be commutative", add(interleave1, interleave2), add(interleave2, interleave1));
		
	}
	@Test
	public void testSubtract(){
		int lat1,lat2,long1,long2,latDiff,longDiff;
		long interleave1, interleave2, interleaveDiff;

		lat1=1000;
		lat2=20234;
		long1=-3245;
		long2=59873;
		latDiff=lat1-lat2;
		longDiff=long1-long2;
		interleave1=interleave(lat1,long1);
		interleave2=interleave(lat2,long2);
		interleaveDiff=interleave(latDiff,longDiff);
		
		assertEquals("difference of interleave should be interleave of differences", interleaveDiff, subtract(interleave1, interleave2));
		
	}

}
