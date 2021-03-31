using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using EvoMaster.Controller;
using EvoMaster.Controller.Api;
using EvoMaster.Controller.Controllers.db;
using EvoMaster.Controller.Problem;
using Microsoft.Data.SqlClient;

namespace SampleProject
{
    public class EmbeddedEvoMasterController : EmbeddedSutController
    {
        private bool _isSutRunning;
        private int _sutPort;
        private SqlConnection _connection;
        // private TestcontainerDatabase _container;

        static void Main(string[] args)
        {
            var embeddedEvoMasterController = new EmbeddedEvoMasterController();

            var instrumentedSutStarter = new InstrumentedSutStarter(embeddedEvoMasterController);

            Console.WriteLine("Driver is starting...\n");

            instrumentedSutStarter.Start();
        }

        public override string GetDatabaseDriverName() => null;

        public override List<AuthenticationDto> GetInfoForAuthentication() => null;

        public override string GetPackagePrefixesToCover() => "SampleProject.API";

        public override OutputFormat GetPreferredOutputFormat() => OutputFormat.CSHARP_XUNIT;

        //TODO: check again
        public override IProblemInfo GetProblemInfo() =>
            GetSutPort() != 0
                ? new RestProblem("http://localhost:" + GetSutPort() + "/swagger/v1/swagger.json", null)
                : new RestProblem(null, null);

        public override bool IsSutRunning() => _isSutRunning;

        public override void ResetStateOfSut()
        {
            //TODO
            DbCleaner.ClearDatabase(_connection, null, DatabaseType.MS_SQL_SERVER);
        }                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        

        public override string StartSut()                                   
        {
            var ephemeralPort = GetEphemeralTcpPort();

            const int timeout = 300;                                                                                
            
            Task.Run(async () =>
            {
                var (connectionString, connection) = await DockerDatabaseStarter.StartAsync(DatabaseType.MS_SQL_SERVER,"Sample_Database", timeout);
                _connection = (SqlConnection) connection;
                //TODO
                _connection.ConnectionString = connectionString;
                API.Program.Main(new[] {ephemeralPort.ToString(), connectionString});
            });

             WaitUntilSutIsRunning(ephemeralPort, timeout);

            _sutPort = ephemeralPort;

            _isSutRunning = true;

            return $"http://localhost:{ephemeralPort}";
        }

        public override void StopSut()
        {
            API.Program.Shutdown();

            _isSutRunning = false;
        }

        protected int GetSutPort() => _sutPort;
    }
}