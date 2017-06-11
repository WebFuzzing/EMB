package org.devgateway.ocds.persistence.mongo.reader;

import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.repository.ReleaseRepository;
import org.devgateway.ocds.persistence.mongo.spring.ImportService;

import java.text.ParseException;

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
        if (release.getId() == null) {
            repository.insert(release);
        } else {
            repository.save(release);
        }
    }

    public abstract Release createReleaseFromReleaseRow(String[] row) throws ParseException;
}
