import json

from django.core.exceptions import ObjectDoesNotExist
from django.http import HttpResponse
from django.shortcuts import render
from django.utils.datastructures import MultiValueDictKeyError

from paper.models import Book, Page


def cli(request):
    return render(request, 'webinterface/cli.html')


def editor(request):
    try:
        book_name = request.GET['book_name']
        page_name = request.GET['page_name']
        # Mode of opening
        # Default is readonly
        try:
            readonly = request.GET['readonly']
            if readonly == '0':
                readonly = False
            else:
                readonly = True
        except MultiValueDictKeyError:
            readonly = True

        # Get book
        if book_name == '':
            book = None
        else:
            book = Book.objects.get(name=book_name)

        existing_page = Page.objects.get(name=page_name, book=book)

        return render(request, 'webinterface/editor.html', {'page': existing_page, 'readonly': readonly})
    except ObjectDoesNotExist:
        return HttpResponse(json.dumps({'status': -1, 'message': 'Inconsistent data'}))
    except (ValueError, TypeError, MultiValueDictKeyError):
        return HttpResponse(json.dumps({'status': -1, 'message': 'Improper data'}))


def home(request):
    return render(request, 'webinterface/home.html')
