using System;

namespace SCS.Imp
{
    public class Ordered4
    {
        public static string Subject(string w, string x, string z, string y)
        {
            var result = "unordered";
            
            if (w.Length >= 5 && w.Length <= 6 && //LIMIT LENGTH TO LIMIT PROB OF RANDOM SATISFACTION
                x.Length >= 5 && x.Length <= 6 &&
                y.Length >= 5 && y.Length <= 6 &&
                z.Length >= 5 && z.Length <= 6)
            {
                if (string.Compare(z, y, StringComparison.Ordinal) > 0 &&
                    string.Compare(y, x, StringComparison.Ordinal) > 0 &&
                    string.Compare(x, w, StringComparison.Ordinal) > 0)
                {
                    result = "increasing";
                }
                else if (string.Compare(w, x, StringComparison.Ordinal) > 0 &&
                         string.Compare(x, y, StringComparison.Ordinal) > 0 &&
                         string.Compare(y, z, StringComparison.Ordinal) > 0)
                {
                    result = "decreasing";
                }
            }

            return result;
        }
    }
}