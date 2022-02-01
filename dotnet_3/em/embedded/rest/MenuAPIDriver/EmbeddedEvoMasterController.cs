using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using EvoMaster.Controller;
using EvoMaster.Controller.Api;
using EvoMaster.Controller.Problem;
using Npgsql;
using EvoMaster.Controller.Controllers.db;
using EvoMaster.DatabaseController;
using EvoMaster.DatabaseController.Abstractions;

namespace Menu {
    public class EmbeddedEvoMasterController : EmbeddedSutController {
        private bool _isSutRunning;
        private static int _sutPort;
        private NpgsqlConnection _connection;
        private IDatabaseController _databaseController;

        private static void Main(string[] args) {
            var embeddedEvoMasterController = new EmbeddedEvoMasterController();
            
            var controllerPort = 40100;
            if (args.Length > 0) {
                controllerPort = Int32.Parse(args[0]);
            }
            embeddedEvoMasterController.SetControllerPort(controllerPort);

            if (args.Length > 1) {
                _sutPort = Int32.Parse(args[1]);
            } else {
                var ephemeralPort = embeddedEvoMasterController.GetEphemeralTcpPort();
                _sutPort = ephemeralPort;
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
            DbCleaner.ClearDatabase_Postgres(_connection);
        }

        public override string StartSut() {

            Task.Run(async () => {
                // TODO why is this not taken from Docker??? 
                var dbPort = GetEphemeralTcpPort();

                _databaseController = new PostgresDatabaseController("restaurant_menu_database", dbPort, "password123");

                var (connectionString, dbConnection) = await _databaseController.StartAsync();

                _connection = dbConnection as NpgsqlConnection;

                API.Program.Main(new[] {$"{_sutPort}", connectionString});
            });

            WaitUntilSutIsRunning(_sutPort);
            
            _isSutRunning = true;

            return $"http://localhost:{_sutPort}";
        }

        public override void StopSut() {
            API.Program.Shutdown();
            _databaseController.Stop();
            _isSutRunning = false;
        }

        protected int GetSutPort() => _sutPort;
    }
}