from django.shortcuts import render


def cli(request):
    return render(request, 'webinterface/cli.html')


def editor(request):
    return render(request, 'webinterface/editor.html')
