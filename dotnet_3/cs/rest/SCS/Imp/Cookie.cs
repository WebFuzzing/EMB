namespace SCS.Imp
{
    public class Cookie
    {
        public static string Subject(string name,  string val, string site)
        {
            name = name.ToLower();
            val = val.ToLower();
            site = site.ToLower();
            var result = 0;

            if (name == "userid") {
                if (val.Length > 6) {
                    if ("user".Equals(val[..4])) {
                        result = 1;
                    }
                }
            }
            else if (name == "session" && ("am".Equals(val) && "abc.com".Equals(site))) {
                result = 1;
            }
            else if (name == "session") {
                result = 2;
            }

            return "" + result;
        }
    }
}