package com.example.clockit;

public class ClassData {
    private String classId;
    private String className;
    private String classCode;
    private String classDescription;
    private String teacherName;

    // Default constructor required for Firebase
    public ClassData() {
    }

    // Parameterized constructor
    public ClassData(String classId, String className, String classCode, String classDescription, String teacherName) {
        this.classId = classId;
        this.className = className;
        this.classCode = classCode;
        this.classDescription = classDescription;
        this.teacherName = teacherName;
    }

    // Getters and setters
    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getClassDescription() {
        return classDescription;
    }

    public void setClassDescription(String classDescription) {
        this.classDescription = classDescription;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
}
