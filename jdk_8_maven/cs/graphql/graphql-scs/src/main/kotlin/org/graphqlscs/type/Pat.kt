package org.graphqlscs.type

import org.springframework.stereotype.Component

@Component
class Pat {

    private fun Reverse(s: String): String {
        //RTN REVERSE OF s
        val slen = s.length
        if (slen < 2) {
            return s
        }
        var result = ""
        //var result : System.Text.StringBuilder = new System.Text.StringBuilder(slen);
        //var i : int = slen - 1;
        for (i in slen - 1 downTo 0) {
            //result.Append(s[i]);
            result += s[i]
        }
        //Console.WriteLine("s {0} revs {1}", s, result.ToString());
        return result
    }

    fun subject(txt: String, pat: String): String {
        //SEARCH txt FOR FIRST OCCURRENCE OF pat OR REVERSE OF pat
        //IF pat (STRING OF LENGTH AT LEAST 3) OCCURS IN txt, RTN 1
        //IF REVERSE OF pat OCCURS IN txt, RTN 2
        //IF pat AND REVERSE OF pat OCCURS IN txt, RTN 3
        //IF PALINDROME CONSISTING OF pat FOLLOWED BY REVERSE pat OCCURS IN txt, RTN 4
        //IF PALINDROME CONSISTING OF REVERSE pat FOLLOWED pat OCCURS IN txt, RTN 5
        var result = 0
        var i = 0
        var j = 0
        val txtlen = txt.length
        val patlen = pat.length
        var possmatch: String? = null
        if (patlen > 2) {
            val patrev = Reverse(pat)
            i = 0
            while (i <= txtlen - patlen) {
                if (txt[i] == pat[0]) {
                    possmatch = txt.substring(i, i + patlen)
                    if (possmatch == pat) {
                        //FOUND pat
                        result = 1
                        //CHECK IF txt CONTAINS REVERSE pat
                        j = i + patlen
                        while (j <= txtlen - patlen) {
                            if (txt[j] == patrev[0]) {
                                possmatch = txt.substring(j, j + patlen)
                                if (possmatch == patrev) {
                                    return if (j == i + patlen) {
                                        "" + i //4;
                                    } else {
                                        "" + i //3;
                                    }
                                }
                            }
                            j++
                        }
                    }
                } else if (txt[i] == patrev[0]) {
                    possmatch = txt.substring(i, i + patlen)
                    if (possmatch == patrev) {
                        //FOUND pat REVERSE
                        result = 2
                        //CHECK IF txt CONTAINS pat
                        j = i + patlen
                        while (j <= txtlen - patlen) {
                            if (txt[j] == pat[0]) {
                                possmatch = txt.substring(j, j + patlen)
                                if (possmatch == pat) {
                                    return if (j == i + patlen) {
                                        "" + i //5;
                                    } else {
                                        "" + i //3;
                                    }
                                }
                            }
                            j++
                        }
                    }
                }
                i++
            }
        }
        return "" + result
    }
}