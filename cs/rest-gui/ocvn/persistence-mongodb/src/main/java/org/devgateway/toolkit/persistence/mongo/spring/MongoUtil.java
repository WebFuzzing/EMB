package org.devgateway.toolkit.persistence.mongo.spring;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Created by mpostelnicu on 11-May-17.
 */
public final class MongoUtil {

    private MongoUtil() {

    }

    public static final int BATCH_SIZE = 5000;


    public static <T, ID extends Serializable> void processRepositoryItemsPaginated(MongoRepository<T, ID> repository,
                                                                                    Consumer<? super T> action,
                                                                                    Consumer<String> logMessage
                                                                                    ) {
        int pageNumber = 0;
        AtomicInteger processedCount = new AtomicInteger(0);
        Page<T> page;
        do {
            page = repository.findAll(new PageRequest(pageNumber++, BATCH_SIZE));
            page.getContent().forEach(action);
            processedCount.addAndGet(page.getNumberOfElements());
            logMessage.accept("Processed " + processedCount.get() + " items");
        } while (!page.isLast());
    }
}
