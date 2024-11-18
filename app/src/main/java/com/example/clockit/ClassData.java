package com.example.clockit;

public class ClassData {
    private String classId;
    private String className;
    private String classCode;
    private String classDescription;
    private String teacherName;
    private String startTime;
    private String endTime;
    private String roomNumber;
    private String adminId; // New field to identify which admin created this class

    public ClassData() {
        // Default constructor required for calls to DataSnapshot.getValue(ClassData.class)
    }

    public ClassData(String classId, String className, String classCode, String classDescription, String teacherName, String startTime, String endTime, String roomNumber, String adminId) {
        this.classId = classId;
        this.className = className;
        this.classCode = classCode;
        this.classDescription = classDescription;
        this.teacherName = teacherName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomNumber = roomNumber;
        this.adminId = adminId;
    }

    // Getters and Setters for each field
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }
}

