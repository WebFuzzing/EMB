namespace SCS.Imp
{
    public class Pat
    {
        private static string Reverse(string s)
        {
            //RTN REVERSE OF s
            var sLen = s.Length;
            if (sLen < 2)
            {
                return s;
            }

            var result = "";
            //var result : System.Text.stringBuilder = new System.Text.stringBuilder(sLen);
            //var i : int = sLen - 1;
            for (var i = sLen - 1; i >= 0; i--)
            {
                //result.Append(s[i]);
                result += s[i];
            }

            //Console.WriteLine("s {0} revs {1}", s, result.Tostring());
            return result;
        }

        public static string Subject(string txt, string pat)
        {
            //SEARCH txt FOR FIRST OCCURRENCE OF pat OR REVERSE OF pat
            //IF pat (STRING OF LENGTH AT LEAST 3) OCCURS IN txt, RTN 1
            //IF REVERSE OF pat OCCURS IN txt, RTN 2
            //IF pat AND REVERSE OF pat OCCURS IN txt, RTN 3
            //IF PALINDROME CONSISTING OF pat FOLLOWED BY REVERSE pat OCCURS IN txt, RTN 4
            //IF PALINDROME CONSISTING OF REVERSE pat FOLLOWED pat OCCURS IN txt, RTN 5
            var result = 0;
            var txtLen = txt.Length;
            var patLen = pat.Length;

            if (patLen > 2)
            {
                var patRev = Reverse(pat);
                int i;
                for (i = 0; i <= txtLen - patLen; i++)
                {
                    string possMatch = null;
                    int j;
                    if (txt[i] == pat[0])
                    {
                        possMatch = txt.Substring(i, patLen);
                        if (possMatch.Equals(pat))
                        {
                            //FOUND pat
                            result = 1;
                            //CHECK IF txt CONTAINS REVERSE pat
                            for (j = i + patLen; j <= txtLen - patLen; j++)
                            {
                                if (txt[j] == patRev[0])
                                {
                                    //possMatch = txt.Substring(j, j + patLen);
                                    possMatch = txt.Substring(j,  patLen);
                                    if (possMatch.Equals(patRev))
                                    {
                                        if (j == i + patLen)
                                        {
                                            return "" + i; //4;
                                        }
                                        else
                                        {
                                            return "" + i; //3;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if (txt[i] == patRev[0])
                    {
                        possMatch = txt.Substring(i, patLen);
                        if (possMatch.Equals(patRev))
                        {
                            //FOUND pat REVERSE
                            result = 2;
                            //CHECK IF txt CONTAINS pat
                            for (j = i + patLen; j <= txtLen - patLen; j++)
                            {
                                if (txt[j] == pat[0])
                                {
                                    possMatch = txt.Substring(j, patLen);
                                    if (possMatch.Equals(pat))
                                    {
                                        if (j == i + patLen)
                                        {
                                            return "" + i; //5;
                                        }
                                        else
                                        {
                                            return "" + i; //3;
                                        }
                                    }
                                }
                            }
                        }
                    }
                } //pat NOR REVERSE FOUND
            }

            return "" + result;
        }
    }
}