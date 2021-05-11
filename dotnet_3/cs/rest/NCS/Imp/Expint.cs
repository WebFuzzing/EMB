using System;

namespace NCS.Imp
{
    public class Expint
    {
        private const double Maxit = 100;
        private const double Euler = 0.5772156649;
        private const double Fpmin = 1.0e-30;
        private const double Eps = 1.0e-7;

        public static double Exe(int n, double x)
        {
            int i, ii, nm1;
            double a, b, c, d, del, fact, h, psi, ans;

            nm1 = n - 1;

            if (n < 0 || x < 0.0 || (x == 0.0 && (n == 0 || n == 1)))
                throw new Exception("error: n < 0 or x < 0");
            else
            {
                if (n == 0)
                    ans = Math.Exp(-x) / x;
                else
                {
                    if (x == 0.0)
                        ans = 1.0 / nm1;
                    else
                    {
                        if (x > 1.0)
                        {
                            b = x + n;
                            c = 1.0 / Fpmin;
                            d = 1.0 / b;
                            h = d;

                            for (i = 1; i <= Maxit; i++)
                            {
                                a = -i * (nm1 + i);
                                b += 2.0;
                                d = 1.0 / (a * d + b);
                                c = b + a / c;
                                del = c * d;
                                h *= del;

                                if (Math.Abs(del - 1.0) < Eps)
                                {
                                    return h * Math.Exp(-x);
                                }
                            }

                            throw new Exception("continued fraction failed in expint");
                        }
                        else
                        {
                            ans = (nm1 != 0 ? 1.0 / nm1 : -Math.Log(x) - Euler);
                            fact = 1.0;

                            for (i = 1; i <= Maxit; i++)
                            {
                                fact *= -x / i;

                                if (i != nm1)
                                    del = -fact / (i - nm1);
                                else
                                {
                                    psi = -Euler;

                                    for (ii = 1; ii <= nm1; ii++)
                                        psi += 1.0 / ii;

                                    del = fact * (-Math.Log(x) + psi);
                                }

                                ans += del;

                                if (Math.Abs(del) < Math.Abs(ans) * Eps)
                                {
                                    return ans;
                                }
                            }

                            throw new Exception("series failed in expint");
                        }
                    }
                }
            }

            return ans;
        }
    }
}