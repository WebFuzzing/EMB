using System;
using System.Collections.Concurrent;
using System.IO;
using System.Reflection;
using System.Runtime.InteropServices.ComTypes;
using System.Threading;
using Microsoft.AspNetCore;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using SampleProject.Infrastructure.Database;

namespace SampleProject.API
{
    public class Program
    {
        private static ConcurrentDictionary<int, CancellationTokenSource> tokens =
            new ConcurrentDictionary<int, CancellationTokenSource>();

        public static void Main(string[] args)
        {
            if (args.Length > 0)
            {
                var port = Convert.ToInt32(args[0]);

                tokens.TryAdd(port, new CancellationTokenSource());

                var host = CreateWebHostBuilder(args).Build();
                
                host.RunAsync(tokens[port].Token).GetAwaiter().GetResult();
            }
            else
            {
                var host = CreateWebHostBuilder(args).Build();

                host.RunAsync().GetAwaiter().GetResult();
            }
        }

        private static IWebHostBuilder CreateWebHostBuilder(string[] args)
        {
            var webHostBuilder = WebHost.CreateDefaultBuilder(args)
                .UseStartup<Startup>()
                .UseConfiguration(new ConfigurationBuilder()
                    .SetBasePath(Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location))
                    .AddJsonFile("appsettings.json", optional: true, reloadOnChange: true).Build());

            if (args.Length == 1)
                return webHostBuilder.UseUrls($"http://*:{args[0]}");
            if (args.Length > 1)
                return webHostBuilder.UseUrls($"http://*:{args[0]}").UseSetting("ConnectionStringFromDriver", args[1]);

            return webHostBuilder;
        }

        public static void Shutdown()
        {
            foreach (var pair in tokens)
            {
                pair.Value.Cancel();
            }

            tokens.Clear();
        }
        
        private static string GetConnectionString(IConfiguration configuration)
        {
            var res =
                configuration.GetConnectionString("ConnectionStringFromDriver") ??
                configuration.GetConnectionString("OrdersConnectionString");

            return res;
        }
    }
}