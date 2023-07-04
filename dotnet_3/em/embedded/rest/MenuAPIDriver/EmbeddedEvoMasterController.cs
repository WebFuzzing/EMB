using System;
using System.Collections.Generic;
using System.Data.Common;
using System.Threading.Tasks;
using EvoMaster.Controller;
using EvoMaster.Controller.Api;
using EvoMaster.Controller.Problem;
using Npgsql;
using EvoMaster.Controller.Controllers.db;
using Testcontainers.PostgreSql;

namespace Menu {
    public class EmbeddedEvoMasterController : EmbeddedSutController {
        private bool _isSutRunning;
        private static int _sutPort;
        private DbConnection _connection;
        private readonly PostgreSqlContainer _postgreSqlContainer = new PostgreSqlBuilder().Build();

        private static void Main(string[] args) {
            var embeddedEvoMasterController = new EmbeddedEvoMasterController();
            
            var controllerPort = 40100;
            if (args.Length > 0) {
                controllerPort = Int32.Parse(args[0]);
            }
            embeddedEvoMasterController.SetControllerPort(controllerPort);

            if (args.Length > 1) {
                _sutPort = Int32.Parse(args[1]);
            } 

            var instrumentedSutStarter = new InstrumentedSutStarter(embeddedEvoMasterController);

            Console.WriteLine("Driver is starting...\n");

            instrumentedSutStarter.Start();
        }
        
        public EmbeddedEvoMasterController(){
            _sutPort = GetEphemeralTcpPort();
        }

        public override string GetDatabaseDriverName() => null;

        public override List<AuthenticationDto> GetInfoForAuthentication() => null;

        public override string GetPackagePrefixesToCover() => "Menu.API";

        public override OutputFormat GetPreferredOutputFormat() => OutputFormat.CSHARP_XUNIT;

        //TODO: check again
        public override IProblemInfo GetProblemInfo() =>
            GetSutPort() != 0
                ? new RestProblem("http://localhost:" + GetSutPort() + "/swagger/v1/swagger.json", null)
                : new RestProblem(null, null);

        public override bool IsSutRunning() => _isSutRunning;

        public override void ResetStateOfSut() {
            DbCleaner.ClearDatabase(_connection, null, DatabaseType.POSTGRES);
        }

        public override string StartSut() {

            Task.Run(async () => {
                // TODO why is this not taken from Docker??? 
                var dbPort = GetEphemeralTcpPort();
                
                await _postgreSqlContainer.StartAsync();

                await using (DbConnection connection = new NpgsqlConnection(_postgreSqlContainer.GetConnectionString()))
                {
                    _connection = connection;
                    API.Program.Main(new[] {$"{_sutPort}", connection.ConnectionString});

                }
            });

            WaitUntilSutIsRunning(_sutPort, 45);
            
            _isSutRunning = true;

            return $"http://localhost:{_sutPort}";
        }

        public override async void StopSut() {
            API.Program.Shutdown();
            await _postgreSqlContainer.StopAsync();
            _isSutRunning = false;
        }

        protected int GetSutPort() => _sutPort;
    }
}