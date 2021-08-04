from django.utils.datastructures import MultiValueDictKeyError
from interface.views.responsewrapper import ResponseWrapper
from django.views.decorators.csrf import csrf_exempt
from django.http import HttpResponse
import json

PASSWORD = 'airturtle'
SESSION_KEY = 'loggedIn'
SESSION_VALUE = 'yes'


def is_session_active(session):
    try:
        return session[SESSION_KEY] == SESSION_VALUE
    except KeyError:
        return False


@csrf_exempt
def login(request):
    if request.method == 'POST':
        try:
          password = request.POST['password']
          if password == PASSWORD:
              request.session[SESSION_KEY] = SESSION_VALUE
              return HttpResponse(json.dumps(ResponseWrapper.of(0, ResponseWrapper.NUMBER_RESPONSE)))
          else:
              return HttpResponse(json.dumps(ResponseWrapper.error('Incorrect Password')))
        except (ValueError, TypeError, MultiValueDictKeyError):
            return HttpResponse(json.dumps(ResponseWrapper.error('Improper data')))
    else:
        return HttpResponse(json.dumps(ResponseWrapper.error('Invalid request')))


@csrf_exempt
def logout(request):
    request.session.clear()
    return HttpResponse(json.dumps(ResponseWrapper.of(0, ResponseWrapper.NUMBER_RESPONSE)))
