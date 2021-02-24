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
//! name = calc           //NAME OF EXPT, NOT COMPUTATIONALLY SIGNIFICANT


const calc = (op, arg1, arg2) => {
    op = op.toLowerCase();

    let result = 0.0;
    if ("pi" === op) { //CONSTANT OPERATOR
        result = Math.PI;
    } else if ("e" === op) {
        result = Math.E;
    }       //UNARY OPERATOR
    else if ("sqrt" === op) {
        result = Math.sqrt(arg1);
    } else if ("log" === op) {
        result = Math.log(arg1);
    } else if ("sine" === op) {
        result = Math.sin(arg1);
    } else if ("cosine" === op) {
        result = Math.cos(arg1);
    } else if ("tangent" === op) {
        result = Math.tan(arg1);
    }      //BINARY OPERATOR
    else if ("plus" === op) {
        result = arg1 + arg2;
    } else if ("subtract" === op) {
        result = arg1 - arg2;
    } else if ("multiply" === op) {
        result = arg1 * arg2;
    } else if ("divide" === op) {
        result = arg1 / arg2;
    }
    return "" + result;
}

module.exports = calc
