package com.github.gustavoflor.finduserservice.infrastructure.persistence;

import com.github.gustavoflor.finduserservice.core.User;
import com.github.gustavoflor.finduserservice.infrastructure.shared.Pageable;
import com.github.gustavoflor.finduserservice.infrastructure.shared.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final MongoTemplate mongoTemplate;

    public boolean isEmpty() {
        Query query = Query.query(Criteria.where("_id").ne(null));
        return mongoTemplate.count(query, User.class) == 0;
    }

    public void deleteAll() {
        mongoTemplate.remove(User.class);
    }

    public void insert(List<User> users) {
        mongoTemplate.insert(users, User.class);
    }

    public Page<User> findAll(Pageable pageable, String sortBy) {
        List<AggregationOperation> pipeline = new ArrayList<>();
        applyFilter(pipeline, pageable);
        applySortable(pipeline, sortBy);
        applyPagination(pipeline, pageable);
        return Page.of(aggregate(pipeline), pageable);
    }

    private void applyFilter(List<AggregationOperation> pipeline, Pageable pageable) {
        pipeline.add(match(new TextCriteria().matching(pageable.getQuery())));
    }

    private void applySortable(List<AggregationOperation> pipeline, String sortBy) {
        pipeline.add(sort(Sort.by(sortBy)));
    }

    private void applyPagination(List<AggregationOperation> pipeline, Pageable pageable) {
        long elementsToSkip = pageable.getFrom() * pageable.getSize();
        pipeline.add(skip(elementsToSkip));
        pipeline.add(limit(pageable.getSize()));
    }

    private List<User> aggregate(List<AggregationOperation> pipeline) {
        Aggregation aggregation = newAggregation(pipeline);
        return mongoTemplate.aggregate(aggregation, User.class, User.class).getMappedResults();
    }

}
