using System;
using System.Collections.Generic;
using EvoMaster.Controller;
using EvoMaster.Controller.Api;
using EvoMaster.Controller.Problem;

namespace NCS
{
    public class EmbeddedEvoMasterController : EmbeddedSutController
    {
        private bool isSutRunning;
        private int sutPort;
        
        static void Main(string[] args)
        {
            var embeddedEvoMasterController = new EmbeddedEvoMasterController ();

            InstrumentedSutStarter instrumentedSutStarter = new InstrumentedSutStarter (embeddedEvoMasterController);

            System.Console.WriteLine ("Driver is starting...\n");

            instrumentedSutStarter.Start ();
        }

        public override void StopSut()
        {
            throw new NotImplementedException();
        }

        public override bool IsSutRunning()
        {
            throw new NotImplementedException();
        }

        public override string GetPackagePrefixesToCover()
        {
            throw new NotImplementedException();
        }

        public override List<AuthenticationDto> GetInfoForAuthentication()
        {
            throw new NotImplementedException();
        }

        public override string GetDatabaseDriverName()
        {
            throw new NotImplementedException();
        }

        public override IProblemInfo GetProblemInfo()
        {
            throw new NotImplementedException();
        }

        public override OutputFormat GetPreferredOutputFormat()
        {
            throw new NotImplementedException();
        }

        public override void ResetStateOfSut()
        {
            throw new NotImplementedException();
        }

        public override string StartSut()
        {
            throw new NotImplementedException();
        }
    }
}