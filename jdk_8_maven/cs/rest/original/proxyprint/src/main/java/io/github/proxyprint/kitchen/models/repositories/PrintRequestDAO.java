package io.github.proxyprint.kitchen.models.repositories;

import io.github.proxyprint.kitchen.models.consumer.Consumer;
import io.github.proxyprint.kitchen.models.consumer.printrequest.PrintRequest;
import io.github.proxyprint.kitchen.models.consumer.printrequest.PrintRequest.Status;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by MGonc on 28/04/16.
 */

public interface PrintRequestDAO extends CrudRepository<PrintRequest, Long> {

    public List<PrintRequest> findByStatusInAndPrintshop(List<Status> statuses, PrintShop printshop);
    public PrintRequest findByIdInAndPrintshop(long id, PrintShop printshop);
    public PrintRequest findByIdInAndConsumer(long id, Consumer consumer);
    public List<PrintRequest> findByStatusInAndConsumer(List<Status> statuses, Consumer consumer);
    public List<PrintRequest> findByPrintshop(PrintShop printshop);
}
