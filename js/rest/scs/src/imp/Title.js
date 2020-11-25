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

const title = (sex, title) => {
    //CHECK PERSONAL TITLE CONSISTENT WITH SEX
    sex = sex.toLowerCase();
    title = title.toLowerCase();
    let result = -1;
    if ("male" === (sex)) {
        if ("mr" === (title) ||
            "dr" === (title) ||
            "sir" === (title) ||
            "rev" === (title) ||
            "rthon" === (title) ||
            "prof" === (title)) {
            result = 1;
        }
    } else if ("female" === (sex)) {
        if ("mrs" === (title) ||
            "miss" === (title) ||
            "ms" === (title) ||
            "dr" === (title) ||
            "lady" === (title) ||
            "rev" === (title) ||
            "rthon" === (title) ||
            "prof" === (title)) {
            result = 0;
        }
    } else if ("none" === (sex)) {
        if ("dr" === (title) ||
            "rev" === (title) ||
            "rthon" === (title) ||
            "prof" === (title)) {
            result = 2;
        }
    }
    return "" + result;
}

module.exports = title;