from django.conf.urls import url
from . import views

app_name = 'webinterface'

urlpatterns = [
    # host/cli/
    url(r'^cli/$', views.cli, name='cli'),

    # host/editor/<GET book_name, page_name, mode>
    url(r'^editor/$', views.editor, name='editor'),
]
