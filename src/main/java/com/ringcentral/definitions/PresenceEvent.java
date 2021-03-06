package com.ringcentral.definitions;

public class PresenceEvent {
    // Internal identifier of an extension. Optional parameter
    public String extensionId;
    // Telephony presence status. Returned if telephony status is changed. See Telephony Status Values
    public String telephonyStatus;
    // Type of call termination. Supported for calls in 'NoCall' status. If the returned termination type is 'Intermediate' it means the call is not actually ended, the connection is established on one of the devices
    public String terminationType;
    // Order number of a notification to state the chronology
    public Long sequence;
    // Aggregated presence status, calculated from a number of sources
    public String presenceStatus;
    // User-defined presence status (as previously published by the user)
    public String userStatus;
    // Extended DnD (Do not Disturb) status
    public String dndStatus;
    // If 'True' enables other extensions to see the extension presence status
    public Boolean allowSeeMyPresence;
    // If 'True' enables to ring extension phone, if any user monitored by this extension is ringing
    public Boolean ringOnMonitoredCall;
    // If 'True' enables the extension user to pick up a monitored line on hold
    public Boolean pickUpCallsOnHold;

    public PresenceEvent extensionId(String extensionId) {
        this.extensionId = extensionId;
        return this;
    }

    public PresenceEvent telephonyStatus(String telephonyStatus) {
        this.telephonyStatus = telephonyStatus;
        return this;
    }

    public PresenceEvent terminationType(String terminationType) {
        this.terminationType = terminationType;
        return this;
    }

    public PresenceEvent sequence(Long sequence) {
        this.sequence = sequence;
        return this;
    }

    public PresenceEvent presenceStatus(String presenceStatus) {
        this.presenceStatus = presenceStatus;
        return this;
    }

    public PresenceEvent userStatus(String userStatus) {
        this.userStatus = userStatus;
        return this;
    }

    public PresenceEvent dndStatus(String dndStatus) {
        this.dndStatus = dndStatus;
        return this;
    }

    public PresenceEvent allowSeeMyPresence(Boolean allowSeeMyPresence) {
        this.allowSeeMyPresence = allowSeeMyPresence;
        return this;
    }

    public PresenceEvent ringOnMonitoredCall(Boolean ringOnMonitoredCall) {
        this.ringOnMonitoredCall = ringOnMonitoredCall;
        return this;
    }

    public PresenceEvent pickUpCallsOnHold(Boolean pickUpCallsOnHold) {
        this.pickUpCallsOnHold = pickUpCallsOnHold;
        return this;
    }
}
