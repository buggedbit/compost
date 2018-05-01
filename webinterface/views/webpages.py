from django.shortcuts import render
from webinterface.views.sessionapis import is_loggedin


def index(request):
    if is_loggedin(request.session):
        context = {'isLoggedIn': True}
    else:
        context = {'isLoggedIn': False}
    return render(request, 'webinterface/index/index.html', context)


def goal_glance(request):
    return render(request, 'webinterface/goal/glance.html')


def scratch(request):
    return render(request, 'webinterface/scratch.html')
