package org.projectsnailtrail.tools;

public class MicroDegreeTools {
	private static int[] LONGITUDINAL_MULTIPLIER = {
		100, 100, 100, 100, 100, 100, 100, 100, 100, 101,    // 0-9   
		101, 101, 102, 102, 103, 103, 104, 104, 105, 105,    // 10-19
		106, 107, 107, 108, 109, 110, 111, 112, 113, 114,    // 20-29
		115, 116, 117, 119, 120, 122, 123, 125, 126, 128,    // 30-39
		130, 132, 134, 136, 139, 141, 143, 146, 149, 152,
		155, 158, 162, 166, 170, 174, 178, 183, 188, 194,
		200, 206, 213, 220, 228, 236, 245, 255, 266, 279,
		292, 307, 323, 342, 362, 386, 413, 444, 480, 524,
		575, 639, 718, 820, 956, 1147, 1433, 1910, 2865, 5729   // 80-89
	};
	
	/**
	 * Factor for converting latitudinal and longitudinal microdegrees to comparable units.  To use multiply longitudinal 
	 * microdegrees by 100, then divide by the result of this method.  Alternatively, multiply longitude by 100 and latitude
	 * by the result.  
	 * 
	 * For simpler implementations convert microdegrees to meters using toApproximateMeters.
	 * 
	 * This may seem like overkill, but it allows you to avoid floating point numbers and get a decent approximation.
	 * The factor itself is only approximate and is much less accurate as you approach the poles.
	 * 
	 * @param latitudeMicroDegrees
	 * @return the factor for creating comparable microdegree units
	 */
	public static int getLongitudinalFactor(int latitudeMicroDegrees){
		return LONGITUDINAL_MULTIPLIER[Math.abs(latitudeMicroDegrees)/1000000];
	}
	/**
	 * Converts microdegrees to approximate meters.  Degrees latitude are always the same length, but the length of a degree
	 * of longitude depends on the latitude and is only the same length as a degree of latitude near the equator.  For that
	 * reason you need to specify the latitude for which the longitude microdegrees are to be converted.  Use zero if converting
	 * latitude values (to simulate being at the equator). 
	 * 
	 * @param microdegrees the number of microdegrees to convert to meters.
	 * @param latitude the latitude (in microdegrees) at which the microdegrees longitude should be converted. For converting microdegrees latitude use zero.
	 * @return length of microdegrees in meters.
	 */
	public static int toApproximateMeters(int microdegrees, int latitude){
		return (int)((long)microdegrees * 11 / LONGITUDINAL_MULTIPLIER[latitude/1000000] );
	}

}
