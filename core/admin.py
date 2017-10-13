from django.contrib import admin

from models.dtt import DTT
from models.dtb import DTB
from core.models.goal import Goal
from models.job import Job

admin.site.register(DTT)
admin.site.register(DTB)
admin.site.register(Goal)
admin.site.register(Job)
