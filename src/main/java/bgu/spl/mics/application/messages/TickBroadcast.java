package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {

    private Integer currentTime;
    private Integer duration;

    public TickBroadcast(Integer currentTime , Integer duration)
    {
        this.currentTime = currentTime;
        this.duration = duration;
    }

    public Integer getCurrentTime() {
        return currentTime;
    }

    public Integer getDuration() {
        return duration;
    }

}
