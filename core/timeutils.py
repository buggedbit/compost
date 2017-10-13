def to_microseconds(time_delta):
    if isinstance(time_delta, time_delta):
        return time_delta.days * 86400000000 + time_delta.seconds * 1000000 + time_delta.microseconds
