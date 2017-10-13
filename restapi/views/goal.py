from django.core.exceptions import ObjectDoesNotExist
from django.http import HttpResponse
from django.utils.datastructures import MultiValueDictKeyError
from datetime import datetime as dt

from django.views.decorators.csrf import csrf_exempt

from core.models.goal import Goal
import re
import json
from core.dacs.goaldac import GoalDAC


@csrf_exempt
def jsonize_goal(goal):
    deadline = None
    if goal.deadline is not None:
        deadline = {
            'year': goal.deadline.year,
            'month': goal.deadline.month,
            'day': goal.deadline.day,
            'hour': goal.deadline.hour,
            'minute': goal.deadline.minute,
            'second': goal.deadline.second,
            'microsecond': goal.deadline.microsecond,
        }
    parent_ids = []
    for parent in goal.get_parents():
        parent_ids.append(parent.id)
    child_ids = []
    for child in goal.get_children():
        child_ids.append(child.id)

    json_goal = {
        'description': goal.description,
        'deadline': deadline,
        'is_achieved': goal.is_achieved,
        'id': goal.id,
        'parent_ids': parent_ids,
        'child_ids': child_ids,
        'color': goal.color,
    }
    return json_goal


@csrf_exempt
def jsonize_goal_iterable(goal_ids):
    jsoned = []
    for goal_id in goal_ids:
        goal = Goal.objects.get(pk=goal_id)
        jsoned.append(jsonize_goal(goal))
    return jsoned


@csrf_exempt
def read_regex(request):
    if request.method == 'GET':
        try:
            regex = request.GET['regex']
            global_search = request.GET['global_search']

            matched_goal_family_subsets = GoalDAC.read_regex(regex, global_search)

            json_goal_family_subsets = []
            for family_subset in matched_goal_family_subsets:
                json_family_subset = []
                for goal in family_subset:
                    json_family_subset.append(jsonize_goal(goal))
                json_goal_family_subsets.append(json_family_subset)

            return HttpResponse(json.dumps({'status': 0, 'body': json_goal_family_subsets}))
        except MultiValueDictKeyError:
            return HttpResponse(json.dumps({'status': -1, 'message': 'Improper data'}))
        except re.error:
            return HttpResponse(json.dumps({'status': -1, 'message': 'Not proper regex'}))
    else:
        return HttpResponse(json.dumps({'status': -1, 'message': 'No regex string in request'}))


@csrf_exempt
def read_family(request, pk):
    try:

        goal_family = GoalDAC.read_family(pk)
        json_family = []
        for member in goal_family:
            json_family.append(jsonize_goal(member))

        return HttpResponse(json.dumps({'status': 0, 'body': json_family}))
    except ObjectDoesNotExist:
        return HttpResponse(json.dumps({'status': -1, 'message': 'No goal with such id'}))


@csrf_exempt
def create(request):
    if request.method == 'POST':
        try:
            description = request.POST['description']
            deadline = json.loads(request.POST['deadline'])

            if deadline is not None:
                deadline = dt(year=deadline['year'],
                              month=deadline['month'],
                              day=deadline['day'],
                              hour=deadline['hour'],
                              minute=deadline['minute'],
                              second=deadline['second'],
                              microsecond=deadline['microsecond'])
            is_created = GoalDAC.create(description, deadline)

            if is_created[0] is True:
                return HttpResponse(json.dumps({'status': 0, 'body': jsonize_goal(is_created[1])}))
            else:
                return HttpResponse(json.dumps({'status': -1, 'message': is_created[1]}))

        except (ValueError, TypeError, MultiValueDictKeyError):
            return HttpResponse(json.dumps({'status': -1, 'message': 'Improper data'}))
    else:
        return HttpResponse(json.dumps({'status': -1, 'message': 'Invalid request'}))


