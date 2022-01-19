package org.graphqlncs.type

import org.springframework.stereotype.Component

@Component
class Expint {
    private val MAXIT = 100.0
    private val EULER = 0.5772156649
    private val FPMIN = 1.0e-30
    private val EPS = 1.0e-7

    fun exe(n: Int, x: Double): Double {
        var i: Int
        var ii: Int
        val nm1: Int
        var a: Double
        var b: Double
        var c: Double
        var d: Double
        var del: Double
        var fact: Double
        var h: Double
        var psi: Double
        var ans: Double
        nm1 = n - 1
        if (n < 0 || x < 0.0 || x == 0.0 && (n == 0 || n == 1)) throw RuntimeException("error: n < 0 or x < 0") else {
            if (n == 0) ans = Math.exp(-x) / x else {
                if (x == 0.0) ans = 1.0 / nm1 else {
                    if (x > 1.0) {
                        b = x + n
                        c = 1.0 / FPMIN
                        d = 1.0 / b
                        h = d
                        i = 1
                        while (i <= MAXIT) {
                            a = (-i * (nm1 + i)).toDouble()
                            b += 2.0
                            d = 1.0 / (a * d + b)
                            c = b + a / c
                            del = c * d
                            h *= del
                            if (Math.abs(del - 1.0) < EPS) {
                                return h * Math.exp(-x)
                            }
                            i++
                        }
                        throw RuntimeException("continued fraction failed in expint")
                    } else {
                        ans = if (nm1 != 0) 1.0 / nm1 else -Math.log(x) - EULER
                        fact = 1.0
                        i = 1
                        while (i <= MAXIT) {
                            fact *= -x / i
                            if (i != nm1) del = -fact / (i - nm1) else {
                                psi = -EULER
                                ii = 1
                                while (ii <= nm1) {
                                    psi += 1.0 / ii
                                    ii++
                                }
                                del = fact * (-Math.log(x) + psi)
                            }
                            ans += del
                            if (Math.abs(del) < Math.abs(ans) * EPS) {
                                return ans
                            }
                            i++
                        }
                        throw RuntimeException("series failed in expint")
                    }
                }
            }
        }
        return ans
    }
}