package model;

/**
 * Data Model representing a Student Attendance record.
 */
public class StudentAttendance {
    private int attendanceId;
    private String studentName;
    private String courseCode;
    private int attendedClasses;
    private int totalClasses;
    private String status;

    public StudentAttendance() {
        this.totalClasses = 50; // Default class count
    }

    public StudentAttendance(int attendanceId, String studentName, String courseCode, int attendedClasses, int totalClasses, String status) {
        this.attendanceId = attendanceId;
        this.studentName = studentName;
        this.courseCode = courseCode;
        this.attendedClasses = attendedClasses;
        this.totalClasses = totalClasses > 0 ? totalClasses : 50;
        this.status = status;
    }

    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public int getAttendedClasses() {
        return attendedClasses;
    }

    public void setAttendedClasses(int attendedClasses) {
        this.attendedClasses = attendedClasses;
    }

    public int getTotalClasses() {
        return totalClasses;
    }

    public void setTotalClasses(int totalClasses) {
        this.totalClasses = totalClasses > 0 ? totalClasses : 50;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getPercentage() {
        if (totalClasses <= 0) return 0.0;
        return ((double) attendedClasses / totalClasses) * 100.0;
    }

    /**
     * Determines eligibility based on the 75% attendance threshold.
     */
    public static String determineStatus(int attended, int total) {
        if (total <= 0) return "Eligible";
        double pct = ((double) attended / total) * 100.0;
        return pct >= 75.0 ? "Eligible" : "Attendance Shortage";
    }

    @Override
    public String toString() {
        return "StudentAttendance{" +
                "id=" + attendanceId +
                ", name='" + studentName + '\'' +
                ", course='" + courseCode + '\'' +
                ", attended=" + attendedClasses + "/" + totalClasses +
                ", status='" + status + '\'' +
                '}';
    }
}
