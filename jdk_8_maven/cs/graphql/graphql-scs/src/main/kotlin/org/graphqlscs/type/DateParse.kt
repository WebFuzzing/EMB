package org.graphqlscs.type

import org.springframework.stereotype.Component
import java.util.*
@Component
class DateParse {
    fun subject(dayname: String, monthname: String): String {
        var dayname = dayname
        var monthname = monthname
        var result = 0
        //int month = -1;
        dayname = dayname.lowercase(Locale.getDefault())
        monthname = monthname.lowercase(Locale.getDefault())
        if ("mon" == dayname || "tue" == dayname || "wed" == dayname || "thur" == dayname || "fri" == dayname || "sat" == dayname || "sun" == dayname) {
            result = 1
        }
        if ("jan" == monthname) {
            result += 1
        }
        if ("feb" == monthname) {
            result += 2
        }
        if ("mar" == monthname) {
            result += 3
        }
        if ("apr" == monthname) {
            result += 4
        }
        if ("may" == monthname) {
            result += 5
        }
        if ("jun" == monthname) {
            result += 6
        }
        if ("jul" == monthname) {
            result += 7
        }
        if ("aug" == monthname) {
            result += 8
        }
        if ("sep" == monthname) {
            result += 9
        }
        if ("oct" == monthname) {
            result += 10
        }
        if ("nov" == monthname) {
            result += 11
        }
        if ("dec" == monthname) {
            result += 12
        }
        return "" + result
    }

}