package org.projectsnailtrail.tools;

import static org.junit.Assert.*;

import org.junit.Test;

import static org.projectsnailtrail.tools.MicroDegreeTools.*;

public class MicroDegreeToolsTest {

	@Test
	public void testGetMultiplier() {
		assertEquals("multiplier should be 1 at equator", 100, getLongitudinalFactor(0), 10);
		assertEquals("multiplier should be 2 at 60 degrees", 200, getLongitudinalFactor(60000000), 10);
		
	}
	
	@Test
	public void testToApproximateMeters(){
		assertEquals("circumference at equator is 40 thousand km", 39600000, toApproximateMeters(360000000, 0));
		assertEquals("circumference at 60 degrees is half that", 19800000, toApproximateMeters(360000000, 60000000));
		
	}

}
