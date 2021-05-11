using System;

namespace NCS.Imp
{
    public class TriangleClassification
    {
        public static int Classify(int a, int b, int c)
        {
            if (a <= 0 || b <= 0 || c <= 0)
            {
                return 0;
            }

            if (a == b && b == c)
            {
                return 3;
            }

            var max = Math.Max(a, Math.Max(b, c));

            if ((max == a && max - b - c >= 0) ||
                (max == b && max - a - c >= 0) ||
                (max == c && max - a - b >= 0))
            {
                return 0;
            }

            if (a == b || b == c || a == c)
            {
                return 2;
            }

            return 1;
        }
    }
}