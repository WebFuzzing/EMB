/**
 *
 */
package org.devgateway.ocds.persistence.mongo.spring;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.devgateway.ocds.persistence.mongo.Record;
import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.repository.main.RecordRepository;
import org.devgateway.ocds.persistence.mongo.repository.main.ReleaseRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author mpostelnicu
 *
 *         AOP Monitor that saves new {@link Release}S in {@link Record}S for
 *         archiving purposes.
 *         http://standard.open-contracting.org/latest/en/getting_started/releases_and_records/#records
 *
 *         Whenever a new release is identified, the {@link Release} save action
 *         will trigger a {@link Release} archiving process as well.
 *
 *         This will - search for a {@link Record} with the
 *         {@link Record#getOcid()} - if none can be found, create a new record
 *         - if existing record found, append to the record -
 *
 *
 */
//@Aspect
//@Component
@Deprecated
public class ReleaseRecordMonitor {

    protected static Logger logger = Logger.getLogger(ReleaseRecordMonitor.class);

    @Autowired
    private RecordRepository recordRepository;

    /**
     * Programmatic invocation of {@link #saveRecordForRelease(Release)} for a
     * list of releases
     *
     * @param releases
     * @return
     */
    public List<Record> saveRecordsForReleases(final Iterable<Release> releases) {
        return StreamSupport.stream(releases.spliterator(), false).map(this::saveRecordForRelease)
                .collect(Collectors.toList());
    }

    /**
     * Method invoked by AOP whenever a {@link Release} is saved through
     * {@link ReleaseRepository} using the .insert method. This should also
     * catch the {@link ReleaseRepository#save(Iterable)} since it internally
     * invokes insert
     *
     * @param jp
     * @param release
     * @return
     */
    public Record saveRecordForRelease(final JoinPoint jp, final Release release) {
        logger.debug("Release record archival triggered by " + jp);
        return saveRecordForRelease(release);
    }

    /**
     * This method queries the {@link RecordRepository} to find a {@link Record}
     * for the given {@link Release#getOcid()}. If it finds none, it creates a
     * new record and adds this release to {@link Record#getReleases()}. If it
     * finds a record, it just appends this release to
     * {@link Record#getReleases()}.
     *
     * This method is intended to be invoked internally by
     * {@link #saveRecordForRelease(JoinPoint, Release)} which is an AOP hook
     * invoked whenever any {@link Release} is inserted anywhere in the
     * application.
     *
     * @see #saveRecordForRelease(JoinPoint, Release)
     * @param release
     * @return the saved {@link Record}
     */
    protected Record saveRecordForRelease(final Release release) {
        Record record = recordRepository.findByOcid(release.getOcid());
        if (record == null) {
            record = new Record();
            record.setOcid(release.getOcid());
        }
        record.getReleases().add(release);
        return recordRepository.save(record);
    }

}
