namespace SCS.Imp
{
    public class Title
    {
        public static string Subject(string sex, string title)
        {
            //CHECK PERSONAL TITLE CONSISTENT WITH SEX
            sex = sex.ToLower();
            title = title.ToLower();
            var result = -1;

            if (sex == "male") {
                if ("mr".Equals(title) ||
                    "dr".Equals(title) ||
                    "sir".Equals(title) ||
                    "rev".Equals(title) ||
                    "rthon".Equals(title) ||
                    "prof".Equals(title)) {
                    result = 1;
                }
            }
            else if (sex == "female") {
                if ("mrs".Equals(title) ||
                    "miss".Equals(title) ||
                    "ms".Equals(title) ||
                    "dr".Equals(title) ||
                    "lady".Equals(title) ||
                    "rev".Equals(title) ||
                    "rthon".Equals(title) ||
                    "prof".Equals(title)) {
                    result = 0;
                }
            }
            else if (sex == "none") {
                if ("dr".Equals(title) ||
                    "rev".Equals(title) ||
                    "rthon".Equals(title) ||
                    "prof".Equals(title)) {
                    result = 2;
                }
            }

            return "" + result;
        }
    }
}