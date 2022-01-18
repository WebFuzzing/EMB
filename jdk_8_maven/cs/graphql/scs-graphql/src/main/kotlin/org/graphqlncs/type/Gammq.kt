package org.graphqlncs.type

class Gammq {
    private val ITMAX = 100
    private val EPS = 3.0e-7
    private val FPMIN = 1.0e-30

    private var gamser = 0.0
    private  var gammcf:kotlin.Double = 0.0
    private  var gln:kotlin.Double = 0.0

    private fun gammln(xx: Double): Double {
        val x: Double
        var y: Double
        var tmp: Double
        var ser: Double
        val cof = doubleArrayOf(
            76.18009172947146,
            -86.50532032941677,
            24.01409824083091,
            -1.231739572450155,
            0.1208650973866179e-2,
            -0.5395239384953e-5
        )
        var j: Int
        x = xx
        y = x
        tmp = x + 5.5
        tmp -= (x + 0.5) * Math.log(tmp)
        ser = 1.000000000190015
        j = 0
        while (j <= 5) {
            ser += cof[j] / ++y
            j++
        }
        return -tmp + Math.log(2.5066282746310005 * ser / x)
    }
    private fun gcf(a: Double, x: Double) {
        var i: Int
        var an: Double
        var b: Double
        var c: Double
        var d: Double
        var del: Double
        var h: Double
        gln = gammln(a)
        b = x + 1.0 - a
        c = 1.0 / FPMIN
        d = 1.0 / b
        h = d
        i = 1
        while (i <= ITMAX) {
            an = -i * (i - a)
            b += 2.0
            d = an * d + b
            if (Math.abs(d) < FPMIN) d = FPMIN
            c = b + an / c
            if (Math.abs(c) < FPMIN) c = FPMIN
            d = 1.0 / d
            del = d * c
            h *= del
            if (Math.abs(del - 1.0) < EPS) break
            i++
        }
        if (i > ITMAX) throw RuntimeException("a too large, ITMAX too small in gcf")
        gammcf = Math.exp(-x + a * Math.log(x) - gln) * h
    }
    private fun gser(a: Double, x: Double) {
        var n: Int
        var sum: Double
        var del: Double
        var ap: Double
        gln = gammln(a)
        if (x <= 0.0) {
            if (x < 0.0) throw RuntimeException("x less than 0 in routine gser")
            gamser = 0.0
            return
        } else {
            ap = a
            sum = 1.0 / a
            del = sum
            n = 1
            while (n <= ITMAX) {
                ++ap
                del *= x / ap
                sum += del
                if (Math.abs(del) < Math.abs(sum) * EPS) {
                    gamser = sum * Math.exp(-x + a * Math.log(x) - gln)
                    return
                }
                n++
            }
            throw RuntimeException("a too large, ITMAX too small in routine gser")
        }
    }

    fun exe(a: Double, x: Double): Double {
        if (x < 0.0 || a <= 0.0) throw RuntimeException("Invalid arguments in routine gammq")
        return if (x < a + 1.0) {
            gser(a, x)
            1 - gamser
        } else {
            gcf(a, x)
            gammcf
        }
    }
}