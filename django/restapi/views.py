import json

from django.core.exceptions import ObjectDoesNotExist, ValidationError
from django.db import IntegrityError
from django.http import HttpResponse
from django.utils.datastructures import MultiValueDictKeyError

from paper.models import Book, Page
from restapi.exceptions import SudoException

PASSWORD = 'airturtle'


def sudo(request):
    if request.method == 'POST':
        password = request.POST['password']
        if password == PASSWORD:
            request.session['sudo'] = True
            return HttpResponse(json.dumps({'status': 0}))
        else:
            return HttpResponse(json.dumps({'status': -1, 'message': 'Incorrect password'}))
    else:
        return HttpResponse(json.dumps({'status': -1, 'message': 'Invalid request'}))


def unsudo(request):
    try:
        del request.session['sudo']
    except KeyError:
        pass
    return HttpResponse(json.dumps({'status': 0}))


def confirm_sudo_mode(request):
    if not request.session.get('sudo', False):
        raise SudoException('Not in sudo mode')


def ls(request):
    if request.method == 'GET':
        try:
            book_name = request.GET['book_name']
            answer = {'pages': [], 'books': []}

            # list out all books and loose pages
            if book_name == '':
                all_books = Book.objects.all()
                for book in all_books:
                    answer['books'].append(book.name)
                loose_pages = Page.objects.filter(book=None)
                for loose_page in loose_pages:
                    answer['pages'].append(loose_page.name)

            # list out all pages in that book
            else:
                book = Book.objects.get(name=book_name)
                for page in book.pages.all():
                    answer['pages'].append(page.name)

            return HttpResponse(json.dumps({'status': 0, 'body': answer}))
        except ObjectDoesNotExist:
            return HttpResponse(json.dumps({'status': -1, 'message': 'Inconsistent data'}))
        except (ValueError, TypeError, MultiValueDictKeyError):
            return HttpResponse(json.dumps({'status': -1, 'message': 'Improper data'}))
    else:
        return HttpResponse(json.dumps({'status': -1, 'message': 'Invalid request'}))


def book_jsonize(book):
    jsoned_book = {
        'id': book.id,
        'name': book.name,
    }
    return jsoned_book


def book_exists(request):
    if request.method == 'GET':
        try:
            book_name = request.GET['book_name']
            Book.objects.get(name=book_name)
            return HttpResponse(json.dumps({'status': 0}))
        except ObjectDoesNotExist:
            return HttpResponse(json.dumps({'status': -1, 'message': 'Book doesn\'t exist'}))
        except (ValueError, TypeError, MultiValueDictKeyError):
            return HttpResponse(json.dumps({'status': -1, 'message': 'Improper data'}))
    else:
        return HttpResponse(json.dumps({'status': -1, 'message': 'Invalid request'}))


def book_create(request):
    if request.method == 'POST':
        try:
            confirm_sudo_mode(request)
            name = request.POST['book_name']
            new_book = Book(name=name)
            new_book.save()
            return HttpResponse(json.dumps({'status': 0, 'body': book_jsonize(new_book)}))
        except IntegrityError:
            return HttpResponse(json.dumps({'status': -1, 'message': 'Two books cannot have same name'}))
        except ValidationError as e:
            return HttpResponse(json.dumps({'status': -1, 'message': e.message}))
        except (ValueError, TypeError, MultiValueDictKeyError):
            return HttpResponse(json.dumps({'status': -1, 'message': 'Improper data'}))
        except SudoException as e:
            return HttpResponse(json.dumps({'status': -1, 'message': e.message}))
    else:
        return HttpResponse(json.dumps({'status': -1, 'message': 'Invalid request'}))


