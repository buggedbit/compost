package com.partsavatar.mapsapi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class Response {
    private String origin;
    private String destination;
    private long distance;
    private long duration;

    public int durationCompare(Response r) {
        if (duration < r.duration) {
            return -1;
        } else if (duration == r.duration) {
            return 0;
        } else {
            return 1;
        }
    }

    public int distanceCompare(Response r) {
        if (distance < r.distance) {
            return -1;
        } else if (distance == r.distance) {
            return 0;
        } else {
            return 1;
        }
    }

}
