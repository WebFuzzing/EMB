//using System;
//using System.Collections.Generic;
//using System.Linq;
//using System.Threading.Tasks;
//using Microsoft.AspNetCore.Http;
//using Microsoft.AspNetCore.Mvc;

//namespace Library.API.Controllers
//{
//    [Route("api/[controller]")]
//    [ApiController]
//    [ApiConventionType(typeof(CustomConventions))]
//    public class ConventionTestsController : ControllerBase
//    {
//        // GET: api/ConventionTests
//        [HttpGet]
//        public IEnumerable<string> Get()
//        {
//            return new string[] { "value1", "value2" };
//        }

//        // GET: api/ConventionTests/5
//        [HttpGet("{id}", Name = "Get")]
//        //[ApiConventionMethod(typeof(DefaultApiConventions),
//        //    nameof(DefaultApiConventions.Get))]
//        public string Get(int id)
//        {
//            return "value";
//        }

//        // POST: api/ConventionTests
//        [HttpPost]
//        //[ApiConventionMethod(typeof(CustomConventions),
//        //    nameof(CustomConventions.Insert))]
//        public void InsertTest([FromBody] string value)
//        {
//        }

//        // PUT: api/ConventionTests/5
//        [HttpPut("{id}")]
//        public void Put(int id, [FromBody] string value)
//        {
//        }

//        // DELETE: api/ApiWithActions/5
//        [HttpDelete("{id}")]
//        public void Delete(int id)
//        {
//        }
//    }
//}
