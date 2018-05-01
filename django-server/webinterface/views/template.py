from django.shortcuts import render


def index(request):
    return render(request, 'webinterface/index/index.html')


def goal_glance(request):
    return render(request, 'webinterface/goal/glance.html')


def scratch(request):
    return render(request, 'webinterface/scratch.html')
