using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using EvoMaster.Controller;
using EvoMaster.Controller.Api;
using EvoMaster.Controller.Problem;

namespace ScsDriver {
    public class EmbeddedEvoMasterController : EmbeddedSutController {
        private bool _isSutRunning;
        private static int _sutPort;

        static void Main(string[] args) {
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

        public override string StartSut() {

            Task.Run(() => { SCS.Program.Main(new[] {_sutPort.ToString()}); });

            WaitUntilSutIsRunning(_sutPort);

            _isSutRunning = true;

            return $"http://localhost:{_sutPort}";
        }

        public override void StopSut() {
            SCS.Program.Shutdown();
            _isSutRunning = false;
        }

        public override bool IsSutRunning() => _isSutRunning;

        public override string GetPackagePrefixesToCover() => "SCS";

        public override List<AuthenticationDto> GetInfoForAuthentication() => null;

        public override string GetDatabaseDriverName() => null;

        //TODO: check
        public override IProblemInfo GetProblemInfo() =>
            GetSutPort() != 0
                ? new RestProblem("http://localhost:" + GetSutPort() + "/swagger/v1/swagger.json", null)
                : new RestProblem(null, null);

        public override OutputFormat GetPreferredOutputFormat() => OutputFormat.CSHARP_XUNIT;

        public override void ResetStateOfSut() {
        }

        private int GetSutPort() => _sutPort;
    }
}