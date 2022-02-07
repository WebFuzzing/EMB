using System;

namespace SCS.Imp
{
    public class Calc
    {
        public static string Subject(string op, double arg1 , double arg2 )
        {
            op = op.ToLower();
            
            double result;
            if (op == "pi")
                result = Math.PI;
            else if (op == "e")
                result = Math.E;
            else if (op == "sqrt")
                result = Math.Sqrt(arg1);
            else if (op == "log")
                result = Math.Log(arg1);
            else if (op == "sine")
                result = Math.Sin(arg1);
            else if (op == "cosine")
                result = Math.Cos(arg1);
            else if (op == "tangent")
                result = Math.Tan(arg1);
            else if (op == "plus")
                result = arg1 + arg2;
            else if (op == "subtract")
                result = arg1 - arg2;
            else if (op == "multiply")
                result = arg1 * arg2;
            else if (op == "divide")
                result = arg1 / arg2;
            else
                result = 0.0;

            return "" + result;
        }
    }
}