import json

from django.core.exceptions import ObjectDoesNotExist
from django.http import HttpResponse
from django.shortcuts import render
from paper.models import Book, Page


def cli(request):
    return render(request, 'webinterface/cli.html')


def editor(request):
    try:
        book_name = request.GET['book_name']
        page_name = request.GET['page_name']
        mode = request.GET['mode']
        # Get book
        if book_name == '':
            book = None
        else:
            book = Book.objects.get(name=book_name)

        existing_page = Page.objects.get(name=page_name, book=book)

        return render(request, 'webinterface/editor.html', {'page': existing_page, 'mode': mode})
    except ObjectDoesNotExist:
        return HttpResponse(json.dumps({'status': -1, 'message': 'Inconsistent data'}))
    except (ValueError, TypeError):
        return HttpResponse(json.dumps({'status': -1, 'message': 'Improper data'}))
