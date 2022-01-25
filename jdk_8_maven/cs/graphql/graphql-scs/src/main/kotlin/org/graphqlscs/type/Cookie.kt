package org.graphqlscs.type

import org.springframework.stereotype.Component
import java.util.*
@Component
class Cookie {
    fun subject(name: String, `val`: String, site: String): String {
        var name = name
        var `val` = `val`
        var site = site
        name = name.lowercase(Locale.getDefault())
        `val` = `val`.lowercase(Locale.getDefault())
        site = site.lowercase(Locale.getDefault())
        var result = 0
        if ("userid" == name) {
            if (`val`.length > 6) {
                if ("user" == `val`.substring(0, 4)) {
                    result = 1
                }
            }
        } else if ("session" == name) {
            result = if ("am" == `val` && "abc.com" == site) {
                1
            } else {
                2
            }
        }
        return "" + result
    }
}