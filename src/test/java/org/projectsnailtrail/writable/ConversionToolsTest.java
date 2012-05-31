package org.projectsnailtrail.writable;

import static org.junit.Assert.*;

import org.junit.Test;

import static org.projectsnailtrail.tools.ConversionTools.*;

public class ConversionToolsTest {

	@Test
	public void test() {

		//fail("Not yet implemented");
		assertEquals("max should work", 180000000, convertBradToMicrodegrees(Integer.MAX_VALUE));
		assertEquals("min should work", -180000000, convertBradToMicrodegrees(Integer.MIN_VALUE));
		assertEquals("90degrees should work", 90000000, convertBradToMicrodegrees(-(Integer.MIN_VALUE/2)));
		assertEquals("negative 90 deg should work", -90000000, convertBradToMicrodegrees(Integer.MIN_VALUE/2));
		assertEquals("zero should come back as zero",0,convertBradToMicrodegrees(0));
		assertEquals("half Integer.MIN_VALUE should come back as -90million",-90000000,convertBradToMicrodegrees(Integer.MIN_VALUE/2));

//		assertEquals("Max should come back as max", Integer.MAX_VALUE, convertMicrodegreesToBrad(179999999));
		assertEquals("Min should come back as min", Integer.MIN_VALUE, convertMicrodegreesToBrad(-180000000));
		assertEquals("zero should come back as zero",0,convertMicrodegreesToBrad(0));
		assertEquals("convertMicrodegreesToBrad should be reversible", -180000000, convertBradToMicrodegrees(convertMicrodegreesToBrad(-180000000)));
//		assertEquals("convertMicrodegreesToBrad should be reversible", 179999999, convertBradToMicrodegrees(convertMicrodegreesToBrad(179999999)));

		//why not --  might as well just test every possible value of microdegrees to make sure we can get back to them
		for(int i =-180000000;i<180000000;i++){
			assertEquals("convertMicrodegreesToBrad should be reversible", i, convertBradToMicrodegrees(convertMicrodegreesToBrad(i)));
			
		}
		
		
	}

}
