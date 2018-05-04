def to_microseconds(time_delta):
    if isinstance(time_delta, time_delta):
        return time_delta.days * 86400000000 + time_delta.seconds * 1000000 + time_delta.microseconds


class DeadlineUtils:
    @staticmethod
    def is_greater(d1, d2):
        if d1 is None and d2 is not None:
            return True
        if d1 is not None and d2 is not None and d1 > d2:
            return True
        return False

    @staticmethod
    def is_equal(d1, d2):
        if d1 is None and d2 is None:
            return True
        if d1 is not None and d2 is not None and d1 == d2:
            return True
        return False

    @staticmethod
    def is_lesser(d1, d2):
        return DeadlineUtils.is_greater(d2, d1)
