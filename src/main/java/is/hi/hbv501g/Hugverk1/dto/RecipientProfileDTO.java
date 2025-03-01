package is.hi.hbv501g.Hugverk1.dto;

import java.util.Set;

public class RecipientProfileDTO {
    private Long recipientProfileId;
    private Double height;
    private Double weight;
    private Integer age;
    private String eyeColor;
    private String hairColor;
    private String educationLevel;
    private Set<String> medicalHistory;
    private String race;
    private String ethnicity;
    private String bloodType;
    private String getToKnow;
    private String traits;
    private String imagePath;
    private String recipientType;

    private Long userId;
    private String username;
    private String email;

    // Getters and Setters
    public Long getRecipientProfileId() {
        return recipientProfileId;
    }
    public void setRecipientProfileId(Long recipientProfileId) {
        this.recipientProfileId = recipientProfileId;
    }
    public Double getHeight() {
        return height;
    }
    public void setHeight(Double height) {
        this.height = height;
    }
    public Double getWeight() {
        return weight;
    }
    public void setWeight(Double weight) {
        this.weight = weight;
    }
    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
    public String getEyeColor() {
        return eyeColor;
    }
    public void setEyeColor(String eyeColor) {
        this.eyeColor = eyeColor;
    }
    public String getHairColor() {
        return hairColor;
    }
    public void setHairColor(String hairColor) {
        this.hairColor = hairColor;
    }
    public String getEducationLevel() {
        return educationLevel;
    }
    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }
    public Set<String> getMedicalHistory() {
        return medicalHistory;
    }
    public void setMedicalHistory(Set<String> medicalHistory) {
        this.medicalHistory = medicalHistory;
    }
    public String getRace() {
        return race;
    }
    public void setRace(String race) {
        this.race = race;
    }
    public String getEthnicity() {
        return ethnicity;
    }
    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }
    public String getBloodType() {
        return bloodType;
    }
    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }
    public String getGetToKnow() {
        return getToKnow;
    }
    public void setGetToKnow(String getToKnow) {
        this.getToKnow = getToKnow;
    }
    public String getTraits() {
        return traits;
    }
    public void setTraits(String traits) {
        this.traits = traits;
    }
    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    public String getRecipientType() {
        return recipientType;
    }
    public void setRecipientType(String recipientType) {
        this.recipientType = recipientType;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
