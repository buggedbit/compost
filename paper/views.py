import json

from django.http import HttpResponse
from django.shortcuts import render
from models import Book, Chapter


def book_jsonize(book):
    chapter_ids = []
    for chapter in book.chapters:
        chapter_ids.append(chapter.id)

    jsoned_book = {
        'description': book.description,
        'id': book.id,
        'chapter_ids': chapter_ids,
        'child_ids': child_ids
    }
    return jsoned_book


def book_create(request):
    if request.method == 'POST':
        try:
            name = request.POST['name']
            new_book = Book(name=name)
            new_book.save()
            return HttpResponse(json.dumps({'status': 0, 'body': jsonize(new_book)}))
        except (ValueError, TypeError):
            return HttpResponse(json.dumps({'status': -1, 'message': 'Improper data'}))
    else:
        return HttpResponse(json.dumps({'status': -1, 'message': 'Invalid request'}))


def book_update_name(request):
    return None


def book_delete(request):
    return None
