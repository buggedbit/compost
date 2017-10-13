from __future__ import unicode_literals

from django.db import models


class DTT(models.Model):
    """
    Deterministic Time Tree
    """
    id = models.AutoField(primary_key=True)

    def __str__(self):
        return str(self.id)
