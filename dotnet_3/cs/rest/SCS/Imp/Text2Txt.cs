namespace SCS.Imp {
    public class Text2Txt {
        public static string Subject(string word1, string word2, string word3) {
            //CONVERT ENGLISH TEXT txt INTO MOBILE TELEPHONE TXT
            //BY SUBSTITUTING ABBREVIATIONS FOR COMMON WORDS
            word1 = word1.ToLower();
            word2 = word2.ToLower();
            word3 = word3.ToLower();
            var result = "";

            if (word1.Equals("two"))
                result = "2";
            else if (word1.Equals("for") || word1.Equals("four"))
                result = "4";
            else if (word1.Equals("you"))
                result = "u";
            else if (word1.Equals("and"))
                result = "n";
            else if (word1.Equals("are"))
                result = "r";
            else if (word1.Equals("see") && word2.Equals("you"))
                result = "cu";
            else if (word1.Equals("by") && (word2.Equals("the") && word3.Equals("way"))) result = "btw";

            return result;
        }
    }
}