def book_delete(request):
    if request.method == 'POST':
        try:
            confirm_sudo_mode(request)
            name = request.POST['book_name']
            existing_book = Book.objects.get(name=name)
            jsoned_book = book_jsonize(existing_book)
            existing_book.delete()
            return HttpResponse(json.dumps({'status': 0, 'body': jsoned_book}))
        except ObjectDoesNotExist:
            return HttpResponse(json.dumps({'status': -1, 'message': 'Inconsistent data'}))
        except (ValueError, TypeError, MultiValueDictKeyError):
            return HttpResponse(json.dumps({'status': -1, 'message': 'Improper data'}))
        except SudoException as e:
            return HttpResponse(json.dumps({'status': -1, 'message': e.message}))
    else:
        return HttpResponse(json.dumps({'status': -1, 'message': 'Invalid request'}))


def book_update_name(request):
    if request.method == 'POST':
        try:
            confirm_sudo_mode(request)
            old_name = request.POST['old_book_name']
            new_name = request.POST['new_book_name']
            existing_book = Book.objects.get(name=old_name)
            existing_book.name = new_name
            existing_book.save()
            return HttpResponse(json.dumps({'status': 0, 'body': book_jsonize(existing_book)}))
        except ObjectDoesNotExist:
            return HttpResponse(json.dumps({'status': -1, 'message': 'Book doesn\'t exist'}))
        except IntegrityError:
            return HttpResponse(json.dumps({'status': -1, 'message': 'Two books cannot have same name'}))
        except ValidationError as e:
            return HttpResponse(json.dumps({'status': -1, 'message': e.message}))
        except (ValueError, TypeError, MultiValueDictKeyError):
            return HttpResponse(json.dumps({'status': -1, 'message': 'Improper data'}))
        except SudoException as e:
            return HttpResponse(json.dumps({'status': -1, 'message': e.message}))
    else:
        return HttpResponse(json.dumps({'status': -1, 'message': 'Invalid request'}))


def page_jsonize(page):
    jsoned_page = {
        'id': page.id,
        'name': page.name,
    }
    return jsoned_page


def page_exists(request):
    if request.method == 'POST':
        try:
            book_name = request.POST['book_name']
            page_name = request.POST['page_name']
            # Get book
            if book_name == '':
                book = None
            else:
                book = Book.objects.get(name=book_name)

            Page.objects.get(name=page_name, book=book)

            return HttpResponse(json.dumps({'status': 0}))
        except ObjectDoesNotExist:
            return HttpResponse(json.dumps({'status': -1, 'message': 'Inconsistent data'}))
        except (ValueError, TypeError, MultiValueDictKeyError):
            return HttpResponse(json.dumps({'status': -1, 'message': 'Improper data'}))
    else:
        return HttpResponse(json.dumps({'status': -1, 'message': 'Invalid request'}))


def page_create(request):
    if request.method == 'POST':
        try:
            confirm_sudo_mode(request)
            book_name = request.POST['book_name']
            page_name = request.POST['page_name']
            # Get book
            if book_name == '':
                book = None
            else:
                book = Book.objects.get(name=book_name)

            new_page = Page(name=page_name)
            new_page.book = book
            new_page.save()

            return HttpResponse(json.dumps({'status': 0, 'body': page_jsonize(new_page)}))
        except ValidationError as e:
            return HttpResponse(json.dumps({'status': -1, 'message': e.message}))
        except ObjectDoesNotExist:
            return HttpResponse(json.dumps({'status': -1, 'message': 'Inconsistent data'}))
        except (ValueError, TypeError, MultiValueDictKeyError):
            return HttpResponse(json.dumps({'status': -1, 'message': 'Improper data'}))
        except SudoException as e:
            return HttpResponse(json.dumps({'status': -1, 'message': e.message}))
    else:
        return HttpResponse(json.dumps({'status': -1, 'message': 'Invalid request'}))


