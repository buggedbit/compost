from __future__ import unicode_literals

from django.core.exceptions import ValidationError
from django.db import models


class Book(models.Model):
    # Relational fields
    id = models.AutoField(primary_key=True)
    # Data fields
    name = models.TextField(unique=True)

    def __str__(self):
        return self.name


class Chapter(models.Model):
    # Relational fields
    id = models.AutoField(primary_key=True)
    book = models.ForeignKey(Book, on_delete=models.CASCADE, related_name='chapters')
    # Data fields
    name = models.TextField()  # unique in its book
    content = models.TextField()

    def save(self, force_insert=False, force_update=False, using=None, update_fields=None):
        is_valid = self.is_valid()
        if is_valid is True:
            super(Chapter, self).save(force_insert=False, force_update=False, using=None, update_fields=None)
        else:
            raise ValidationError(is_valid[1])

    def is_valid(self):
        is_unique_in_its_book = self.is_unique_in_its_book()
        if is_unique_in_its_book is True:
            return True
        else:
            error_message = is_unique_in_its_book[1]
        return False, error_message

    def is_unique_in_its_book(self):
        if self.book is not None:
            siblings = self.book.chapters.all()
            for sibling in siblings:
                if self.name == sibling.name:
                    return False, 'Can\'t have same name as sibling'

        return True

    def __str__(self):
        return str(self.id) + ':' + self.name
