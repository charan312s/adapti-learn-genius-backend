package com.adaptilearn.repository;

import com.adaptilearn.model.Subject;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends MongoRepository<Subject, String> {
    List<Subject> findByCategoryIn(List<String> categories);
}


