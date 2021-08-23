namespace SCS.Imp
{
    public class DateParse
    {
        public static string Subject(string dayName, string monthName)
        {
            var result = 0;
            //int month = -1;
            dayName = dayName.ToLower();
            monthName = monthName.ToLower();

            if ("mon".Equals(dayName) ||
                "tue".Equals(dayName) ||
                "wed".Equals(dayName) ||
                "thur".Equals(dayName) ||
                "fri".Equals(dayName) ||
                "sat".Equals(dayName) ||
                "sun".Equals(dayName))
            {
                result = 1;
            }

            switch (monthName)
            {
                case "jan":
                    result += 1;
                    break;
                case "feb":
                    result += 2;
                    break;
                case "mar":
                    result += 3;
                    break;
                case "apr":
                    result += 4;
                    break;
                case "may":
                    result += 5;
                    break;
                case "jun":
                    result += 6;
                    break;
                case "jul":
                    result += 7;
                    break;
                case "aug":
                    result += 8;
                    break;
                case "sep":
                    result += 9;
                    break;
                case "oct":
                    result += 10;
                    break;
                case "nov":
                    result += 11;
                    break;
                case "dec":
                    result += 12;
                    break;
            }

            return "" + result;
        }
    }
}