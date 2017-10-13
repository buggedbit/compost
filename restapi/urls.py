from django.conf.urls import url
from views import goal

app_name = 'restapi'

urlpatterns = [
    # host/rest/goal/read/regex/?regex=REGEX_PATTERN & global_search=(1 or 0)
    url(r'^goal/read/regex/$', goal.read_regex, name='goal_read_regex'),
    # host/rest/goal/read/family/pk
    url(r'^goal/read/family/(?P<pk>[1-9][0-9]*)/$', goal.read_family, name='goal_read_family'),

    # host/rest/goal/create/<POST description, deadline>
    url(r'^goal/create/$', goal.create, name='goal_create'),
    # host/rest/goal/update/<POST id, description, deadline>
    url(r'^goal/update/$', goal.update, name='goal_update'),
    # host/rest/goal/delete/if_single/<POST id>
    url(r'^goal/delete/if_single/$', goal.delete_if_single, name='goal_delete_if_single'),
    # host/rest/goal/add_relation/<POST parent_id, child_id>
    url(r'^goal/add_relation/$', goal.add_relation, name='goal_add_relation'),
    # host/rest/goal/remove_relation/<POST parent_id, child_id>
    url(r'^goal/remove_relation/$', goal.remove_relation, name='goal_remove_relation'),

    # host/rest/goal/toggle/is_achieved/pk
    url(r'^goal/toggle/is_achieved/(?P<pk>[1-9][0-9]*)/$', goal.toggle_is_achieved,
        name='goal_toggle_is_achieved'),
]
