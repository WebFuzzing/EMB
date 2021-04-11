using System;

namespace SCS.Imp
{
    public class NotyPevar
    {
        //SHOW USE OF UNTYPED VARIABLES
        public static string Subject(int i, string s)
        {
            var result = 0;
            var x = i;
            var y = x;
            if (x + y == 56)
            {
                //i0
                result = x;
            }

            var xs = "hello";
            if ((xs + y).Equals("hello7"))
            {
                //i1
                result = 1;
            }

            if (string.Compare(xs, s, StringComparison.Ordinal) < 0)
            {
                //i2
                result = 2;
            }

            x = 5;
            if (y > x)
            {
                //i3
                result = 3;
            }

            return "" + result;
        }
    }
}