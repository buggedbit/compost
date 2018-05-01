from django.conf.urls import url
from interface.views import goalapis, sessionapis
from interface.views import webpages

app_name = 'interface'

urlpatterns = [

    # webpages
    # host/scratch/
    url(r'^scratch/$', webpages.scratch, name='scratch'),
    # host/
    url(r'^$', webpages.index, name='index'),
    # host/goal/glance/
    url(r'^goal/glance/$', webpages.goal_glance, name='goal_glance'),

    # sessionapis
    # host/login/<POST password>
    url(r'^login/$', sessionapis.login, name='login'),
    # host/logout/
    url(r'^logout/$', sessionapis.logout, name='logout'),

    # goalapis
    # read
    # host/goal/read/regex/?regex=REGEX_PATTERN & global_search=(1 or 0)
    url(r'^goal/read/regex/$', goalapis.read_regex, name='goal_read_regex'),
    # host/goal/read/family/pk
    url(r'^goal/read/family/(?P<pk>[1-9][0-9]*)/$', goalapis.read_family, name='goal_read_family'),
    # write
    # host/goal/create/<POST description, deadline>
    url(r'^goal/create/$', goalapis.create, name='goal_create'),
    # host/goal/update/<POST id, description, deadline>
    url(r'^goal/update/$', goalapis.update, name='goal_update'),
    # host/goal/delete/if_single/<POST id>
    url(r'^goal/delete/if_single/$', goalapis.delete_if_single, name='goal_delete_if_single'),
    # host/goal/add_relation/<POST parent_id, child_id>
    url(r'^goal/add_relation/$', goalapis.add_relation, name='goal_add_relation'),
    # host/goal/remove_relation/<POST parent_id, child_id>
    url(r'^goal/remove_relation/$', goalapis.remove_relation, name='goal_remove_relation'),
    # host/goal/toggle/is_achieved/pk
    url(r'^goal/toggle/is_achieved/(?P<pk>[1-9][0-9]*)/$', goalapis.toggle_is_achieved,
        name='goal_toggle_is_achieved'),
]
