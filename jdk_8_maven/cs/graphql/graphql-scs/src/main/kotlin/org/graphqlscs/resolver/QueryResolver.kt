package org.graphqlscs.resolver

import graphql.kickstart.tools.GraphQLQueryResolver
import org.graphqlscs.type.*
import org.springframework.stereotype.Component

@Component
open class QueryResolver(

    private val calc: Calc,
    private val cookie: Cookie,
    private val costfuns: Costfuns,
    private val dateParse: DateParse,
    private val fleSuffix: FileSuffix,
    private val notyPevar: NotyPevar,
    private val ordered4: Ordered4,
    private val pa: Pat,
    private val regex: Regex,
    private val txt2Txt: Text2Txt,
    private val titl: Title

) : GraphQLQueryResolver {


    fun calc(op: String, arg1: Double, arg2: Double): String {
        return calc.subject(op, arg1, arg2)
    }

    fun cookie(name: String, `val`: String, site: String): String {
        return cookie.subject(name, `val`, site)
    }

    fun costfuns(i: Int, s: String): String {
        return costfuns.subject(i, s)
    }

    fun dateParse(dayname: String, monthname: String): String {
        return dateParse.subject(dayname, monthname)
    }

    fun fileSuffix(directory: String, file: String): String {
        return fleSuffix.subject(directory, file)
    }

    fun notyPevar(i: Int, s: String): String {
        return notyPevar.subject(i, s)
    }

    fun ordered4(w: String, x: String, z: String, y: String): String {
        return ordered4.subject(w, x, z, y)
    }

    fun pat(txt: String, pat: String): String {
        return pa.subject(txt, pat)
    }

    fun regex(txt: String): String {
        return regex.subject(txt)
    }

    fun text2Txt(word1: String, word2: String, word3: String): String {
        return txt2Txt.subject(word1, word2, word3)
    }

    fun title(sex: String, title: String): String {
        return titl.subject(sex, title)
    }

}