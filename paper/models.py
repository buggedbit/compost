from __future__ import unicode_literals

from django.db import models


class Book(models.Model):
    # Relational fields
    name = models.CharField(primary_key=True, max_length=30)

    def __str__(self):
        return self.name


class Chapter(models.Model):
    # Relational fields
    name = models.CharField(primary_key=True, max_length=30)
    book = models.ForeignKey(Book, related_name='chapters')
    # Data fields
    content = models.TextField(default='')

    def __str__(self):
        return self.name
