package com.lxy.recovery;

import lombok.Data;

@Data
public class RecoveryState {

    private Integer continuationAttempts = 0;

    private Integer compactAttempts = 0;

    private Integer transportAttempts = 0;

    public void incrementContinuationAttempts() {
        this.continuationAttempts++;
    }

    public void incrementCompactAttempts() {
        this.compactAttempts++;
    }

    public void incrementTransportAttempts() {
        this.transportAttempts++;
    }

    public void refresh(){
        this.continuationAttempts = 0;
        this.compactAttempts = 0;
        this.transportAttempts = 0;
    }

}
