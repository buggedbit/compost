from django.shortcuts import render


def cli(request):
    return render(request, 'webinterface/cli.html')
