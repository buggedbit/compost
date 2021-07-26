from django.conf.urls import url, include
from django.contrib import admin

urlpatterns = [
    # Admin
    # host/admin/
    url(r'^admin/', admin.site.urls),

    # Rest api
    # host/restapi/
    url(r'^restapi/', include('restapi.urls')),

    # Web interface
    # host/
    url(r'^', include('webinterface.urls')),
]
