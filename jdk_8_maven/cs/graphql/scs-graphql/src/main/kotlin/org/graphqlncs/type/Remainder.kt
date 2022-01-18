package org.graphqlncs.type

class Remainder {
    fun exe(a: Int, b: Int): Int {
        var r = 0 - 1
        var cy = 0
        var ny = 0
        if (a == 0) ; else if (b == 0) ; else if (a > 0) if (b > 0) while (a - ny >= b) {
            ny = ny + b
            r = a - ny
            cy = cy + 1
        } else  // b<0
        //while((a+ny)>=Math.abs(b))
            while (a + ny >= if (b >= 0) b else -b) {
                ny = ny + b
                r = a + ny
                cy = cy - 1
            } else  // a<0
            if (b > 0) //while(Math.abs(a+ny)>=b)
                while ((if (a + ny >= 0) a + ny else -(a + ny)) >= b) {
                    ny = ny + b
                    r = a + ny
                    cy = cy - 1
                } else while (b >= a - ny) {
                ny = ny + b
                //r=Math.abs(a-ny);
                r = if (a - ny >= 0) a - ny else -(a - ny)
                cy = cy + 1
            }
        return r
    }
}