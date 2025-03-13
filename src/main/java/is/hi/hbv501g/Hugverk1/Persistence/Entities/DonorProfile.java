package is.hi.hbv501g.Hugverk1.Persistence.Entities;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "DonorProfile")
public class DonorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "donor_profile_id", nullable = false, unique = true)
    private Long donorProfileId;

    // Links each profile to a unique donor, (The user_id in the DonorProfile table should always match the donorId in MyAppUsers table for that user.)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    private MyAppUsers user;

    @Column(name = "donor_type")
    private String donorType;

    @Column(name = "height")
    private Double height;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "age")
    private Integer age;

    @Column(name = "eye_color")
    private String eyeColor;

    @Column(name = "hair_color")
    private String hairColor;

    @Column(name = "education_level")
    private String educationLevel;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "donor_medical_history", joinColumns = @JoinColumn(name = "donor_profile_id"))
    @Column(name = "history")
    private Set<String> medicalHistory = new HashSet<>();

    @Column(name = "race")
    private String race;

    @Column(name = "ethnicity")
    private String ethnicity;

    @Column(name = "blood_type")
    private String bloodType;

    @Column(name = "get_to_know", length = 1000)
    private String getToKnow;

    @Column(name = "traits", nullable = true)
    private String traits = "";

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "donation_limit", nullable = false)
    private int donationLimit = 5;

    @Column(name = "donations_completed", nullable = false)
    private int donationsCompleted = 0;

    @Column(name = "location")
    private String location;


    // Getters and Setters.

    public Long getDonorProfileId() {
        return donorProfileId;
    }

    public void setDonorProfileId(Long donorProfileId) {
        this.donorProfileId = donorProfileId;
    }

    public MyAppUsers getUser() {
        return user;
    }

    public void setUser(MyAppUsers user) {
        this.user = user;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDonorType() {
        return donorType;
    }

    public void setDonorType(String donorType) {
        this.donorType = donorType;
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

    public int getDonationLimit() {
        return donationLimit;
    }
    public void setDonationLimit(int donationLimit) {
       this.donationLimit = donationLimit;
    }
    public int getDonationsCompleted() { return donationsCompleted; }

    public void setDonationsCompleted(int donationsCompleted) { this.donationsCompleted = donationsCompleted; }

    public void incrementDonationsCompleted() {
       this.donationsCompleted++;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}