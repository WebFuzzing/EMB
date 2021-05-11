using System;

namespace SCS.Imp
{
    public class Calc
    {
        public static string Subject(string op, double arg1 , double arg2 )
        {
            op = op.ToLower();
            
            var result = op switch
            {
                "pi" => Math.PI,
                "e" => Math.E,
                "sqrt" => Math.Sqrt(arg1),
                "log" => Math.Log(arg1),
                "sine" => Math.Sin(arg1),
                "cosine" => Math.Cos(arg1),
                "tangent" => Math.Tan(arg1),
                "plus" => arg1 + arg2,
                "subtract" => arg1 - arg2,
                "multiply" => arg1 * arg2,
                "divide" => arg1 / arg2,
                _ => 0.0
            };
            
            return "" + result;
        }
    }
}