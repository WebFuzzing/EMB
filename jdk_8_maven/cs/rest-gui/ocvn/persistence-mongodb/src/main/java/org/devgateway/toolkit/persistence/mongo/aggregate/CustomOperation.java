package org.devgateway.toolkit.persistence.mongo.aggregate;

import com.mongodb.DBObject;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;

public class CustomOperation implements AggregationOperation {
    private DBObject operation;

    public CustomOperation(final DBObject operation) {
        this.operation = operation;
    }

    @Override
    public DBObject toDBObject(final AggregationOperationContext context) {
        return context.getMappedObject(operation);
    }
}