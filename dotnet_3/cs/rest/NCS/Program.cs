using System;
using System.Collections.Concurrent;
using System.Threading;
using Microsoft.AspNetCore;
using Microsoft.AspNetCore.Hosting;

namespace NCS
{
    public class Program
    {
        private static ConcurrentDictionary<int, CancellationTokenSource> _tokens =
            new ConcurrentDictionary<int, CancellationTokenSource>();

        public static void Main(string[] args)
        {
            if (args.Length > 0)
            {
                int port = Convert.ToInt32(args[0]);

                _tokens.TryAdd(port, new CancellationTokenSource());

                var host = CreateWebHostBuilder(args).Build();

                host.RunAsync(_tokens[port].Token).GetAwaiter().GetResult();
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

            return args.Length > 0 ? webHostBuilder.UseUrls($"http://*:{args[0]}") : webHostBuilder;
        }

        public static void Shutdown()
        {
            foreach (var pair in _tokens)
            {
                pair.Value.Cancel();
            }

            _tokens.Clear();
        }
    }
}