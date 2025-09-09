package com.adaptilearn.controller;

import com.adaptilearn.model.StudentProfile;
import com.adaptilearn.model.Subject;
import com.adaptilearn.repository.StudentProfileRepository;
import com.adaptilearn.repository.SubjectRepository;
import com.adaptilearn.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/learn")
@CrossOrigin(origins = "*")
public class LearningController {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private com.adaptilearn.service.UserService userService;

    @GetMapping("/subjects")
    public ResponseEntity<List<Subject>> listSubjects() {
        return ResponseEntity.ok(subjectRepository.findAll());
    }

    @PostMapping("/subjects/seed")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<String> seedSubjects() {
        if (subjectRepository.count() > 0) {
            return ResponseEntity.ok("Subjects already seeded");
        }
        List<Subject> subjects = Arrays.asList(
                new Subject("Analog Circuits", "Electrical", "Basics to advanced analog electronics"),
                new Subject("Digital Electronics", "Electrical", "Logic design and digital systems"),
                new Subject("Signals and Systems", "Electrical", "Continuous and discrete-time signals"),
                new Subject("Control Systems", "Electrical", "Feedback and stability"),
                new Subject("Power Electronics", "Electrical", "Converters and drives")
        );
        subjectRepository.saveAll(subjects);
        return ResponseEntity.ok("Seeded 5 subjects");
    }

    @PostMapping("/interests")
    public ResponseEntity<StudentProfile> setInterests(@RequestBody Set<String> subjectIds, Authentication auth) {
        String username = auth.getName();
        StudentProfile profile = studentProfileRepository.findByUsername(username).orElseGet(() -> {
            StudentProfile sp = new StudentProfile();
            sp.setUsername(username);
            return sp;
        });
        profile.setInterestedSubjectIds(subjectIds);
        return ResponseEntity.ok(studentProfileRepository.save(profile));
    }

    @PostMapping("/teacher/metrics/{username}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<StudentProfile> updateMetrics(
            @PathVariable String username,
            @RequestParam(required = false) Double cgpa,
            @RequestParam(required = false, name = "arrears") Integer numArrears
    ) {
        StudentProfile profile = studentProfileRepository.findByUsername(username).orElseGet(() -> {
            StudentProfile sp = new StudentProfile();
            sp.setUsername(username);
            return sp;
        });
        if (cgpa != null) profile.setCgpa(cgpa);
        if (numArrears != null) profile.setNumArrears(numArrears);
        return ResponseEntity.ok(studentProfileRepository.save(profile));
    }

    @GetMapping("/recommendation")
    public ResponseEntity<Map<String, Object>> getRecommendation(Authentication auth) {
        String username = auth.getName();
        String difficulty = recommendationService.recommendDifficulty(username);
        Map<String, Object> resp = new HashMap<>();
        resp.put("difficulty", difficulty);
        // Content policy per difficulty
        if ("Beginner".equals(difficulty)) {
            resp.put("plan", Arrays.asList("simple_examples", "basic_videos"));
        } else if ("Intermediate".equals(difficulty)) {
            resp.put("plan", Arrays.asList("quizzes", "medium_content"));
        } else {
            resp.put("plan", Arrays.asList("challenging_problems", "research_articles"));
        }
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/students")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<StudentProfile>> getAllStudents() {
        return ResponseEntity.ok(studentProfileRepository.findAll());
    }

    // Return platform users (teachers should be able to see available users even if no StudentProfile exists)
    @GetMapping("/teacher/available-students")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<Map<String, String>>> getAvailableStudents() {
        java.util.List<com.adaptilearn.model.User> users = userService.getAllUsers();
        List<Map<String, String>> simplified = new ArrayList<>();
        for (com.adaptilearn.model.User u : users) {
            // Skip teacher accounts so teachers don't see other teachers in student lists
            if (u.getRoles() != null && u.getRoles().contains("ROLE_TEACHER")) continue;
            Map<String, String> m = new HashMap<>();
            m.put("username", u.getUsername());
            m.put("firstName", u.getFirstName());
            m.put("lastName", u.getLastName());
            m.put("email", u.getEmail());
            simplified.add(m);
        }
        return ResponseEntity.ok(simplified);
    }

    // Return profile for the currently authenticated user (for Learn page)
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getMyProfile(Authentication auth) {
        String username = auth.getName();
        Map<String, Object> resp = new HashMap<>();
        // user basic info
        userService.findByUsername(username).ifPresent(u -> {
            resp.put("username", u.getUsername());
            resp.put("firstName", u.getFirstName());
            resp.put("lastName", u.getLastName());
            resp.put("email", u.getEmail());
            resp.put("learningStyle", u.getLearningStyle());
        });
        // student profile if exists
        studentProfileRepository.findByUsername(username).ifPresent(sp -> {
            resp.put("cgpa", sp.getCgpa());
            resp.put("numArrears", sp.getNumArrears());
            resp.put("presentClass", sp.getPresentClass());
            resp.put("department", sp.getDepartment());
            resp.put("semester", sp.getSemester());
            resp.put("contactEmail", sp.getContactEmail());
            resp.put("notes", sp.getNotes());
        });
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/student/{username}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<StudentProfile> getStudentByUsername(@PathVariable String username) {
        return studentProfileRepository.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/teacher/student")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<StudentProfile> createStudent(@RequestBody StudentProfile studentData) {
        // Check if student already exists
        Optional<StudentProfile> existing = studentProfileRepository.findByUsername(studentData.getUsername());
        if (existing.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        
        StudentProfile profile = new StudentProfile();
        profile.setUsername(studentData.getUsername());
        profile.setCgpa(studentData.getCgpa());
        profile.setNumArrears(studentData.getNumArrears());
        profile.setInterestedSubjectIds(studentData.getInterestedSubjectIds());
        profile.setPresentClass(studentData.getPresentClass());
        profile.setDepartment(studentData.getDepartment());
        profile.setSemester(studentData.getSemester());
        profile.setContactEmail(studentData.getContactEmail());
        profile.setNotes(studentData.getNotes());
        
        return ResponseEntity.ok(studentProfileRepository.save(profile));
    }

    @PutMapping("/teacher/student/{username}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<StudentProfile> updateStudent(
            @PathVariable String username,
            @RequestBody StudentProfile studentData
    ) {
        StudentProfile profile = studentProfileRepository.findByUsername(username).orElseGet(() -> {
            StudentProfile sp = new StudentProfile();
            sp.setUsername(username);
            return sp;
        });
        
        if (studentData.getCgpa() != null) profile.setCgpa(studentData.getCgpa());
        if (studentData.getNumArrears() != null) profile.setNumArrears(studentData.getNumArrears());
        if (studentData.getInterestedSubjectIds() != null) profile.setInterestedSubjectIds(studentData.getInterestedSubjectIds());
        if (studentData.getPresentClass() != null) profile.setPresentClass(studentData.getPresentClass());
        if (studentData.getDepartment() != null) profile.setDepartment(studentData.getDepartment());
        if (studentData.getSemester() != null) profile.setSemester(studentData.getSemester());
        if (studentData.getContactEmail() != null) profile.setContactEmail(studentData.getContactEmail());
        if (studentData.getNotes() != null) profile.setNotes(studentData.getNotes());
        
        return ResponseEntity.ok(studentProfileRepository.save(profile));
    }

    @DeleteMapping("/teacher/student/{username}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> deleteStudent(@PathVariable String username) {
        Optional<StudentProfile> existing = studentProfileRepository.findByUsername(username);
        if (existing.isPresent()) {
            studentProfileRepository.delete(existing.get());
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
