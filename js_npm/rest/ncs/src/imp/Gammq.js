const ITMAX = 100;
const EPS = 3.0e-7;
const FPMIN = 1.0e-30;

let gamser, gammcf, gln;

const gammln = (xx) => {

    let x, y, tmp, ser;

    let cof = [76.18009172947146, -86.50532032941677, 24.01409824083091, -1.231739572450155, 0.1208650973866179e-2, -0.5395239384953e-5];

    let j;

    y = x = xx;
    tmp = x + 5.5;
    tmp -= (x + 0.5) * Math.log(tmp);
    ser = 1.000000000190015;
    for (j = 0; j <= 5; j++) ser += cof[j] / ++y;
    return -tmp + Math.log(2.5066282746310005 * ser / x);
};

const gcf = (a, x) => {
    let i;
    let an, b, c, d, del, h;

    gln = gammln(a);
    b = x + 1.0 - a;
    c = 1.0 / FPMIN;
    d = 1.0 / b;
    h = d;
    for (i = 1; i <= ITMAX; i++) {
        an = -i * (i - a);
        b += 2.0;
        d = an * d + b;
        if (Math.abs(d) < FPMIN) d = FPMIN;
        c = b + an / c;
        if (Math.abs(c) < FPMIN) c = FPMIN;
        d = 1.0 / d;
        del = d * c;
        h *= del;
        if (Math.abs(del - 1.0) < EPS) break;
    }
    if (i > ITMAX) throw new Error("a too large, ITMAX too small in gcf");
    gammcf = Math.exp(-x + a * Math.log(x) - gln) * h;
};

const gser = (a, x) => {

    let n;
    let sum, del, ap;

    gln = gammln(a);

    if (x <= 0.0) {
        if (x < 0.0) throw new Error("x less than 0 in routine gser");
        gamser = 0.0;
        return;
    } else {
        ap = a;
        del = sum = 1.0 / a;
        for (n = 1; n <= ITMAX; n++) {
            ++ap;
            del *= x / ap;
            sum += del;
            if (Math.abs(del) < Math.abs(sum) * EPS) {
                gamser = sum * Math.exp(-x + a * Math.log(x) - gln);
                return;
            }
        }
        throw new Error("a too large, ITMAX too small in routine gser");
    }
};

const gammq = (a, x) => {
    if (x < 0.0 || a <= 0.0) throw new Error("Invalid arguments in routine gammq");
    if (x < (a + 1.0)) {
        gser(a, x);
        return 1 - gamser;
    } else {
        gcf(a, x);
        return gammcf;
    }
};

module.exports = {gammq};



