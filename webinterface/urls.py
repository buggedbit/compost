from django.conf.urls import url
from webinterface.views import ajax
from webinterface.views import template

app_name = 'webinterface'

urlpatterns = [
    # host/scratch/
    url(r'^scratch/$', template.scratch, name='scratch'),

    # host/
    url(r'^$', template.index, name='index'),
    # host/goal/glance/
    url(r'^goal/glance/$', template.goal_glance, name='goal_glance'),

    # host/goal/read/regex/?regex=REGEX_PATTERN & global_search=(1 or 0)
    url(r'^goal/read/regex/$', ajax.read_regex, name='goal_read_regex'),
    # host/goal/read/family/pk
    url(r'^goal/read/family/(?P<pk>[1-9][0-9]*)/$', ajax.read_family, name='goal_read_family'),

    # host/goal/create/<POST description, deadline>
    url(r'^goal/create/$', ajax.create, name='goal_create'),
    # host/goal/update/<POST id, description, deadline>
    url(r'^goal/update/$', ajax.update, name='goal_update'),
    # host/goal/delete/if_single/<POST id>
    url(r'^goal/delete/if_single/$', ajax.delete_if_single, name='goal_delete_if_single'),
    # host/goal/add_relation/<POST parent_id, child_id>
    url(r'^goal/add_relation/$', ajax.add_relation, name='goal_add_relation'),
    # host/goal/remove_relation/<POST parent_id, child_id>
    url(r'^goal/remove_relation/$', ajax.remove_relation, name='goal_remove_relation'),
    # host/goal/toggle/is_achieved/pk
    url(r'^goal/toggle/is_achieved/(?P<pk>[1-9][0-9]*)/$', ajax.toggle_is_achieved,
        name='goal_toggle_is_achieved'),
]
