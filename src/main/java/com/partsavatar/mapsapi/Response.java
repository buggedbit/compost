package com.partsavatar.mapsapi;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
public class Response {
    @NonNull
    private String origin;
    @NonNull
    private String destination;
    private long distance;
    private long duration;

    public Response(@NonNull String origin, @NonNull String destination, long distance, long duration) {
        if (distance < 0 || duration < 0) throw new IllegalArgumentException();
        this.origin = origin;
        this.destination = destination;
        this.distance = distance;
        this.duration = duration;
    }

    public int compareDuration(@NonNull Response r) {
        if (duration < r.duration) {
            return -1;
        } else if (duration == r.duration) {
            return 0;
        } else {
            return 1;
        }
    }

    public int compareDistance(@NonNull Response r) {
        if (distance < r.distance) {
            return -1;
        } else if (distance == r.distance) {
            return 0;
        } else {
            return 1;
        }
    }

}
