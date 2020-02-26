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

const text2txt = (word1, word2, word3) => {
//CONVERT ENGLISH TEXT txt INTO MOBILE TELEPHONE TXT
//BY SUBSTITUTING ABBREVIATIONS FOR COMMON WORDS
    word1 = word1.toLowerCase();
    word2 = word2.toLowerCase();
    word3 = word3.toLowerCase();
    let result = "";
    if (word1 === ("two")) {
        result = "2";
    }
    if (word1 === ("for") || word1 === ("four")) {
        result = "4";
    }
    if (word1 === ("you")) {
        result = "u";
    }
    if (word1 === ("and")) {
        result = "n";
    }
    if (word1 === ("are")) {
        result = "r";
    } else if (word1 === ("see") && word2 === ("you")) {
        result = "cu";
    } else if (word1 === ("by") && word2 === ("the") && word3 === ("way")) {
        result = "btw";
    }
    return result;
}

module.exports = text2txt;