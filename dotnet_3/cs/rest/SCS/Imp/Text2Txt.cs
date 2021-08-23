namespace SCS.Imp
{
    public class Text2Txt
    {
        public static string Subject(string word1, string word2, string word3)
        {
            //CONVERT ENGLISH TEXT txt INTO MOBILE TELEPHONE TXT
            //BY SUBSTITUTING ABBREVIATIONS FOR COMMON WORDS
            word1 = word1.ToLower();
            word2 = word2.ToLower();
            word3 = word3.ToLower();
            var result = "";
            
            switch (word1)
            {
                case "two":
                    result = "2";
                    break;
                case "for":
                case "four":
                    result = "4";
                    break;
                case "you":
                    result = "u";
                    break;
                case "and":
                    result = "n";
                    break;
                case "are":
                    result = "r";
                    break;
                case "see" when word2.Equals("you"):
                    result = "cu";
                    break;
                case "by" when word2.Equals("the") && word3.Equals("way"):
                    result = "btw";
                    break;
            }

            return result;
        }
    }
}