def page_delete(request):
    if request.method == 'POST':
        try:
            confirm_sudo_mode(request)
            book_name = request.POST['book_name']
            page_name = request.POST['page_name']
            # Get book
            if book_name == '':
                book = None
            else:
                book = Book.objects.get(name=book_name)

            existing_page = Page.objects.get(name=page_name, book=book)
            jsoned_page = page_jsonize(existing_page)
            existing_page.delete()

            return HttpResponse(json.dumps({'status': 0, 'body': jsoned_page}))
        except ObjectDoesNotExist:
            return HttpResponse(json.dumps({'status': -1, 'message': 'Inconsistent data'}))
        except (ValueError, TypeError, MultiValueDictKeyError):
            return HttpResponse(json.dumps({'status': -1, 'message': 'Improper data'}))
        except SudoException as e:
            return HttpResponse(json.dumps({'status': -1, 'message': e.message}))
    else:
        return HttpResponse(json.dumps({'status': -1, 'message': 'Invalid request'}))


def page_read_text(request):
    if request.method == 'POST':
        try:
            book_name = request.POST['book_name']
            page_name = request.POST['page_name']
            # Get book
            if book_name == '':
                book = None
            else:
                book = Book.objects.get(name=book_name)

            existing_page = Page.objects.get(name=page_name, book=book)

            return HttpResponse(json.dumps({'status': 0, 'body': existing_page.text}))
        except ObjectDoesNotExist:
            return HttpResponse(json.dumps({'status': -1, 'message': 'Inconsistent data'}))
        except (ValueError, TypeError, MultiValueDictKeyError):
            return HttpResponse(json.dumps({'status': -1, 'message': 'Improper data'}))
    else:
        return HttpResponse(json.dumps({'status': -1, 'message': 'Invalid request'}))


def page_update_text(request):
    if request.method == 'POST':
        try:
            book_name = request.POST['book_name']
            page_name = request.POST['page_name']
            text = request.POST['text']
            password = request.POST['password']
            # Password check
            if password != PASSWORD:
                return HttpResponse(json.dumps({'status': -1, 'message': 'Invalid Password'}))

            # Get book
            if book_name == '':
                book = None
            else:
                book = Book.objects.get(name=book_name)

            existing_page = Page.objects.get(name=page_name, book=book)
            existing_page.text = text
            existing_page.save()

            return HttpResponse(json.dumps({'status': 0}))
        except ObjectDoesNotExist:
            return HttpResponse(json.dumps({'status': -1, 'message': 'Inconsistent data'}))
        except (ValueError, TypeError, MultiValueDictKeyError):
            return HttpResponse(json.dumps({'status': -1, 'message': 'Improper data'}))
    else:
        return HttpResponse(json.dumps({'status': -1, 'message': 'Invalid request'}))


def page_move(request):
    if request.method == 'POST':
        try:
            confirm_sudo_mode(request)
            prev_book_name = request.POST['prev_book_name']
            prev_page_name = request.POST['prev_page_name']

            pres_book_name = request.POST['pres_book_name']
            pres_page_name = request.POST['pres_page_name']

            # Get prev book
            if prev_book_name == '':
                prev_book = None
            else:
                prev_book = Book.objects.get(name=prev_book_name)

            # Get book to which page is moved (pres book)
            if pres_book_name == '':
                pres_book = None
            else:
                pres_book = Book.objects.get(name=pres_book_name)

            prev_page = Page.objects.get(name=prev_page_name, book=prev_book)
            prev_page.name = pres_page_name
            prev_page.book = pres_book
            prev_page.save()

            return HttpResponse(json.dumps({'status': 0, 'body': page_jsonize(prev_page)}))
        except ObjectDoesNotExist:
            return HttpResponse(json.dumps({'status': -1, 'message': 'Inconsistent data'}))
        except (ValueError, TypeError, MultiValueDictKeyError):
            return HttpResponse(json.dumps({'status': -1, 'message': 'Improper data'}))
        except SudoException as e:
            return HttpResponse(json.dumps({'status': -1, 'message': e.message}))
    else:
        return HttpResponse(json.dumps({'status': -1, 'message': 'Invalid request'}))
