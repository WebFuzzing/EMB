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

            switch (sex)
            {
                case "male":
                {
                    if ("mr".Equals(title) ||
                        "dr".Equals(title) ||
                        "sir".Equals(title) ||
                        "rev".Equals(title) ||
                        "rthon".Equals(title) ||
                        "prof".Equals(title))
                    {
                        result = 1;
                    }

                    break;
                }
                case "female":
                {
                    if ("mrs".Equals(title) ||
                        "miss".Equals(title) ||
                        "ms".Equals(title) ||
                        "dr".Equals(title) ||
                        "lady".Equals(title) ||
                        "rev".Equals(title) ||
                        "rthon".Equals(title) ||
                        "prof".Equals(title))
                    {
                        result = 0;
                    }

                    break;
                }
                case "none":
                {
                    if ("dr".Equals(title) ||
                        "rev".Equals(title) ||
                        "rthon".Equals(title) ||
                        "prof".Equals(title))
                    {
                        result = 2;
                    }

                    break;
                }
            }

            return "" + result;
        }
    }
}