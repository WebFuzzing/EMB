using System;
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
            var dto = new Dto {ResultAsInt = TriangleClassification.Classify(a, b, c)};

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

            var dto = new Dto {ResultAsDouble = new Bessj().BessjFunction(n, x)};

            return Ok(dto);
        }

        [HttpGet("expint/{n:int}/{x:double}")]
        [Produces("application/json")]
        public IActionResult Expint(int n, double x)
        {
            try
            {
                var dto = new Dto {ResultAsDouble = Imp.Expint.Exe(n, x)};

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
                var dto = new Dto {ResultAsDouble = Imp.Fisher.Exe(m, n, x)};

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

                dto.ResultAsDouble = gammq.Exe(a, x);

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

            var dto = new Dto {ResultAsInt = Imp.Remainder.Exe(a, b)};

            return Ok(dto);
        }
    }
}