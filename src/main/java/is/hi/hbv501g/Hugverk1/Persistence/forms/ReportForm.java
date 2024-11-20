package is.hi.hbv501g.Hugverk1.Persistence.forms;



//This file is where a data transfer object (DTO) captures input data for creating a report
//this includes report_name, accused_user and incident_description

public class ReportForm {
    private Long reportname;
    private Long donorId;
    private Long recipientId;
    private Long accuseduser;
    private String incidentDescription;

    public ReportForm() {}

    public ReportForm(Long reportname) { this.reportname = reportname; }

    //Getters and Setters
    public String getincidentDescription() {return incidentDescription;}

    public void setincidentDescription(String incidentDescription) {this.incidentDescription = incidentDescription;}

    public Long getReportname() {return reportname;}

    public void setReportname(Long reportname) {this.reportname = reportname;}

    public Long getAccuseduser() {return accuseduser;}

    public void setAccuseduser(Long accuseduser) {this.accuseduser =accuseduser; }


    //Getters and Setters
    //public Long getDonorId() {return donorId; }

    //public void setDonorId(Long donorId) {this.donorId = donorId; }

    //public Long getRecipientId() { return  recipientId; }

    //public void setRecipientId(Long recipientId) { this.recipientId = recipientId; }


    //public Object getIncidentDescription() {
    //    return null;
    //}
}
