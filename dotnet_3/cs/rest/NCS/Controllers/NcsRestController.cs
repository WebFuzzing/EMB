using System;
using System.Globalization;
using Microsoft.AspNetCore.Mvc;
using NCS.Imp;

namespace NCS.Controllers
{
    [ApiController]
    [Route("api")]
    public class NcsRestController : ControllerBase
    {
        [HttpGet("triangle/{a:int}/{b:int}/{c:int}")]
        [Produces("application/json")]
        public IActionResult CheckTriangle(int a, int b, int c)
        {
            var dto = new Dto {Result = TriangleClassification.Classify(a, b, c).ToString()};

            return Ok(dto);
        }

        [HttpGet("bessj/{n:int}/{x:double}")]
        [Produces("application/json")]
        public IActionResult Bessj(int n, double x)
        {
            if (n <= 2 || n > 1000)
            {
                return BadRequest();
            }

            var dto = new Dto {Result = new Bessj().BessjFunction(n, x).ToString(CultureInfo.InvariantCulture)};

            return Ok(dto);
        }

        [HttpGet("expint/{n:int}/{x:double}")]
        [Produces("application/json")]
        public IActionResult Expint(int n, double x)
        {
            try
            {
                var dto = new Dto {Result = Imp.Expint.Exe(n, x).ToString(CultureInfo.InvariantCulture)};

                return Ok(dto);
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }

        [HttpGet("fisher/{m:int}/{n:int}/{x:double}")]
        [Produces("application/json")]
        public IActionResult Fisher(int m, int n, double x)
        {
            if (m > 1000 || n > 1000)
            {
                return BadRequest();
            }

            try
            {
                var dto = new Dto {Result = Imp.Fisher.Exe(m, n, x).ToString(CultureInfo.InvariantCulture)};

                return Ok(dto);
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }

        [HttpGet("gammq/{a:double}/{x:double}")]
        [Produces("application/json")]
        public IActionResult Gammq(double a, double x)
        {
            try
            {
                var dto = new Dto();

                var gammq = new Gammq();

                dto.Result = gammq.Exe(a, x).ToString(CultureInfo.InvariantCulture);

                return Ok(dto);
            }
            catch (Exception e)
            {
                return BadRequest(e.Message);
            }
        }

        [HttpGet("remainder/{a:int}/{b:int}")]
        [Produces("application/json")]
        public IActionResult Remainder(int a, int b)
        {
            const int lim = 10_000;

            if (a > lim || a < -lim || b > lim || b < -lim)
            {
                return BadRequest();
            }

            var dto = new Dto {Result = Imp.Remainder.Exe(a, b).ToString()};

            return Ok(dto);
        }
    }
}