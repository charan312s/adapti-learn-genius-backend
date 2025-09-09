package com.adaptilearn.repository;

import com.adaptilearn.model.StudentProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentProfileRepository extends MongoRepository<StudentProfile, String> {
    Optional<StudentProfile> findByUsername(String username);
}


