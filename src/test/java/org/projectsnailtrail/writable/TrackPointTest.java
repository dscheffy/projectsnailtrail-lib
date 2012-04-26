package org.projectsnailtrail.writable;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Test;

public class TrackPointTest {
	//@Test
	public void xtestStuff() {
		String fileName = "bla/bla/snail_123455.xyz";
		String time = fileName.substring(fileName.lastIndexOf('_')+1,fileName.lastIndexOf('.'));
		System.out.println(time);
	}

	@Test
	public void testSerialization() throws Exception {
		TrackPoint tp = new TrackPoint();
		tp.setLatitude(43.21);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		tp.write(baos);
		TrackPoint tp1 = new TrackPoint();
		tp1.read(new ByteArrayInputStream(baos.toByteArray()));
		assertTrue("tp1 should have a latitude", tp1.hasLatitude());
//		assertFalse("tp1 shouldn't have a longitude", tp1.hasLongitude());
		assertEquals("tp and tp1 should have the same latitude", tp.getLatitude(), tp1.getLatitude(), .000001);
		
		tp.setLongitude(-83.2341);
		tp.setTimestamp(234827938);
		tp.setAccuracy(20);
		baos.reset();
		tp.write(baos);
		tp1.read(new ByteArrayInputStream(baos.toByteArray()));
		assertTrue("tp1 should have a latitude", tp1.hasLatitude());
		assertTrue("tp1 should have a longitude", tp1.hasLongitude());
		assertTrue("tp1 should have accuracy", tp1.hasAccuracy());
		assertTrue("tp1 should have a timestamp", tp1.hasTimestamp());
		assertEquals("tp and tp1 should have the same latitude", tp.getLatitude(), tp1.getLatitude(), .000001);
		assertEquals("tp and tp1 should have the same longitude", tp.getLongitude(), tp1.getLongitude(), .000001);
		assertEquals("tp and tp1 should have the same accuracy", tp.getAccuracy(), tp1.getAccuracy(), .000001);
		assertEquals("tp and tp1 should have the same timestamp", tp.getTimestamp(), tp1.getTimestamp(), .000001);
		
		// create an input stream with four track points -- tp, then tp2, then tp, then tp2 again (repeating because I'm lazy)
		TrackPoint tp2 = new TrackPoint();
		tp2.setLongitude(141.798355);
		tp2.setLatitude(-64.837443);
		tp2.setAccuracy(50);
		tp2.setTimestamp(123456789);
		
		baos.reset();
		tp.write(baos);
		tp2.write(baos);
		tp.write(baos);
		tp2.write(baos);
		
		int counter = 0;
		for(TrackPoint tpx : TrackPoint.iterate(new ByteArrayInputStream(baos.toByteArray()))){
			counter +=1;
			if(counter % 2==1){
				assertTrue("tp# "+counter+" should have a latitude", tpx.hasLatitude());
				assertTrue("tp# "+counter+" should have a longitude", tpx.hasLongitude());
				assertTrue("tp# "+counter+" should have accuracy", tpx.hasAccuracy());
				assertTrue("tp# "+counter+" should have a timestamp", tpx.hasTimestamp());
				assertEquals("tp# "+counter+" and tp should have the same latitude", tpx.getLatitude(), tp.getLatitude(), .000001);
				assertEquals("tp# "+counter+" and tp should have the same longitude", tpx.getLongitude(), tp.getLongitude(), .000001);
				assertEquals("tp# "+counter+" and tp should have the same accuracy", tpx.getAccuracy(), tp.getAccuracy(), .000001);
				assertEquals("tp# "+counter+" and tp should have the same timestamp", tpx.getTimestamp(), tp.getTimestamp(), .000001);
				
			} else {
				assertTrue("tp# "+counter+" should have a latitude", tpx.hasLatitude());
				assertTrue("tp# "+counter+" should have a longitude", tpx.hasLongitude());
				assertTrue("tp# "+counter+" should have accuracy", tpx.hasAccuracy());
				assertTrue("tp# "+counter+" should have a timestamp", tpx.hasTimestamp());
				assertEquals("tp# "+counter+" and tp2 should have the same latitude", tpx.getLatitude(), tp2.getLatitude(), .000001);
				assertEquals("tp# "+counter+" and tp2 should have the same longitude", tpx.getLongitude(), tp2.getLongitude(), .000001);
				assertEquals("tp# "+counter+" and tp2 should have the same accuracy", tpx.getAccuracy(), tp2.getAccuracy(), .000001);
				assertEquals("tp# "+counter+" and tp2 should have the same timestamp", tpx.getTimestamp(), tp2.getTimestamp(), .000001);

			}
			
		}
		assertEquals("There should have been 4 track points in the iterator", 4, counter);
		
		
	}
	
	@Test
	public void testConversion() {
		assertEquals("Zero should convert to zero", 0,TrackPoint.convertDoubleToInt(0));
		//Signed ints make this a bit confusing -- not sure if I should bother to flip the sign. 
		//This makes more sense if you think of it as an unsigned int though.
		assertEquals("180 should convert to min int", Integer.MIN_VALUE, TrackPoint.convertDoubleToInt(180));
		assertEquals("90 should convert to one half of max int", -(Integer.MIN_VALUE/2), TrackPoint.convertDoubleToInt(90));
		assertEquals("45 should convert to one half of max int", -(Integer.MIN_VALUE/4), TrackPoint.convertDoubleToInt(45));
		//This is actually kind of tricky -- in reality 180 and -180 represent the same exact point, 
		//so they should both convert to the same point.  
		assertEquals("-180 should convert to min int", Integer.MIN_VALUE, TrackPoint.convertDoubleToInt(-180));
		//being lazy and referring to min/max Integer doesn't work so well because the min value is equal to a 
		//power of two whereas the max value is one less than a power of two.  
		assertEquals("-90 should convert to one half of min int", Integer.MIN_VALUE/2, TrackPoint.convertDoubleToInt(-90));
		assertEquals("-45 should convert to one half of min int", Integer.MIN_VALUE/4, TrackPoint.convertDoubleToInt(-45));
		
		//Now lets test that the conversion is reversible
		//At it's greatest, a microdegree is about a meter, so 1/10th of a microdegree (.0000001) is one tenth of a meter
		//These tests confirm that the back and forth conversion is accurate to a tenth of a meter (on the tests given).
		double tenthOfMicroDegree = .0000001;
		assertEquals("90 should convert back to 90", 90, TrackPoint.convertIntToDouble(TrackPoint.convertDoubleToInt(90)), tenthOfMicroDegree);
		assertEquals("60 should convert back to 60", 60, TrackPoint.convertIntToDouble(TrackPoint.convertDoubleToInt(60)), tenthOfMicroDegree);
		assertEquals("25.3293 should convert back to 25.3293", 25.3293, TrackPoint.convertIntToDouble(TrackPoint.convertDoubleToInt(25.3293)), tenthOfMicroDegree);
		assertEquals("-10 should convert back to -10", -10, TrackPoint.convertIntToDouble(TrackPoint.convertDoubleToInt(-10)), tenthOfMicroDegree);
	}
	

}
