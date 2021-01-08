using Library.API.Models;
using Microsoft.OpenApi.Models;
using Swashbuckle.AspNetCore.SwaggerGen;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Library.API.OperationFilters
{
    public class CreateBookOperationFilter : IOperationFilter
    {
        public void Apply(OpenApiOperation operation, OperationFilterContext context)
        {
            if (operation.OperationId != "CreateBook")
            {
                return;
            }

            operation.RequestBody.Content.Add(
                "application/vnd.marvin.bookforcreationwithamountofpages+json",
                new OpenApiMediaType()
                {
                    Schema = context.SchemaRegistry.GetOrRegister(
                        typeof(BookForCreationWithAmountOfPages))
                });
        }
    }
}
