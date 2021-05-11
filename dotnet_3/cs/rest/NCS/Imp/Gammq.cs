using System;

namespace NCS.Imp
{
    public class Gammq
    {
        private const int Itmax = 100;
        private const double Eps = 3.0e-7;
        private const double Fpmin = 1.0e-30;

        private double _gamser, _gammcf, _gln;

        private double Gammln(double xx)
        {
            double x, y, tmp, ser;

            var cof = new[]
            {
                76.18009172947146, -86.50532032941677,
                24.01409824083091, -1.231739572450155,
                0.1208650973866179e-2, -0.5395239384953e-5
            };

            int j;

            y = x = xx;
            tmp = x + 5.5;
            tmp -= (x + 0.5) * Math.Log(tmp);
            ser = 1.000000000190015;
            for (j = 0; j <= 5; j++) ser += cof[j] / ++y;
            return -tmp + Math.Log(2.5066282746310005 * ser / x);
        }

        private void Gcf(double a, double x)
        {
            int i;
            double an, b, c, d, del, h;

            _gln = Gammln(a);
            b = x + 1.0 - a;
            c = 1.0 / Fpmin;
            d = 1.0 / b;
            h = d;
            for (i = 1; i <= Itmax; i++)
            {
                an = -i * (i - a);
                b += 2.0;
                d = an * d + b;
                if (Math.Abs(d) < Fpmin) d = Fpmin;
                c = b + an / c;
                if (Math.Abs(c) < Fpmin) c = Fpmin;
                d = 1.0 / d;
                del = d * c;
                h *= del;
                if (Math.Abs(del - 1.0) < Eps) break;
            }

            if (i > Itmax) throw new Exception("a too large, ITMAX too small in gcf");
            _gammcf = Math.Exp(-x + a * Math.Log(x) - _gln) * h;
        }

        private void Gser(double a, double x)
        {
            int n;
            double sum, del, ap;

            _gln = Gammln(a);

            if (x <= 0.0)
            {
                if (x < 0.0) throw new Exception("x less than 0 in routine Gser");
                _gamser = 0.0;
                return;
            }

            ap = a;
            del = sum = 1.0 / a;
            for (n = 1; n <= Itmax; n++)
            {
                ++ap;
                del *= x / ap;
                sum += del;
                if (Math.Abs(del) < Math.Abs(sum) * Eps)
                {
                    _gamser = sum * Math.Exp(-x + a * Math.Log(x) - _gln);
                    return;
                }
            }

            throw new Exception("a too large, ITMAX too small in routine gser");
        }

        public double Exe(double a, double x)
        {
            if (x < 0.0 || a <= 0.0) throw new Exception("Invalid arguments in routine gammq");
            if (x < (a + 1.0))
            {
                Gser(a, x);
                return 1 - _gamser;
            }

            Gcf(a, x);
            return _gammcf;
        }
    }
}