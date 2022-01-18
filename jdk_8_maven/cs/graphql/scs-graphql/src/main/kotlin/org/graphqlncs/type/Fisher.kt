package org.graphqlncs.type

class Fisher {
    fun exe(m: Int, n: Int, x: Double): Double {
        val a: Int
        var b: Int
        var i: Int
        var j: Int
        val w: Double
        var y: Double
        var z: Double
        val zk: Double
        var d: Double
        var p: Double
        a = 2 * (m / 2) - m + 2
        b = 2 * (n / 2) - n + 2
        w = x * m / n
        z = 1.0 / (1.0 + w)
        if (a == 1) {
            if (b == 1) {
                p = Math.sqrt(w)
                y = 0.3183098862
                d = y * z / p
                p = 2.0 * y * Math.atan(p)
            } else {
                p = Math.sqrt(w * z)
                d = 0.5 * p * z / w
            }
        } else if (b == 1) {
            p = Math.sqrt(z)
            d = 0.5 * z * p
            p = 1.0 - p
        } else {
            d = z * z
            p = w * z
        }
        y = 2.0 * w / z
        if (a == 1) {
            j = b + 2
            while (j <= n) {
                d *= (1.0 + 1.0 / (j - 2)) * z
                p += d * y / (j - 1)
                j += 2
            }
        } else {
            zk = Math.pow(z, ((n - 1) / 2).toDouble())
            d *= zk * n / b
            p = p * zk + w * z * (zk - 1.0) / (z - 1.0)
        }
        y = w * z
        z = 2.0 / z
        b = n - 2
        i = a + 2
        while (i <= m) {
            j = i + b
            d *= y * j / (i - 2)
            p -= z * d / j
            i += 2
        }
        return if (p < 0.0) 0.0 else if (p > 1.0) 1.0 else p
    }
}