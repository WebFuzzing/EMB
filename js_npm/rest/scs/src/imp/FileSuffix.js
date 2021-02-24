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


const filesuffix = (directory, file) => {
    let result = 0;

    //EG pathname = "...WORD/FILE.DOC";
    // files : Object[];
    let fileparts = null;
    //var lastfile : int = 0;
    let lastpart = 0;
    let suffix = null;
    fileparts = file.split(".");
    lastpart = fileparts.length - 1;
    if (lastpart > 0) {
        suffix = fileparts[lastpart];
        //Console.WriteLine("{0}, {1}", directory, suffix);
        if ("text" === (directory)) {
            if ("txt" === (suffix)) {
                result = 1;
            }
        }
        if ("acrobat" === (directory)) {
            if ("pdf" === (suffix)) {
                //print("acrobat");
                result = 2;
            }
        }
        if ("word" === (directory)) {
            if ("doc" === (suffix)) {
                //print("word");
                result = 3;
            }
        }
        if ("bin" === (directory)) {
            if ("exe" === (suffix)) {
                //print("bin");
                result = 4;
            }
        }
        if ("lib" === (directory)) {
            if ("dll" === (suffix)) {
                //print("lib");
                result = 5;
            }
        }
    }
    return "" + result;
}

module.exports = filesuffix;