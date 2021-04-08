namespace NCS.Imp
{
    public class Remainder
    {
        public static int Exe(int a, int b)
        {
            var r = 0 - 1;
            var cy = 0;
            var ny = 0;

            if (a == 0) ;
            else if (b == 0) ;
            else if (a > 0)
                if (b > 0)
                    while ((a - ny) >= b)
                    {
                        ny += b;
                        r = a - ny;
                        cy += 1;
                    }
                else // b<0
                    //while((a+ny)>=Math.abs(b))
                    while ((a + ny) >= ((b >= 0) ? b : -b))
                    {
                        ny += b;
                        r = a + ny;
                        cy -= 1;
                    }
            else // a<0
            if (b > 0)
                //while(Math.abs(a+ny)>=b)
                while (((a + ny) >= 0 ? (a + ny) : -(a + ny)) >= b)
                {
                    ny += b;
                    r = a + ny;
                    cy -= 1;
                }
            else
                while (b >= (a - ny))
                {
                    ny += b;
                    //r=Math.abs(a-ny);
                    r = ((a - ny) >= 0 ? (a - ny) : -(a - ny));
                    cy += 1;
                }

            return r;
        }
    }
}