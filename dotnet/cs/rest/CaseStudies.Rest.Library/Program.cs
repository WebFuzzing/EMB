using System;
using CaseStudies.Rest.Library.Contexts;
using Microsoft.AspNetCore;
using Microsoft.AspNetCore.Hosting;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using System.Collections.Concurrent;
using System.Threading;

namespace CaseStudies.Rest.Library {
    public class Program {
        private static ConcurrentDictionary<int, CancellationTokenSource> tokens = new ConcurrentDictionary<int, CancellationTokenSource> ();

        public static void Main (string[] args) {
            if (args.Length > 0) {

                int port = Convert.ToInt32 (args[0]);

                tokens.TryAdd (port, new CancellationTokenSource ());

                var host = CreateWebHostBuilder (args).Build ();

                // Migrate (host);

                host.RunAsync (tokens[port].Token).GetAwaiter ().GetResult ();
            } else {
                var host = CreateWebHostBuilder (args).Build ();

                // Migrate (host);

                host.RunAsync ().GetAwaiter ().GetResult ();
            }
        }

        // migrate the database.  Best practice = in Main, using service scope
        private static void Migrate (IWebHost host) {
            using (var scope = host.Services.CreateScope ()) {
                try {
                    var context = scope.ServiceProvider.GetService<LibraryContext> ();
                    context.Database.Migrate ();
                } catch (Exception ex) {
                    var logger = scope.ServiceProvider.GetRequiredService<ILogger<Program>> ();
                    logger.LogError (ex, "An error occurred while migrating the database.");
                }
            }
        }
        public static IWebHostBuilder CreateWebHostBuilder (string[] args) {

            var webHostBuilder = WebHost.CreateDefaultBuilder (args)
                .UseStartup<Startup> ();

            return args.Length > 0 ? webHostBuilder.UseUrls ($"http://*:{args[0]}") : webHostBuilder;
        }

        public static void Shutdown () {

            foreach (var pair in tokens) {
                pair.Value.Cancel ();
            }

            tokens.Clear ();
        }
    }
}