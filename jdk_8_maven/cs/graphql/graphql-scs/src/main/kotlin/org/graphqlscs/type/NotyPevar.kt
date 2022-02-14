package org.graphqlscs.type

import org.springframework.stereotype.Component

@Component
class NotyPevar {
    //SHOW USE OF UNTYPED VARIABLES
    fun subject(i: Int, s: String?): String {
        var x: Int
        val y: Int
        var result = 0
        x = i
        y = x
        if (x + y == 56) {     //i0
            result = x
        }
        val xs = "hello"
        if (xs + y == "hello7") {   //i1
            result = 1
        }
        if (xs.compareTo(s!!) < 0) {  //i2
            result = 2
        }
        x = 5
        if (y > x) {    //i3
            result = 3
        }
        return "" + result
    }
}