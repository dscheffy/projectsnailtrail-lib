package org.projectsnailtrail.writable;

public class BoundingBox {
	private static long SIZE_MASK = 0x3FL; // binary 111111 (six ones)
	private static long INTERLEAVE_MASK = ~SIZE_MASK; // all ones followed by six zeros
	public static int TRANFORM_UP_RIGHT=1;
	public static int TRANSFORM_DOWN_LEFT=-1;
	public static int TRANSFORM_NONE=0;
	public static int TRANSFORM_NOT_INITIALIZED=Integer.MAX_VALUE;

	/**
	 * The interleave of binary radian latitude and longitudes for the center point of this box.
	 */
	long interleave;
	/**
	 * The log base two of the size of this bounding box -- the number of bits required to uniquely identify every point
	 * in the box.
	 */
	int size; //byte would suffice -- value is never greater than 64 
	/**
	 * The coordinate space which is best suited for handling this box.  The one for which all enclosed points share the 
	 * greatest number of leading bits in their interleaved value.
	 */
	int bestTransform;
	
	public BoundingBox(long interleave){
		this.interleave=INTERLEAVE_MASK & interleave;
		this.size = (int)(SIZE_MASK & interleave);
		
		
	}
	public long center(){
		return interleave;
	}
//	public long max(){
//		
//	}
	
	

}
