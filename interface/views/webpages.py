from django.shortcuts import render
from interface.views.sessionapis import is_loggedin


def index(request):
    if is_loggedin(request.session):
        context = {'isLoggedIn': True}
    else:
        context = {'isLoggedIn': False}
    return render(request, 'interface/index/index.html', context)


def goal_glance(request):
    return render(request, 'interface/goal/glance.html')


def scratch(request):
    return render(request, 'interface/scratch.html')
