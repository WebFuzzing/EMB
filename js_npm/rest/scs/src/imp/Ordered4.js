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

const ordered4 = (w, x, z, y) => {
    let result = "unordered";
    if (w.length >= 5 && w.length <= 6 &&  //LIMIT LENGTH TO LIMIT PROB OF RANDOM SATISFACTION
        x.length >= 5 && x.length <= 6 &&
        y.length >= 5 && y.length <= 6 &&
        z.length >= 5 && z.length <= 6) {
        if (z > (y) && y > (x)  && x > (w) ) {
            result = "increasing";
        } else if (w > (x)  && x > (y)  && y > (z) ) {
            result = "decreasing";
        }
    }
    return result;
}

module.exports = ordered4;