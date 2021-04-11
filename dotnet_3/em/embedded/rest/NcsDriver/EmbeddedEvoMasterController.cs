using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using EvoMaster.Controller;
using EvoMaster.Controller.Api;
using EvoMaster.Controller.Problem;

namespace NcsDriver
{
    public class EmbeddedEvoMasterController : EmbeddedSutController
    {
        private bool _isSutRunning;
        private int _sutPort;

        static void Main(string[] args)
        {
            var embeddedEvoMasterController = new EmbeddedEvoMasterController();

            var instrumentedSutStarter = new InstrumentedSutStarter(embeddedEvoMasterController);

            Console.WriteLine("Driver is starting...\n");

            instrumentedSutStarter.Start();
        }

        public override string StartSut()
        {
            var ephemeralPort = GetEphemeralTcpPort();

            Task.Run(() =>
            {
                NCS.Program.Main(new[] {ephemeralPort.ToString()});
            });

            WaitUntilSutIsRunning(ephemeralPort);

            _sutPort = ephemeralPort;

            _isSutRunning = true;

            return $"http://localhost:{ephemeralPort}";
        }

        public override void StopSut()
        {
            NCS.Program.Shutdown();
            _isSutRunning = false;
        }

        public override bool IsSutRunning() => _isSutRunning;

        public override string GetPackagePrefixesToCover() => "NCS";

        public override List<AuthenticationDto> GetInfoForAuthentication() => null;

        public override string GetDatabaseDriverName() => null;

        public override IProblemInfo GetProblemInfo() =>
            GetSutPort() != 0
                ? new RestProblem("http://localhost:" + GetSutPort() + "/swagger/v1/swagger.json", null)
                : new RestProblem(null, null);

        public override OutputFormat GetPreferredOutputFormat() => OutputFormat.CSHARP_XUNIT;

        public override void ResetStateOfSut()
        {
        }

        protected int GetSutPort() => _sutPort;
    }
}