package em.embedded.org.springframework.samples.petclinic;

import org.evomaster.client.java.controller.EmbeddedSutController;
import org.evomaster.client.java.controller.api.dto.AuthenticationDto;
import org.evomaster.client.java.controller.api.dto.SutInfoDto;
import org.evomaster.client.java.controller.problem.ProblemInfo;

import java.sql.Connection;
import java.util.List;

public class EmbeddedEvoMasterController extends EmbeddedSutController {



    @Override
    public boolean isSutRunning() {
        return false;
    }

    @Override
    public String getPackagePrefixesToCover() {
        return null;
    }

    @Override
    public List<AuthenticationDto> getInfoForAuthentication() {
        return null;
    }

    @Override
    public Connection getConnection() {
        return null;
    }

    @Override
    public String getDatabaseDriverName() {
        return null;
    }

    @Override
    public ProblemInfo getProblemInfo() {
        return null;
    }

    @Override
    public SutInfoDto.OutputFormat getPreferredOutputFormat() {
        return null;
    }

    @Override
    public String startSut() {
        return null;
    }

    @Override
    public void stopSut() {

    }

    @Override
    public void resetStateOfSUT() {

    }
}
