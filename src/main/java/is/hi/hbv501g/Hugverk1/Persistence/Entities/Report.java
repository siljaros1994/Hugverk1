package is.hi.hbv501g.Hugverk1.Persistence.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.*;


//This file is a report entity in the database
//The report consists of report_name, accused_user, incident_description and submit_report
//The ids from the database are user_id such as donor_id and recipient_id
//This entity maps to the report table in the database
//The id is the primary key, and the entity uses JPA annotations for ORM (Object-Relational Mapping).

@Entity
//@Table(name ="reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //Each report has a unique ID


    @Column(nullable = false)
    private Long reportName; //reportName is the ID of the user making the report (either donor or recipient)

    @Column(nullable = false)
    private String reportNameType; //reportName can be a recipient or a donor

    @Column(nullable = false)
    private Long accusedUser; //accusedUser is the ID of the user being reported (either a donor or recipient)

    @Column(nullable = false)
    private String accusedUserType; //accusedUser can be a recipient or a donor

    @Column( nullable = false, length = 1000)
    private String incidentDescription; //Description of the incident

    @Column
    private Long donorId; //Optional: Donor involved in the report

    @Column
    private Long recipientId; //Optional: Recipient involved in the report

    public Report() {

    }
    public Report(Long reportName, Long accusedUser,String accusedUserType, String incidentDescription, Long donorId, Long recipientId) {
        this.reportName = reportName;
        this.reportNameType = reportNameType;
        this.accusedUser = accusedUser;
        this.accusedUserType = accusedUserType;
        this.incidentDescription = incidentDescription;
        this.donorId = donorId;
        this.recipientId = recipientId;
    }

    //Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReportName() {
        return reportName;
    }

    public void setReportName(Long reportName) {
        this.reportName = reportName;
    }

    public String getreportNameType() {
        return reportNameType;
    }

    public void setReportNameType(String reportNameType) {
        this.reportNameType = reportNameType;
    }

    public Long getAccusedUser() {
        return accusedUser;
    }

    public void setAccusedUser(Long accusedUser) {
        this.accusedUser = accusedUser;
    }

    public String getAccusedUserType() {
        return accusedUserType;
    }

    public void setAccusedUserType(String accusedUserType) {
        this.accusedUserType =accusedUserType;
    }

    public String getIncidentDescription() {
        return incidentDescription;
    }

    public void setIncidentDescription(String incidentDescription) {
    this.incidentDescription = incidentDescription;
    }

    public Long getDonorId() {
        return donorId;
    }

    public void setDonorId(Long donorId) {
        this.donorId = donorId;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    //@Override
    //public String toString() {
      //  return "Report{" +
      //          "id" + id +
      //          ", reportName=" + reportName +
      //          ", accusedUSer=" + accusedUser +
      //          ",incidentDescription=" + incidentDescription + '\'' +
      //          "}";
    //}
}



