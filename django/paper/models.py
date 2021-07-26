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
        is_name_valid = self.is_name_valid()
        if is_name_valid is True:
            return True
        else:
            error_message = is_name_valid[1]
        return False, error_message

    def is_name_valid(self):
        if self.name == '':
            return False, 'Can\'t have empty name'
        if self.name == '.':
            return False, 'Can\'t have name as .'
        if self.name == '..':
            return False, 'Can\'t have name as ..'
        if '/' in self.name:
            return False, 'Name can\'t have / in it'
        if len(self.name.split()) > 1:
            return False, 'Can\'t have whitespaces or tabs or newlines in name'

        return True

    def __str__(self):
        return str(self.id) + ':' + self.name


class Page(models.Model):
    # Relational fields
    id = models.AutoField(primary_key=True)
    book = models.ForeignKey(Book, on_delete=models.CASCADE, related_name='pages', blank=True, null=True)
    # Data fields
    name = models.TextField()  # unique in its book
    text = models.TextField()

    def save(self, force_insert=False, force_update=False, using=None, update_fields=None):
        is_valid = self.is_valid()
        if is_valid is True:
            super(Page, self).save(force_insert=False, force_update=False, using=None, update_fields=None)
        else:
            raise ValidationError(is_valid[1])

    def is_valid(self):
        is_name_valid = self.is_name_valid()
        if is_name_valid is True:
            is_unique_in_its_domain = self.is_unique_in_its_domain()
            if is_unique_in_its_domain is True:
                return True
            else:
                error_message = is_unique_in_its_domain[1]
        else:
            error_message = is_name_valid[1]
        return False, error_message

    def is_name_valid(self):
        if self.name == '':
            return False, 'Can\'t have empty name'
        if self.name == '.':
            return False, 'Can\'t have name as .'
        if self.name == '..':
            return False, 'Can\'t have name as ..'
        if '/' in self.name:
            return False, 'Name can\'t have / in it'
        if len(self.name.split()) > 1:
            return False, 'Can\'t have whitespaces or tabs or newlines in name'

        return True

    def is_unique_in_its_domain(self):
        if self.book is not None:
            siblings = self.book.pages.all()
            # If new one
            if self.id is None:
                for sibling in siblings:
                    if self.name == sibling.name:
                        return False, 'Two pages in same book can\'t have same name'
            # If existing one
            else:
                for sibling in siblings:
                    if self.name == sibling.name and sibling.id != self.id:
                        return False, 'Two pages in same book can\'t have same name'
        else:
            siblings = Page.objects.filter(book=None)
            # If new one
            if self.id is None:
                for sibling in siblings:
                    if self.name == sibling.name:
                        return False, 'Two loose pages can\'t have same name'
            # If existing one
            else:
                for sibling in siblings:
                    if self.name == sibling.name and sibling.id != self.id:
                        return False, 'Two loose pages can\'t have same name'

        return True

    def __str__(self):
        return str(self.id) + ':' + self.name
