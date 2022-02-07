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

            if (monthName == "jan")
                result += 1;
            else if (monthName == "feb")
                result += 2;
            else if (monthName == "mar")
                result += 3;
            else if (monthName == "apr")
                result += 4;
            else if (monthName == "may")
                result += 5;
            else if (monthName == "jun")
                result += 6;
            else if (monthName == "jul")
                result += 7;
            else if (monthName == "aug")
                result += 8;
            else if (monthName == "sep")
                result += 9;
            else if (monthName == "oct")
                result += 10;
            else if (monthName == "nov")
                result += 11;
            else if (monthName == "dec") result += 12;

            return "" + result;
        }
    }
}