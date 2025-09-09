package com.adaptilearn.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "student_profiles")
public class StudentProfile {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    private Double cgpa;

    private Integer numArrears;

    private Set<String> interestedSubjectIds = new HashSet<>();

    private String presentClass;

    private String department;

    private Integer semester;

    private String contactEmail;

    private String notes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Double getCgpa() {
        return cgpa;
    }

    public void setCgpa(Double cgpa) {
        this.cgpa = cgpa;
    }

    public Integer getNumArrears() {
        return numArrears;
    }

    public void setNumArrears(Integer numArrears) {
        this.numArrears = numArrears;
    }

    public Set<String> getInterestedSubjectIds() {
        return interestedSubjectIds;
    }

    public void setInterestedSubjectIds(Set<String> interestedSubjectIds) {
        this.interestedSubjectIds = interestedSubjectIds;
    }

    public String getPresentClass() {
        return presentClass;
    }

    public void setPresentClass(String presentClass) {
        this.presentClass = presentClass;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
