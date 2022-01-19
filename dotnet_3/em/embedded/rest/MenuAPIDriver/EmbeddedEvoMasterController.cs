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
        private int _sutPort;
        private NpgsqlConnection _connection;
        private IDatabaseController _databaseController;

        private static void Main(string[] args) {
            var embeddedEvoMasterController = new EmbeddedEvoMasterController();

            var instrumentedSutStarter = new InstrumentedSutStarter(embeddedEvoMasterController);

            Console.WriteLine("Driver is starting...\n");

            instrumentedSutStarter.Start();
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
            var ephemeralPort = GetEphemeralTcpPort();

            Task.Run(async () => {
                var dbPort = GetEphemeralTcpPort();

                _databaseController = new PostgresDatabaseController("restaurant_menu_database", dbPort, "password123");

                var (connectionString, dbConnection) = await _databaseController.StartAsync();

                _connection = dbConnection as NpgsqlConnection;

                API.Program.Main(new[] {$"{ephemeralPort}", connectionString});
            });

            WaitUntilSutIsRunning(ephemeralPort);

            _sutPort = ephemeralPort;

            _isSutRunning = true;

            return $"http://localhost:{ephemeralPort}";
        }

        public override void StopSut() {
            API.Program.Shutdown();
            _databaseController.Stop();
            _isSutRunning = false;
        }

        protected int GetSutPort() => _sutPort;
    }
}