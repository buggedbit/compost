from django.shortcuts import render
from interface.views.sessionapis import is_session_active


def index(request):
    if is_session_active(request.session):
        context = {'isSessionActive': True}
    else:
        context = {'isSessionActive': False}
    return render(request, 'interface/index/index.html', context)


def goal_glance(request):
    return render(request, 'interface/goal/glance.html')


def scratch(request):
    return render(request, 'interface/scratch.html')
