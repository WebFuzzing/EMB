using System;
using System.Collections.Concurrent;
using System.IO;
using System.Net;
using System.Reflection;
using System.Threading;
using Menu.API.Data;
using Microsoft.AspNetCore;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Server.Kestrel.Core;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using Serilog;
using Serilog.Events;
using Serilog.Sinks.SystemConsole.Themes;

namespace Menu.API
{
    public class Program
    {
        private static ConcurrentDictionary<int, CancellationTokenSource> tokens =
            new ConcurrentDictionary<int, CancellationTokenSource>();

        public static void Main(string[] args)
        {
            Log.Logger = new LoggerConfiguration()
                .MinimumLevel.Debug()
                .MinimumLevel.Override("Microsoft", LogEventLevel.Information)
                .MinimumLevel.Override("System", LogEventLevel.Information)
                .MinimumLevel.Override("Microsoft.AspNetCore.Authentication", LogEventLevel.Debug)
                .Enrich.FromLogContext()
                .WriteTo.Console(
                    outputTemplate:
                    "[{Timestamp:HH:mm:ss} {Level}] {SourceContext}{NewLine}{Message:lj}{NewLine}{Exception}{NewLine}",
                    theme: AnsiConsoleTheme.Literate)
                .CreateLogger();

            if (args.Length > 0)
            {
                var port = Convert.ToInt32(args[0]);
                tokens.TryAdd(port, new CancellationTokenSource());

                var host = CreateWebHostBuilder(args).Build()
                    .MigrateDbContext<ApplicationDbContext>((context, services) =>
                    {
                        var logger = services.GetRequiredService<ILogger<Program>>();
                        var configuration = services.GetRequiredService<IConfiguration>();

                        var connectionString = GetConnectionString(configuration);
                        logger.LogInformation(connectionString);
                        var dbContextLogger = services.GetRequiredService<ILogger<ApplicationDbContext>>();

                        var env = services.GetRequiredService<IWebHostEnvironment>();
                        new ApplicationDbContextSeed().SeedAsync(context, env, dbContextLogger);
                    });
                host.RunAsync(tokens[port].Token).GetAwaiter().GetResult();
            }
            else
            {
                CreateWebHostBuilder(args)
                    .Build()
                    .MigrateDbContext<ApplicationDbContext>((context, services) =>
                    {
                        var logger = services.GetRequiredService<ILogger<Program>>();
                        var configuration = services.GetRequiredService<IConfiguration>();

                        var connectionString = GetConnectionString(configuration);
                        logger.LogInformation(connectionString);
                        var dbContextLogger = services.GetRequiredService<ILogger<ApplicationDbContext>>();

                        var env = services.GetRequiredService<IWebHostEnvironment>();
                        new ApplicationDbContextSeed().SeedAsync(context, env, dbContextLogger);
                    })
                    .Run();
            }
        }

        public static IWebHostBuilder CreateWebHostBuilder(string[] args)
        {
            var webHostBuilder = WebHost.CreateDefaultBuilder(args)
                .UseStartup<Startup>().UseConfiguration(new ConfigurationBuilder()
                    .SetBasePath(Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location))
                    .AddJsonFile("appsettings.json", optional: true, reloadOnChange: true)
                    .Build());

            return args.Length
                switch
                {
                    0 => webHostBuilder,
                    1 => webHostBuilder.UseUrls($"http://*:{args[0]}"),
                    _ => webHostBuilder.UseUrls($"http://*:{args[0]}").UseSetting("ConnectionString", args[1])
                };
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
                configuration.GetConnectionString("ConnectionString") ??
                configuration.GetConnectionString("MenuDatabaseConnectionString");

            return res;
        }
    }
}