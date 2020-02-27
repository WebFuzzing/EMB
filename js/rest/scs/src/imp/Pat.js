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

const Reverse = (s) => {
    //RTN REVERSE OF s

    let slen = s.length;
    if (slen < 2) {
        return s;
    }

    let result = "";
//var result : System.Text.StringBuilder = new System.Text.StringBuilder(slen);
//var i : int = slen - 1;
    for (let i = slen - 1; i >= 0; i--) {
        //result.Append(s[i]);
        result += s.charAt(i);
    }
//Console.WriteLine("s {0} revs {1}", s, result.ToString());
    return result;
}

const pat_exe = (txt, pat) => {
    //SEARCH txt FOR FIRST OCCURRENCE OF pat OR REVERSE OF pat
    //IF pat (STRING OF LENGTH AT LEAST 3) OCCURS IN txt, RTN 1
    //IF REVERSE OF pat OCCURS IN txt, RTN 2
    //IF pat AND REVERSE OF pat OCCURS IN txt, RTN 3
    //IF PALINDROME CONSISTING OF pat FOLLOWED BY REVERSE pat OCCURS IN txt, RTN 4
    //IF PALINDROME CONSISTING OF REVERSE pat FOLLOWED pat OCCURS IN txt, RTN 5

    let result = 0;

    let i = 0;

    let j = 0;

    let txtlen = txt.length;

    let patlen = pat.length;

    let possmatch = null;

    if (patlen > 2) {

        let patrev = Reverse(pat);
        for (let i = 0; i <= txtlen - patlen; i++) {
            if (txt.charAt(i) === pat.charAt(0)) {
                possmatch = txt.substring(i, i + patlen);
                if (possmatch === (pat)) {
                    //FOUND pat
                    result = 1;
                    //CHECK IF txt CONTAINS REVERSE pat
                    for (let j = i + patlen; j <= txtlen - patlen; j++) {
                        if (txt.charAt(j) === patrev.charAt(0)) {
                            possmatch = txt.substring(j, j + patlen);
                            if (possmatch === (patrev)) {
                                if (j == i + patlen) {
                                    return "" + i;//4;
                                } else {
                                    return "" + i;//3;
                                }
                            }
                        }
                    }
                }
            } else if (txt.charAt(i) === patrev.charAt(0)) {
                possmatch = txt.substring(i, i + patlen);
                if (possmatch === (patrev)) {
                    //FOUND pat REVERSE
                    result = 2;
                    //CHECK IF txt CONTAINS pat
                    for (let j = i + patlen; j <= txtlen - patlen; j++) {
                        if (txt.charAt(j) === pat.charAt(0)) {
                            possmatch = txt.substring(j, j + patlen);
                            if (possmatch === (pat)) {
                                if (j === i + patlen) {
                                    return "" + i;//5;
                                } else {
                                    return "" + i;//3;
                                }
                            }
                        }
                    }
                }
            }
        }  //pat NOR REVERSE FOUND
    }
    return "" + result;
}

module.exports = pat_exe;
