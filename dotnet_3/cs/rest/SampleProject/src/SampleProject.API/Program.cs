using System;
using System.Collections.Concurrent;
using System.Runtime.InteropServices.ComTypes;
using System.Threading;
using Microsoft.AspNetCore;
using Microsoft.AspNetCore.Hosting;

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
                .UseStartup<Startup>();

            if (args.Length == 1)
                return webHostBuilder.UseUrls($"http://*:{args[0]}");
            if (args.Length > 1)
                return webHostBuilder.UseUrls($"http://*:{args[0]}").UseSetting("ConnectionString", args[1]);

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
    }
}