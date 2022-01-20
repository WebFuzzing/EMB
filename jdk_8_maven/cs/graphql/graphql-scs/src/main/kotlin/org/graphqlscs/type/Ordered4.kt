package org.graphqlscs.type

import org.springframework.stereotype.Component

@Component
class Ordered4 {

    fun subject(w: String, x: String, z: String, y: String): String {
        var result = "unordered"
        if (w.length >= 5 && w.length <= 6 && //LIMIT LENGTH TO LIMIT PROB OF RANDOM SATISFACTION
            x.length >= 5 && x.length <= 6 && y.length >= 5 && y.length <= 6 && z.length >= 5 && z.length <= 6
        ) {
            if (z.compareTo(y) > 0 && y.compareTo(x) > 0 && x.compareTo(w) > 0) {
                result = "increasing"
            } else if (w.compareTo(x) > 0 && x.compareTo(y) > 0 && y.compareTo(z) > 0) {
                result = "decreasing"
            }
        }
        return result
    }
}