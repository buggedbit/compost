from django.conf.urls import url
from . import views

app_name = 'restapi'

urlpatterns = [
    # host/restapi/ls/<GET book_name>
    url(r'^ls/$', views.ls, name='ls'),
    # host/restapi/book/exists/<GET book_name>
    url(r'^book/exists/$', views.book_exists, name='book_exists'),

    # host/restapi/book/create/<POST name>
    url(r'^book/create/$', views.book_create, name='book_create'),
    # host/restapi/book/update/name/<POST id, new_name>
    url(r'^book/update/name/$', views.book_update_name, name='book_update_name'),
    # host/restapi/book/delete/<POST id>
    url(r'^book/delete/$', views.book_delete, name='book_delete'),
]
