package is.hi.hbv501g.Hugverk1.Persistence.forms;



//This file is where a data transfer object (DTO) captures input data for creating a report
//this includes report_name, accused_user and incident_description

public class ReportForm {
    private Long reporterId;
    private Long reportedId;
    private Long donorId;
    private Long recipientId;
    private String incidentDescription;

    public ReportForm() {
    }

    public ReportForm(Long reporterId, Long reportedId, String incidentDescription) {
        this.reporterId = reporterId;
        this.reportedId = reportedId;
        this.incidentDescription = incidentDescription;
    }

    //Getters and Setters
    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public Long getReportedId() {
        return reportedId;
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

    public String getIncidentDescription() {
        return incidentDescription;
    }

    public void setIncidentDescription(String incidentDescription) {
        this.incidentDescription = incidentDescription;
    }

}

