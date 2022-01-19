
package org.graphqlscs.type

import org.springframework.stereotype.Component
import java.util.*

@Component
class Calc {

    fun subject(op: String, arg1: Double, arg2: Double): String {
        var op = op
        op = op.lowercase(Locale.getDefault())
        var result = 0.0
        if ("pi" == op) { //CONSTANT OPERATOR
            result = Math.PI
        } else if ("e" == op) {
            result = Math.E
        } //UNARY OPERATOR
        else if ("sqrt" == op) {
            result = Math.sqrt(arg1)
        } else if ("log" == op) {
            result = Math.log(arg1)
        } else if ("sine" == op) {
            result = Math.sin(arg1)
        } else if ("cosine" == op) {
            result = Math.cos(arg1)
        } else if ("tangent" == op) {
            result = Math.tan(arg1)
        } //BINARY OPERATOR
        else if ("plus" == op) {
            result = arg1 + arg2
        } else if ("subtract" == op) {
            result = arg1 - arg2
        } else if ("multiply" == op) {
            result = arg1 * arg2
        } else if ("divide" == op) {
            result = arg1 / arg2
        }
        return "" + result
    }
}