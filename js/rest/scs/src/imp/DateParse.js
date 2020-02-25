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


const dataparse = (dayname, monthname) => {
    let result = 0;
    //int month = -1;
    dayname = dayname.toLowerCase();
    monthname = monthname.toLowerCase();

    if ("mon" === (dayname) ||
        "tue" === (dayname) ||
        "wed" === (dayname) ||
        "thur" === (dayname) ||
        "fri" === (dayname) ||
        "sat" === (dayname) ||
        "sun" === (dayname)) {
        result = 1;
    }
    if ("jan" === (monthname)) {
        result += 1;
    }
    if ("feb" === (monthname)) {
        result += 2;
    }
    if ("mar" === (monthname)) {
        result += 3;
    }
    if ("apr" === (monthname)) {
        result += 4;
    }
    if ("may" === (monthname)) {
        result += 5;
    }
    if ("jun" === (monthname)) {
        result += 6;
    }
    if ("jul" === (monthname)) {
        result += 7;
    }
    if ("aug" === (monthname)) {
        result += 8;
    }
    if ("sep" === (monthname)) {
        result += 9;
    }
    if ("oct" === (monthname)) {
        result += 10;
    }
    if ("nov" === (monthname)) {
        result += 11;
    }
    if ("dec" === (monthname)) {
        result += 12;
    }

    return "" + result;
}

module.exports = dataparse;