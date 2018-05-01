class ResponseWrapper:
    OBJECT_RESPONSE = 1
    ARRAY_RESPONSE = 2
    STRING_RESPONSE = 3
    NUMBER_RESPONSE = 4
    BOOLEAN_RESPONSE = 5
    CHAR_RESPONSE = 6

    ERROR_RESPONSE = -1
    EMPTY_RESPONSE = 0

    @staticmethod
    def of(data, data_type):
        return {'data': data, 'status': data_type}

    @staticmethod
    def error(error):
        return {'error': error, 'status': ResponseWrapper.ERROR_RESPONSE}

    def __init__(self):
        pass
