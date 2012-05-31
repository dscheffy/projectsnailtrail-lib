package org.projectsnailtrail.stats;

import static org.junit.Assert.assertTrue;
import static org.projectsnailtrail.stats.HeuristicEvaluator.actualDistance;
import static org.projectsnailtrail.stats.HeuristicEvaluator.distanceHeuristic;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Writer;

import org.junit.Test;

public class HeuristicEvaluatorTest {
	@Test
	public void testheuristic() throws Exception { 
		int min=-2048;
		int max=2048;
		int heuristic, actual;
		int maxError,error,minError;
		int i,j;
		long totalError,averageError,sumOfSquares;
		double errorRatio,totalErrorRatio,averageErrorRatio,sumOfSquaresRatio;
		PrintWriter writer = new PrintWriter(new FileWriter("/home/dscheffy/sandbox/hackathon/stuff",false));
		for(i=min;i<max;i++){
			totalError=0;
			maxError=0;
			sumOfSquares=0;
			totalErrorRatio=0;
			sumOfSquaresRatio=0;
			minError=Integer.MAX_VALUE;
			for(j=i-100;j<i+100;j++){
				heuristic=distanceHeuristic(i,j);
				actual=actualDistance(i,j);
				assertTrue("i="+i+",j="+j+",heur="+heuristic, heuristic>=actual);
				error = heuristic-actual;
				errorRatio = (double)error/(double)(heuristic+1);
				if(error>maxError) maxError=error;
				if(error<minError) minError=error;
				totalError += error;
				totalErrorRatio += errorRatio;
				sumOfSquares += error*error;
				sumOfSquaresRatio += errorRatio*errorRatio;
			}
			averageError=totalError/(long)(max-min);
			writer.println(i+","+minError+","+maxError+","+averageError+","+sumOfSquares+","+totalErrorRatio+","+sumOfSquaresRatio);
		}
		writer.close();
	//	fail("Not yet implemented");
	}

}
