package org.projectsnailtrail.stats;

public class HeuristicEvaluator {
	
	static int distanceHeuristic(int x, int y){
		int leadingZeroes = Integer.numberOfLeadingZeros((x^y)&0x00000fff);
		int result = 0;
		if(leadingZeroes <32){
		  result = 0xFFFFFFFF >>> leadingZeroes;
		}

		return result;
	}
	static int actualDistance(int x, int y){
		return Math.abs(x-y);
	}

}
