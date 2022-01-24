package org.graphqlscs.type

import org.springframework.stereotype.Component
import java.util.*
@Component
class Title {

    fun subject(sex: String, title: String): String {
        //CHECK PERSONAL TITLE CONSISTENT WITH SEX
        var sex = sex
        var title = title
        sex = sex.lowercase(Locale.getDefault())
        title = title.lowercase(Locale.getDefault())
        var result = -1
        if ("male" == sex) {
            if ("mr" == title || "dr" == title || "sir" == title || "rev" == title || "rthon" == title || "prof" == title) {
                result = 1
            }
        } else if ("female" == sex) {
            if ("mrs" == title || "miss" == title || "ms" == title || "dr" == title || "lady" == title || "rev" == title || "rthon" == title || "prof" == title) {
                result = 0
            }
        } else if ("none" == sex) {
            if ("dr" == title || "rev" == title || "rthon" == title || "prof" == title) {
                result = 2
            }
        }
        return "" + result
    }
}