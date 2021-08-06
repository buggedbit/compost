from django.http import HttpResponse as httpR
from django.shortcuts import render
from django.views.decorators.csrf import csrf_exempt
from errands.models import Std


def scrolling_stubs(request):
    return render(request=request, template_name='time_table/scrolling_stubs.html')


def verbose(request):
    return httpR('Verbose Time Table')
