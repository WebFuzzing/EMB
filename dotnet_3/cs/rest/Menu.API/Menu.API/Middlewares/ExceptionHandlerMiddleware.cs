using System;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Http;
using Microsoft.EntityFrameworkCore;
using Newtonsoft.Json;
using Newtonsoft.Json.Serialization;

namespace Menu.API.Middlewares
{
   public class ExceptionHandlerMiddleware
    {
        private readonly RequestDelegate _next;

        public ExceptionHandlerMiddleware(RequestDelegate next)
        {
            _next = next ??
                throw new ArgumentNullException(nameof(next));
        }

        public async Task Invoke(HttpContext context)
        {
            try
            {
                await _next(context);
            }
            catch (Exception e)
            {
                await ProcessErrorResponseAsync(context, e.Message);
            }
            // catch (DbUpdateException e)
            // {
            //     if (e.InnerException != null && e.InnerException.Message.StartsWith("Cannot insert duplicate"))
            //     {
            //         var duplicateValue = new Regex(@"(?<=\()(.*?)(?=\))").Match(e.InnerException.Message);
            //
            //         await ProcessErrorResponseAsync(context, $"Cannot insert duplicate value. The duplicate value is '{duplicateValue}'");
            //     }
            //     else
            //     {
            //         await ProcessErrorResponseAsync(context, e.ToString());
            //     }
            // }
        }

        private async Task ProcessErrorResponseAsync(HttpContext context, string errorMessage)
        {
            var errorDto = new {ErrorMessage = errorMessage};

            context.Response.Clear();
            context.Response.StatusCode = 404;
            context.Response.ContentType = @"application/json";

            var serializerSettings = new JsonSerializerSettings
            {
                ContractResolver = new CamelCasePropertyNamesContractResolver()
            };

            await context.Response.WriteAsync(JsonConvert.SerializeObject(errorDto, serializerSettings));
        }
    }

    public static class ExceptionHandlerMiddlewareExtensions
    {
        public static IApplicationBuilder UseExceptionHandlerMiddleware(this IApplicationBuilder builder)
        {
            return builder.UseMiddleware<ExceptionHandlerMiddleware>();
        }
    }
}