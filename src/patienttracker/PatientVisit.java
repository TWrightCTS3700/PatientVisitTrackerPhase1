package patienttracker;

import java.time.LocalDate;

/**
 * Represents one patient visit record.
 * Each object contains the six fields.
 */
public final class PatientVisit {
    private final int visitId;
    private final String patientName;
    private final LocalDate visitDate;
    private final String reasonForVisit;
    private final String providerName;
    private final boolean followUpNeeded;

    /**
     * Creates a complete patient visit record.
     * visitId unique positive ID for the visit
     * patientName name of the patient
     * visitDate date of the visit
     * reasonForVisit reason the patient visited
     * providerName name of the medical provider
     * followUpNeeded whether the visit requires follow-up
     */
    public PatientVisit(
            int visitId,
            String patientName,
            LocalDate visitDate,
            String reasonForVisit,
            String providerName,
            boolean followUpNeeded) {
        if (visitId <= 0) {
            throw new IllegalArgumentException("Visit ID must be a positive integer.");
        }
        if (visitId > 999999) {
            throw new IllegalArgumentException("Visit ID cannot be longer than six digits.");
        }

        if (visitDate == null) {
            throw new IllegalArgumentException("Visit date is required.");
        }
        if (!visitDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Visit date must be in the future.");
        }

        this.visitId = visitId;
        this.patientName = validatePatientName(patientName);
        this.visitDate = visitDate;
        this.reasonForVisit = validateText(reasonForVisit, "Reason for visit");
        this.providerName = validateProviderName(providerName);
        this.followUpNeeded = followUpNeeded;
    }

    public int getVisitId() {
        return visitId;
    }

    public String getPatientName() {
        return patientName;
    }

    public LocalDate getVisitDate() {
        return visitDate;
    }

    public String getReasonForVisit() {
        return reasonForVisit;
    }

    public String getProviderName() {
        return providerName;
    }

    public boolean isFollowUpNeeded() {
        return followUpNeeded;
    }

    @Override
    public String toString() {
        String followUpText;
        if (followUpNeeded) {
            followUpText = "Yes";
        } else {
            followUpText = "No";
        }

        return "Visit ID: " + visitId
                + " | Patient: " + patientName
                + " | Date: " + visitDate
                + " | Reason: " + reasonForVisit
                + " | Provider: " + providerName
                + " | Follow-Up: " + followUpText;
    }

    private String validateText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }

        String cleaned = value.trim();
        if (cleaned.contains("|")) {
            throw new IllegalArgumentException(fieldName + " cannot contain the | character.");
        }
        return cleaned;
    }

    private String validatePatientName(String value) {
        String cleaned = validateText(value, "Patient name");

        if (cleaned.length() > 50) {
            throw new IllegalArgumentException(
                    "Patient name cannot be longer than 50 characters.");
        }

        String[] nameParts = cleaned.split("\\s+");
        if (nameParts.length < 2) {
            throw new IllegalArgumentException(
                    "Patient name must include a first and last name.");
        }

        for (int index = 0; index < cleaned.length(); index++) {
            char character = cleaned.charAt(index);
            boolean allowed = Character.isLetter(character)
                    || character == ' '
                    || character == '-'
                    || character == '\''
                    || character == '.';

            if (!allowed) {
                throw new IllegalArgumentException(
                        "Patient name contains an invalid character.");
            }
        }
        return cleaned;
    }

    private String validateProviderName(String value) {
        String cleaned = validateText(value, "Provider name");

        if (cleaned.length() > 50) {
            throw new IllegalArgumentException(
                    "Provider name cannot be longer than 50 characters.");
        }
        return cleaned;
    }
}