@csrf_exempt
def update(request):
    if request.method == 'POST':
        try:
            pk = request.POST['id']
            description = request.POST['description']
            deadline = json.loads(request.POST['deadline'])
            if deadline is not None:
                deadline = dt(year=deadline['year'],
                              month=deadline['month'],
                              day=deadline['day'],
                              hour=deadline['hour'],
                              minute=deadline['minute'],
                              second=deadline['second'],
                              microsecond=deadline['microsecond'])
            is_updated = GoalDAC.update(pk, description, deadline)

            if is_updated[0] is True:
                return HttpResponse(json.dumps({'status': 0, 'body': jsonize_goal(is_updated[1])}))
            else:
                return HttpResponse(json.dumps({'status': -1, 'message': is_updated[1]}))

        except (ValueError, TypeError, MultiValueDictKeyError):
            return HttpResponse(json.dumps({'status': -1, 'message': 'Improper data'}))
        except ObjectDoesNotExist:
            return HttpResponse(json.dumps({'status': -1, 'message': 'Invalid id'}))
    else:
        return HttpResponse(json.dumps({'status': -1, 'message': 'Invalid request'}))


@csrf_exempt
def delete_if_single(request):
    if request.method == 'POST':
        try:
            pk = request.POST['id']

            is_deleted = GoalDAC.delete_if_single(pk)

            if is_deleted is True:
                return HttpResponse(json.dumps({'status': 0}))
            else:
                return HttpResponse(json.dumps({'status': -1, 'message': is_deleted[1]}))

        except (ValueError, TypeError, MultiValueDictKeyError):
            return HttpResponse(json.dumps({'status': -1, 'message': 'Improper data'}))
        except ObjectDoesNotExist:
            return HttpResponse(json.dumps({'status': -1, 'message': 'Invalid id'}))
    else:
        return HttpResponse(json.dumps({'status': -1, 'message': 'Invalid request'}))


@csrf_exempt
def add_relation(request):
    if request.method == 'POST':
        try:
            parent_id = int(request.POST['parent_id'])
            child_id = int(request.POST['child_id'])

            was_relation_added = GoalDAC.add_relation(parent_id, child_id)

            if was_relation_added[0] is True:
                return HttpResponse(json.dumps({'status': 0, 'body': jsonize_goal_iterable(was_relation_added[1])}))
            else:
                return HttpResponse(json.dumps({'status': -1, 'message': was_relation_added[1]}))

        except (ValueError, TypeError, MultiValueDictKeyError):
            return HttpResponse(json.dumps({'status': -1, 'message': 'Improper data'}))
        except ObjectDoesNotExist:
            return HttpResponse(json.dumps({'status': -1, 'message': 'Invalid id'}))
    else:
        return HttpResponse(json.dumps({'status': -1, 'message': 'Invalid request'}))


@csrf_exempt
def remove_relation(request):
    if request.method == 'POST':
        try:
            parent_id = int(request.POST['parent_id'])
            child_id = int(request.POST['child_id'])

            family_id_sets = GoalDAC.remove_relation(parent_id, child_id)
            jsoned_family_id_sets = []
            for family_id_set in family_id_sets:
                jsoned_family_id_sets.append(jsonize_goal_iterable(family_id_set))

            return HttpResponse(json.dumps({'status': 0, 'body': jsoned_family_id_sets}))
        except (ValueError, TypeError, MultiValueDictKeyError):
            return HttpResponse(json.dumps({'status': -1, 'message': 'Improper data'}))
        except ObjectDoesNotExist:
            return HttpResponse(json.dumps({'status': -1, 'message': 'Invalid id'}))
    else:
        return HttpResponse(json.dumps({'status': -1, 'message': 'Invalid request'}))


@csrf_exempt
def toggle_is_achieved(request, pk):
    try:
        is_saved = GoalDAC.toggle_is_achieved(pk)
        if is_saved[0] is True:
            return HttpResponse(json.dumps({'status': 0, 'body': is_saved[1]}))
        else:
            return HttpResponse(json.dumps({'status': -1, 'message': is_saved[1]}))

    except ObjectDoesNotExist:
        return HttpResponse(json.dumps({'status': -1, 'message': 'No goal with such id'}))
