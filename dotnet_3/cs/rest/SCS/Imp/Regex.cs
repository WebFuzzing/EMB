namespace SCS.Imp
{
    public class Regex
    {
        public static string Subject(string txt)
        {
            //MATCH txt AGAINST VARIOUS REGULAR EXPRESSIONS
            //ALL OF txt MUST MATCH
            const string digit = "((0)|(1)|(2)|(3)|(4)|(5)|(6)|(7)|(8)|(9))";
            const string fp = digit + digit + "*\\." + digit + digit + "*";
            const string fpe = fp + "e((\\+)|(-))" + digit + digit;

            var alpha =
                "((a)|(b)|(c)|(d)|(e)|(f)|(g)|(h)|(i)|(j)|(k)|(l)|(m)|(n)|(o)|(p)|(q)|(r)|(s)|(t)|(u)|(v)|(w)|(x)|(y)|(z)|(_)|(-))";
            var iden = alpha + "(" + alpha + "|" + digit + ")*";
            var url = "((http)|(ftp)|(afs)|(gopher))//:" + iden + "/" + iden;
            var day = "((mon)|(tue)|(wed)|(thur)|(fri)|(sat)|(sun))";
            var month = "((jan)|(feb)|(mar)|(apr)|(may)|(jun)|(jul)|(aug)|(sep)|(oct)|(nov)|(dec))";
            var date = day + digit + digit + month;
            //var re : RegExp;

            //Pattern p = Pattern.compile(url);

            //Console.WriteLine("{0}  {1}", txt, iden); 
            //re = new RegExp(url);
            //re.regex.matchinexact.ParseFromRegExp();
            //print(stringUtils.PrettyPrint(re.regex.matchinexact.fsaexact));  
            //if (0 == re.regex.matchinexact.Match(txt)) {
            if (System.Text.RegularExpressions.Regex.IsMatch(txt, url))
            {
                return "url";
            }
            //print(stringUtils.PrettyPrint(re.regex.matchinexact));  

            //Console.WriteLine("{0}  {1}", txt, iden); 
            //re = new RegExp(date);
            //re.regex.matchinexact.ParseFromRegExp();
            //print(stringUtils.PrettyPrint(re.regex.matchinexact.fsaexact));  
            //  if (0 == re.regex.matchinexact.Match(txt)) {
            if (System.Text.RegularExpressions.Regex.IsMatch(txt, date))
            {
                return "date";
            }
            //print(stringUtils.PrettyPrint(re.regex.matchinexact));  

            //Console.WriteLine("{0}  {1}", txt, fpe); 
            //re = new RegExp(fpe);
            //re.regex.matchinexact.ParseFromRegExp();
            //print(stringUtils.PrettyPrint(re.regex.matchinexact.fsaexact));  
            // if (0 == re.regex.matchinexact.Match(txt)) {
            return System.Text.RegularExpressions.Regex.IsMatch(txt, fpe) ? "fpe" : "none";

            //print(stringUtils.PrettyPrint(re.regex.matchinexact));
        }
    }
}