from django.contrib import admin

from core.models.dtt import DTT
from core.models.dtb import DTB
from core.models.goal import Goal

admin.site.register(DTT)
admin.site.register(DTB)
admin.site.register(Goal)
