package is.hi.hbv501g.Hugverk1.dto;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.RecipientProfile;

public class RecipientProfileConverter {
    public static RecipientProfileDTO convertToDTO(RecipientProfile profile) {
        RecipientProfileDTO dto = new RecipientProfileDTO();
        dto.setRecipientProfileId(profile.getRecipientProfileId());
        dto.setHeight(profile.getHeight());
        dto.setWeight(profile.getWeight());
        dto.setAge(profile.getAge());
        dto.setEyeColor(profile.getEyeColor());
        dto.setHairColor(profile.getHairColor());
        dto.setEducationLevel(profile.getEducationLevel());
        dto.setMedicalHistory(profile.getMedicalHistory());
        dto.setRace(profile.getRace());
        dto.setEthnicity(profile.getEthnicity());
        dto.setBloodType(profile.getBloodType());
        dto.setGetToKnow(profile.getGetToKnow());
        dto.setTraits(profile.getTraits());
        dto.setImagePath(profile.getImagePath());
        dto.setRecipientType(profile.getRecipientType());

        if (profile.getUser() != null) {
            dto.setUserId(profile.getUser().getId());
            dto.setUsername(profile.getUser().getUsername());
            dto.setEmail(profile.getUser().getEmail());
        }
        return dto;
    }
}
