from __future__ import unicode_literals

from django.core.exceptions import ValidationError
from django.db import models


class Book(models.Model):
    # Relational fields
    id = models.AutoField(primary_key=True)
    # Data fields
    name = models.TextField(unique=True)

    def save(self, force_insert=False, force_update=False, using=None, update_fields=None):
        is_valid = self.is_valid()
        if is_valid is True:
            super(Book, self).save(force_insert=False, force_update=False, using=None, update_fields=None)
        else:
            raise ValidationError(is_valid[1])

    def is_valid(self):
        is_name_non_empty = self.is_name_non_empty()
        if is_name_non_empty is True:
            return True
        else:
            error_message = is_name_non_empty[1]
        return False, error_message

    def is_name_non_empty(self):
        if self.name == '':
            return False, 'Can\'t have empty name'

        return True

    def __str__(self):
        return str(self.id) + ':' + self.name


class Page(models.Model):
    # Relational fields
    id = models.AutoField(primary_key=True)
    book = models.ForeignKey(Book, on_delete=models.CASCADE, related_name='pages', blank=True, null=True)
    # Data fields
    name = models.TextField()  # unique in its book
    content = models.TextField()

    def save(self, force_insert=False, force_update=False, using=None, update_fields=None):
        is_valid = self.is_valid()
        if is_valid is True:
            super(Page, self).save(force_insert=False, force_update=False, using=None, update_fields=None)
        else:
            raise ValidationError(is_valid[1])

    def is_valid(self):
        is_name_non_empty = self.is_name_non_empty()
        if is_name_non_empty is True:
            is_unique_in_its_book = self.is_unique_in_its_book()
            if is_unique_in_its_book is True:
                return True
            else:
                error_message = is_unique_in_its_book[1]
        else:
            error_message = is_name_non_empty[1]
        return False, error_message

    def is_name_non_empty(self):
        if self.name == '':
            return False, 'Can\'t have empty name'

        return True

    def is_unique_in_its_book(self):
        if self.book is not None:
            siblings = self.book.pages.all()
            for sibling in siblings:
                if self.name == sibling.name:
                    return False, 'Can\'t have same name as sibling'

        return True

    def __str__(self):
        return str(self.id) + ':' + self.name
