package org.devgateway.ocds.persistence.mongo.reader;

import java.text.ParseException;
import java.util.Date;
import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.repository.main.ReleaseRepository;
import org.devgateway.ocds.persistence.mongo.spring.ImportService;

/**
 * @author mpostelnicu
 */
public abstract class ReleaseRowImporter extends RowImporter<Release, String, ReleaseRepository> {

    public ReleaseRowImporter(final ReleaseRepository releaseRepository, final ImportService importService,
                              final int skipRows) {
        super(releaseRepository, importService, skipRows);
    }

    @Override
    public void importRow(final String[] row) throws ParseException {
        Release release = createReleaseFromReleaseRow(row);
        release.setDate(new Date());
        if (release.getId() == null) {
            repository.insert(release);
        } else {
            repository.save(release);
        }
    }

    public abstract Release createReleaseFromReleaseRow(String[] row) throws ParseException;
}
