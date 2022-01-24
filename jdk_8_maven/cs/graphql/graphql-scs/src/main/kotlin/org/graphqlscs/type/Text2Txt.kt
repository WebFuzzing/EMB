package org.graphqlscs.type

import org.springframework.stereotype.Component
import java.util.*
@Component
class Text2Txt {
    fun subject(word1: String, word2: String, word3: String): String {
        //CONVERT ENGLISH TEXT txt INTO MOBILE TELEPHONE TXT
        //BY SUBSTITUTING ABBREVIATIONS FOR COMMON WORDS
        var word1 = word1
        var word2 = word2
        var word3 = word3
        word1 = word1.lowercase(Locale.getDefault())
        word2 = word2.lowercase(Locale.getDefault())
        word3 = word3.lowercase(Locale.getDefault())
        var result = ""
        if (word1 == "two") {
            result = "2"
        }
        if (word1 == "for" || word1 == "four") {
            result = "4"
        }
        if (word1 == "you") {
            result = "u"
        }
        if (word1 == "and") {
            result = "n"
        }
        if (word1 == "are") {
            result = "r"
        } else if (word1 == "see" && word2 == "you") {
            result = "cu"
        } else if (word1 == "by" && word2 == "the" && word3 == "way") {
            result = "btw"
        }
        return result
    }

}