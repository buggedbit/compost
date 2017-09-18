from django.conf.urls import url
from . import views

app_name = 'restapi'

urlpatterns = [
    # host/restapi/sudo/<POST password>
    url(r'^sudo/$', views.sudo, name='sudo'),
    # host/restapi/unsudo/
    url(r'^unsudo/$', views.unsudo, name='unsudo'),

    # host/restapi/ls/<GET book_name>
    url(r'^ls/$', views.ls, name='ls'),

    # host/restapi/book/exists/<GET book_name>
    url(r'^book/exists/$', views.book_exists, name='book_exists'),
    # host/restapi/book/create/<POST book_name>
    url(r'^book/create/$', views.book_create, name='book_create'),
    # host/restapi/book/delete/<POST book_name>
    url(r'^book/delete/$', views.book_delete, name='book_delete'),
    # host/restapi/book/update/name/<POST old_book_name, new_book_name>
    url(r'^book/update/name/$', views.book_update_name, name='book_update_name'),

    # host/restapi/page/exists/<GET book_name, page_name>
    url(r'^page/exists/$', views.page_exists, name='page_exists'),
    # host/restapi/page/create/<POST book_name, page_name>
    url(r'^page/create/$', views.page_create, name='page_create'),
    # host/restapi/page/delete/<POST book_name, page_name>
    url(r'^page/delete/$', views.page_delete, name='page_delete'),
    # host/restapi/page/read/text/<POST book_name, page_name>
    url(r'^page/read/text/$', views.page_read_text, name='page_read_text'),
    # host/restapi/page/update/text/<POST book_name, page_name, text>
    url(r'^page/update/text/$', views.page_update_text, name='page_update_text'),
    # host/restapi/page/move/ <POST old_page_name, old_book_name, new_page_name, new_book_name>
    url(r'^page/move/$', views.page_move, name='page_move'),

]
