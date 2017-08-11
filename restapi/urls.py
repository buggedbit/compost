from django.conf.urls import url
from . import views

app_name = 'restapi'

urlpatterns = [
    # host/paper/book/create/<POST name>
    url(r'^book/create/$', views.book_create, name='book_create'),
    # host/paper/book/update/name/<POST id, new_name>
    url(r'^book/update/name/$', views.book_update_name, name='book_update_name'),
    # host/paper/book/delete/<POST id>
    url(r'^book/delete/$', views.book_delete, name='book_delete'),
]
