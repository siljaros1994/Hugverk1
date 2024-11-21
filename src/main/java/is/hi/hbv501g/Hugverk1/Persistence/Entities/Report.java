package is.hi.hbv501g.Hugverk1.Persistence.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.*;


//This file is a report entity in the database
//The report consists of report_id, user_id, incident_description and submit_report
//The ids from the database are user_id such as donor_id and recipient_id
//This entity maps to the report table in the database
//The id is the primary key, and the entity uses JPA annotations for ORM (Object-Relational Mapping).

@Entity
@Table(name ="reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //Each report has a unique ID


    @Column(name = "reporter_id", nullable = false)
    private Long reporterId; //reportId is the ID of the user making the report (either donor or recipient)


    @Column(name = "reported_id", nullable = false)
    private Long reportedId; //reportedId is the ID of the user being reported (either a donor or recipient)

    @Column
    private Long donorId; //Optional: Donor involved in the report

    @Column
    private Long recipientId; //Optional: Recipient involved in the report


    @Column(name ="incident_description" ,nullable = false, length = 1000)
    private String incidentDescription; //Description of the incident

    @Column(name = "submitted", nullable=false)
    private boolean submitted = false;

    public Report() {

    }
    public Report(Long reporterId, Long reportedId, String incidentDescription, Long donorId, Long recipientId) {
        this.reporterId = reporterId;
        this.reportedId = reportedId;
        this.incidentDescription = incidentDescription;
        this.donorId = donorId;
        this.recipientId = recipientId;
    }

    //Getters and Setters

    //public Long getReportId() {return reportId;}

    //public void setReportId(Long reportId) {this.reportId = reportId;}

    public Long getId() { return id; }

    public void setId(Long id) {this.id = id;}

    public Long getReporterId() {return reporterId; }

    public void setReporterId(Long reporterId) { this.reporterId = reporterId;}


    public Long getReportedId() { return reportedId; }

    public void setReportedId(Long reportedId) { this.reportedId = reportedId;}


    public String getincidentDescription() { return incidentDescription; }

    public void setincidentDescription(String incidentDescription) {this.incidentDescription = incidentDescription;}

    public Long getDonorId() {return donorId;}

    public void setDonorId(Long donorId) {this.donorId = donorId;}

    public Long getRecipientId() {return recipientId;}

    public void setRecipientId(Long recipientId) {this.recipientId = recipientId;}

    public boolean isSubmitted() {return submitted;}
    public void setSubmitted(boolean submit) {this.submitted = submitted;}

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



