from django.conf.urls import url, include
from django.contrib import admin

urlpatterns = [
    # host/admin/
    url(r'^admin/', admin.site.urls),

    # host/paper/
    url(r'^paper/', include('paper.urls')),
]
