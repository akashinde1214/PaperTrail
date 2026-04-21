package com.papertrail.model;

public enum DocType {
    AADHAAR(3650),
    PASSPORT(3650),
    PAN(36500),
    VEHICLE_INSURANCE(365),
    RATION_CARD(1825),
    DRIVING_LICENSE(3650),
    VOTER_ID(36500),
    VEHICLE_PUC(180),
    HEALTH_INSURANCE(365),
    OTHER(365);

    private final int defaultRenewalCycleDays;

    DocType(int defaultRenewalCycleDays) {
        this.defaultRenewalCycleDays = defaultRenewalCycleDays;
    }

    public int getDefaultRenewalCycleDays() {
        return defaultRenewalCycleDays;
    }
}
