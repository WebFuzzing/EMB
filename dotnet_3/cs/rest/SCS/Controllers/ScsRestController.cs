using Microsoft.AspNetCore.Mvc;

namespace SCS.Controllers
{
    [ApiController]
    [Route("api")]
    public class ScsRestController : ControllerBase
    {
        [HttpGet("calc/{op}/{arg1:double}/{arg2:double}")]
        public IActionResult Calc([FromRoute] string op, [FromRoute] double arg1, [FromRoute] double arg2)
        {
            var res = Imp.Calc.Subject(op, arg1, arg2);

            return Ok(res);
        }

        [HttpGet("cookie/{name}/{val}/{site}")]
        public IActionResult Cookie([FromRoute] string name, [FromRoute] string val, [FromRoute] string site)
        {
            var res = Imp.Cookie.Subject(name, val, site);

            return Ok(res);
        }

        [HttpGet("costFuns/{i:int}/{s}")]
        public IActionResult CostFuns([FromRoute] int i, [FromRoute] string s)
        {
            var res = Imp.Costfuns.Subject(i, s);

            return Ok(res);
        }

        [HttpGet("dateParse/{dayName}/{monthName}")]
        public IActionResult DateParse([FromRoute] string dayName, [FromRoute] string monthName)
        {
            var res = Imp.DateParse.Subject(dayName, monthName);

            return Ok(res);
        }

        [HttpGet("fileSuffix/{directory}/{file}")]
        public IActionResult FileSuffix([FromRoute] string directory, [FromRoute] string file)
        {
            var res = Imp.FileSuffix.Subject(directory, file);

            return Ok(res);
        }

        [HttpGet("notypevar/{i:int}/{s}")]
        public IActionResult NotyPevar([FromRoute] int i, [FromRoute] string s)
        {
            var res = Imp.NotyPevar.Subject(i, s);

            return Ok(res);
        }

        [HttpGet("ordered4/{w}/{x}/{z}/{y}")]
        public IActionResult Ordered4([FromRoute] string w, [FromRoute] string x, [FromRoute] string y,
            [FromRoute] string z)
        {
            var res = Imp.Ordered4.Subject(w, x, y, z);

            return Ok(res);
        }

        [HttpGet("pat/{txt}/{pat}")]
        public IActionResult Pat([FromRoute] string txt, [FromRoute] string pat)
        {
            var res = Imp.Pat.Subject(txt, pat);

            return Ok(res);
        }

        [HttpGet("pat/{txt}")]
        public IActionResult Regex([FromRoute] string txt)
        {
            var res = Imp.Regex.Subject(txt);

            return Ok(res);
        }

        [HttpGet("text2txt/{word1}/{word2}/{word3}")]
        public IActionResult Text2Txt([FromRoute] string word1, [FromRoute] string word2, [FromRoute] string word3)
        {
            var res = Imp.Text2Txt.Subject(word1, word2, word3);

            return Ok(res);
        }

        [HttpGet("title/{sex}/{title}")]
        public IActionResult Title([FromRoute] string sex, [FromRoute] string title)
        {
            var res = Imp.Title.Subject(sex, title);

            return Ok(res);
        }
    }
}