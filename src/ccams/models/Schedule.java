package ccams.models;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class Schedule {
    private int scheduleId;
    private int staffId;
    private String dayOfWeek;        // MONDAY, TUESDAY, etc.
    private Time startTime;
    private Time endTime;
    private boolean isRecurring;     // true = weekly recurring, false = specific date
    private Date specificDate;       // specific date if not recurring
    private boolean isAvailable;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Display fields (from joins)
    private String staffName;
    private String staffProfession;

    public Schedule() {
        this.isRecurring = true;
        this.isAvailable = true;
    }

    // ============================================================
    // Getters and Setters
    // ============================================================
    
    public int getScheduleId() { return scheduleId; }
    public void setScheduleId(int scheduleId) { this.scheduleId = scheduleId; }

    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public Time getStartTime() { return startTime; }
    public void setStartTime(Time startTime) { this.startTime = startTime; }

    public Time getEndTime() { return endTime; }
    public void setEndTime(Time endTime) { this.endTime = endTime; }

    public boolean isRecurring() { return isRecurring; }
    public void setRecurring(boolean recurring) { isRecurring = recurring; }

    public Date getSpecificDate() { return specificDate; }
    public void setSpecificDate(Date specificDate) { this.specificDate = specificDate; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }

    public String getStaffProfession() { return staffProfession; }
    public void setStaffProfession(String staffProfession) { this.staffProfession = staffProfession; }

    // ============================================================
    // Helper methods
    // ============================================================
    
    public String getTimeRange() {
        return startTime + " - " + endTime;
    }
    
    public String getDisplayDate() {
        if (isRecurring) {
            return dayOfWeek + " (Recurring)";
        } else {
            return specificDate != null ? specificDate.toString() : "N/A";
        }
    }
    
    @Override
    public String toString() {
        if (isRecurring) {
            return dayOfWeek + " " + getTimeRange();
        } else {
            return specificDate + " " + getTimeRange();
        }
    }
}