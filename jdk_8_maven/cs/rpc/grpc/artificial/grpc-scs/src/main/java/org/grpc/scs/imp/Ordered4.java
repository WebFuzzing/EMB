//! futname = Subject      //NAME OF FUNCTION UNDER TEST
//! mutation = false        //SPECIFY MUTATION COVERAGE
//! textout = true        //WRITE INSTRUMENTED SUBJECT TO FILE
//! maxchildren = 500000  //MAX LENGTH OF SEARCH
//! totalpopsize = 100    //TOTAL SIZE OF POPULATIONS 
//! mutationpercent = 50  //REL FREQUENCY OF GENETIC MUTATION TO CROSSOVER
//! samefitcountmax = 100 //NUMBER OF CONSECUTIVE TESTS IN A POP 
//THAT MUST HAVE THE SAME COST FOR POP TO BE STAGNANT
//! verbose = false        //PRINT MESSAGES SHOWING PROGRESS OF SEARCH
//! showevery = 3000      //NUMBER OF CANDIDATE INPUTS GENERATED BETWEEN EACH SHOW
//! numbins = 0           //GRANULARITY OF CANDIDATE INPUT HISTOGRAM, SET TO 0 TO NOT COLLECT STATS
//! trialfirst = 1        //EACH TRIAL USES A DIFFERENT RANDOM SEED
//! triallast = 1         //NUMBER OF TRIALS = triallast - trialfirst + 1
package org.grpc.scs.imp;

public class Ordered4
{

	public static String subject(String w,  String x,  String z, String y)
	{
		String  result = "unordered";
		if (w.length() >= 5 && w.length() <= 6 &&  //LIMIT LENGTH TO LIMIT PROB OF RANDOM SATISFACTION
				x.length() >= 5 && x.length() <= 6 &&
				y.length() >= 5 && y.length() <= 6 &&
				z.length() >= 5 && z.length() <= 6) {
			if (z.compareTo(y) > 0 && y.compareTo(x) > 0 && x.compareTo(w) > 0) {
				result = "increasing";
			}
			else if (w.compareTo(x) > 0 && x.compareTo(y) > 0 && y.compareTo(z) > 0) {
				result = "decreasing";
			}
		}
		return result;
	}
}