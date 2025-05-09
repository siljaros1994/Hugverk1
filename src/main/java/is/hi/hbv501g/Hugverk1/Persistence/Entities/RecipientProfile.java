package is.hi.hbv501g.Hugverk1.Persistence.Entities;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "RecipientProfile")
public class RecipientProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipient_profile_id", nullable = false, unique = true)
    private Long recipientProfileId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    private MyAppUsers user;

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
    @CollectionTable(name = "recipient_medical_history", joinColumns = @JoinColumn(name = "recipient_profile_id"))
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

    @Column(name = "recipient_type")
    private String recipientType;

    public Long getRecipientProfileId() {
        return recipientProfileId;
    }

    public void setRecipientProfileId(Long recipientProfileId) {
        this.recipientProfileId = recipientProfileId;
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

    public String getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(String recipientType) {
        this.recipientType = recipientType;
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
}
