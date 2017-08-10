import json

from django.db import IntegrityError
from django.http import HttpResponse
from paper.models import Book, Chapter


def book_jsonize(book):
    jsoned_book = {
        'id': book.id,
        'name': book.name,
    }
    return jsoned_book


def book_create(request):
    if request.method == 'POST':
        try:
            name = request.POST['name']
            new_book = Book(name=name)
            new_book.save()
            return HttpResponse(json.dumps({'status': 0, 'body': book_jsonize(new_book)}))
        except IntegrityError:
            return HttpResponse(json.dumps({'status': -1, 'message': 'Constraints not met'}))
        except (ValueError, TypeError):
            return HttpResponse(json.dumps({'status': -1, 'message': 'Improper data'}))
    else:
        return HttpResponse(json.dumps({'status': -1, 'message': 'Invalid request'}))


def book_update_name(request):
    return None


def book_delete(request):
    return None
