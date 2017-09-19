from django.conf.urls import url
from . import views

app_name = 'webinterface'

urlpatterns = [
    # for debugging
    # host/scratch/
    url(r'^scratch/$', views.scratch, name='scratch'),

    # host/
    url(r'^$', views.index, name='index'),
    # host/goal/glance/
    url(r'^goal/glance/$', views.goal_glance, name='goal_glance'),
]
