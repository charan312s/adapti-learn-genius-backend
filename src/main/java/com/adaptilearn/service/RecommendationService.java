package com.adaptilearn.service;

import com.adaptilearn.model.StudentProfile;
import com.adaptilearn.repository.StudentProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecommendationService {

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    public String recommendDifficulty(String username) {
        StudentProfile profile = studentProfileRepository.findByUsername(username).orElse(null);
        if (profile == null) {
            return "Beginner";
        }
        Double cgpa = profile.getCgpa();
        Integer arrears = profile.getNumArrears();

        if (cgpa == null) cgpa = 0.0;
        if (arrears == null) arrears = 0;

        // Simple heuristic per request
        if (cgpa >= 8.0 && arrears == 0) {
            return "Advanced";
        } else if (cgpa >= 6.5 && arrears <= 2) {
            return "Intermediate";
        } else {
            return "Beginner";
        }
    }
}


