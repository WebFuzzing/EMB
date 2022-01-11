using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Docker.DotNet;
using EvoMaster.Controller;
using EvoMaster.Controller.Api;
using EvoMaster.Controller.Controllers.db;
using EvoMaster.Controller.Problem;
using EvoMaster.DatabaseController;
using EvoMaster.DatabaseController.Abstractions;
using Microsoft.Data.SqlClient;

namespace SampleProjectDriver
{
    public class EmbeddedEvoMasterController : EmbeddedSutController
    {
        private bool _isSutRunning;
        private int _sutPort;
        private SqlConnection _connection;
        private IDatabaseController _databaseController;

        static void Main(string[] args)
        {
            var embeddedEvoMasterController = new EmbeddedEvoMasterController();

            var instrumentedSutStarter = new InstrumentedSutStarter(embeddedEvoMasterController);

            Console.WriteLine("Driver is starting...\n");

            instrumentedSutStarter.Start();
        }

        public override string GetDatabaseDriverName() => null;

        public override List<AuthenticationDto> GetInfoForAuthentication() => null;

        public override string GetPackagePrefixesToCover() => "SampleProjectDriver.API";

        public override OutputFormat GetPreferredOutputFormat() => OutputFormat.CSHARP_XUNIT;

        public override IProblemInfo GetProblemInfo() =>
            GetSutPort() != 0
                ? new RestProblem("http://localhost:" + GetSutPort() + "/swagger/v1/swagger.json", null)
                : new RestProblem(null, null);

        public override bool IsSutRunning() => _isSutRunning;

        public override void ResetStateOfSut()
        {
            //TODO
            DbCleaner.ClearDatabase(_connection, null, DatabaseType.MS_SQL_SERVER, "");
        }

        public override string StartSut()
        {
            var ephemeralPort = GetEphemeralTcpPort();

            const int timeout = 300;

            Task.Run(async () =>
            {
                var dbPort = GetEphemeralTcpPort();

                _databaseController = new SqlServerDatabaseController("SampleCQRS", dbPort, "sqlpass@123", timeout, "mcr.microsoft.com/mssql/server:2017-CU14-ubuntu");

                var (connectionString, dbConnection) = await _databaseController.StartAsync();

                _connection = dbConnection as SqlConnection;

                SampleProject.API.Program.Main(new[] {ephemeralPort.ToString(), connectionString});
            });

            WaitUntilSutIsRunning(ephemeralPort, timeout);

            _sutPort = ephemeralPort;

            _isSutRunning = true;

            return $"http://localhost:{ephemeralPort}";
        }

        public override void StopSut()
        {
            SampleProject.API.Program.Shutdown();
            _databaseController.Stop();
            _isSutRunning = false;
        }

        private int GetSutPort() => _sutPort;
    }
}