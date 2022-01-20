package org.graphqlscs.type

import org.springframework.stereotype.Component
import java.util.regex.Pattern
@Component
class Regex {

    fun subject(txt: String?): String {
        //MATCH txt AGAINST VARIOUS REGULAR EXPRESSIONS
        //ALL OF txt MUST MATCH
        val digit = "((0)|(1)|(2)|(3)|(4)|(5)|(6)|(7)|(8)|(9))"
        val fp = "$digit$digit*\\.$digit$digit*"
        val fpe = fp + "e((\\+)|(-))" + digit + digit
        val alpha =
            "((a)|(b)|(c)|(d)|(e)|(f)|(g)|(h)|(i)|(j)|(k)|(l)|(m)|(n)|(o)|(p)|(q)|(r)|(s)|(t)|(u)|(v)|(w)|(x)|(y)|(z)|(_)|(-))"
        val iden = "$alpha($alpha|$digit)*"
        val url = "((http)|(ftp)|(afs)|(gopher))//:$iden/$iden"
        val day = "((mon)|(tue)|(wed)|(thur)|(fri)|(sat)|(sun))"
        val month = "((jan)|(feb)|(mar)|(apr)|(may)|(jun)|(jul)|(aug)|(sep)|(oct)|(nov)|(dec))"
        val date = day + digit + digit + month
        //var re : RegExp;

        //Pattern p = Pattern.compile(url);

        //Console.WriteLine("{0}  {1}", txt, iden);
        //re = new RegExp(url);
        //re.regex.matchinexact.ParseFromRegExp();
        //print(StringUtils.PrettyPrint(re.regex.matchinexact.fsaexact));
        //if (0 == re.regex.matchinexact.Match(txt)) {
        if (Pattern.matches(url, txt)) {
            return "url"
        }
        //print(StringUtils.PrettyPrint(re.regex.matchinexact));

        //Console.WriteLine("{0}  {1}", txt, iden);
        //re = new RegExp(date);
        //re.regex.matchinexact.ParseFromRegExp();
        //print(StringUtils.PrettyPrint(re.regex.matchinexact.fsaexact));
        //  if (0 == re.regex.matchinexact.Match(txt)) {
        if (Pattern.matches(date, txt)) {
            return "date"
        }
        //print(StringUtils.PrettyPrint(re.regex.matchinexact));

        //Console.WriteLine("{0}  {1}", txt, fpe);
        //re = new RegExp(fpe);
        //re.regex.matchinexact.ParseFromRegExp();
        //print(StringUtils.PrettyPrint(re.regex.matchinexact.fsaexact));
        // if (0 == re.regex.matchinexact.Match(txt)) {
        return if (Pattern.matches(fpe, txt)) {
            "fpe"
        } else "none"
        //print(StringUtils.PrettyPrint(re.regex.matchinexact));
    }
}