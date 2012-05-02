package org.projectsnailtrail.writable;

import static org.junit.Assert.*;

import org.junit.Test;
import static org.projectsnailtrail.writable.BradTransformationTools.*;

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
		
		assertEquals("x02468ACE and x13579BDF", interleave(latitude+oneThird,longitude+oneThird), shiftUpRight(interleave));
		assertEquals("shifting right should be reversible", interleave, shiftDownLeft(shiftUpRight(interleave)));
		assertEquals("shifting left should be reversible", interleave, shiftUpRight(shiftDownLeft(interleave)));
		
		latitude = 0x0;
		longitude = 0xFFFFFFFF;//FFFFFFFF;
		interleave = interleave(latitude,longitude);
		assertEquals("x00000000 and xFFFFFFFF", interleave(latitude+oneThird,longitude+oneThird), shiftUpRight(interleave));
		assertEquals("shifting right should be reversible", interleave, shiftDownLeft(shiftUpRight(interleave)));
		assertEquals("shifting left should be reversible", interleave, shiftUpRight(shiftDownLeft(interleave)));

	}

}
