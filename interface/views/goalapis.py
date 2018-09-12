from django.core.exceptions import ObjectDoesNotExist
from django.http import HttpResponse
from django.utils.datastructures import MultiValueDictKeyError
from datetime import datetime as dt

from django.views.decorators.csrf import csrf_exempt

from interface.views.responsewrapper import ResponseWrapper
import re
import json
from core.query.goal import Goal
from interface.views.sessionapis import is_session_active


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
def read_regex(request):
    if is_session_active(request.session):
        if request.method == 'POST':
            try:
                regex = request.POST['regex']
                is_global_search = request.POST['is_global_search']

                matched_goal_family_subsets = Goal.read_regex(regex, is_global_search)

                json_goal_family_subsets = []
                for family_subset in matched_goal_family_subsets:
                    json_family_subset = []
                    for goal in family_subset:
                        json_family_subset.append(jsonize_goal(goal))
                    json_goal_family_subsets.append(json_family_subset)

                return HttpResponse(
                    json.dumps(ResponseWrapper.of(json_goal_family_subsets, ResponseWrapper.ARRAY_RESPONSE)))
            except MultiValueDictKeyError:
                return HttpResponse(json.dumps(ResponseWrapper.error('Improper data')))
            except re.error:
                return HttpResponse(json.dumps(ResponseWrapper.error('Not proper regex')))
        else:
            return HttpResponse(json.dumps(ResponseWrapper.error('Invalid request')))
    else:
        return HttpResponse(json.dumps(ResponseWrapper.error('Invalid session')))


@csrf_exempt
def read_family(request, pk):
    if is_session_active(request.session):
        if request.method == 'POST':
            try:
                goal_family = Goal.read_family(pk)
                json_family = []
                for member in goal_family:
                    json_family.append(jsonize_goal(member))

                return HttpResponse(json.dumps(ResponseWrapper.of(json_family, ResponseWrapper.ARRAY_RESPONSE)))
            except ObjectDoesNotExist:
                return HttpResponse(json.dumps(ResponseWrapper.error('No goal with such id')))
        else:
            return HttpResponse(json.dumps(ResponseWrapper.error('Invalid request')))
    else:
        return HttpResponse(json.dumps(ResponseWrapper.error('Invalid session')))


@csrf_exempt
def create(request):
    if is_session_active(request.session):
        if request.method == 'POST':
            try:
                description = request.POST['description']
                deadline = json.loads(request.POST['deadline'])
                color = request.POST['color']

                if deadline is not None:
                    deadline = dt(year=deadline['year'],
                                  month=deadline['month'],
                                  day=deadline['day'],
                                  hour=deadline['hour'],
                                  minute=deadline['minute'],
                                  second=deadline['second'],
                                  microsecond=deadline['microsecond'])
                is_created = Goal.create(description, deadline, color)

                if is_created[0] is True:
                    return HttpResponse(
                        json.dumps(ResponseWrapper.of(jsonize_goal(is_created[1]), ResponseWrapper.OBJECT_RESPONSE)))
                else:
                    return HttpResponse(json.dumps(ResponseWrapper.error(is_created[1])))

            except (ValueError, TypeError, MultiValueDictKeyError):
                return HttpResponse(json.dumps(ResponseWrapper.error('Improper data')))
        else:
            return HttpResponse(json.dumps(ResponseWrapper.error('Invalid request')))
    else:
        return HttpResponse(json.dumps(ResponseWrapper.error('Invalid session')))


@csrf_exempt
def update(request):
    if is_session_active(request.session):
        if request.method == 'POST':
            try:
                pk = request.POST['id']
                description = request.POST['description']
                deadline = json.loads(request.POST['deadline'])
                color = request.POST['color']

                if deadline is not None:
                    deadline = dt(year=deadline['year'],
                                  month=deadline['month'],
                                  day=deadline['day'],
                                  hour=deadline['hour'],
                                  minute=deadline['minute'],
                                  second=deadline['second'],
                                  microsecond=deadline['microsecond'])
                is_updated = Goal.update(pk, description, deadline, color)

                if is_updated[0] is True:
                    return HttpResponse(
                        json.dumps(ResponseWrapper.of(jsonize_goal(is_updated[1]), ResponseWrapper.OBJECT_RESPONSE)))
                else:
                    return HttpResponse(json.dumps(ResponseWrapper.error(is_updated[1])))

            except (ValueError, TypeError, MultiValueDictKeyError):
                return HttpResponse(json.dumps(ResponseWrapper.error('Improper data')))
            except ObjectDoesNotExist:
                return HttpResponse(json.dumps(ResponseWrapper.error('Invalid id')))
        else:
            return HttpResponse(json.dumps(ResponseWrapper.error('Invalid request')))
    else:
        return HttpResponse(json.dumps(ResponseWrapper.error('Invalid session')))


@csrf_exempt
def chain_update(request):
    if is_session_active(request.session):
        if request.method == 'POST':
            try:
                pk = request.POST['id']
                description = request.POST['description']
                deadline = json.loads(request.POST['deadline'])
                color = request.POST['color']

                if deadline is not None:
                    deadline = dt(year=deadline['year'],
                                  month=deadline['month'],
                                  day=deadline['day'],
                                  hour=deadline['hour'],
                                  minute=deadline['minute'],
                                  second=deadline['second'],
                                  microsecond=deadline['microsecond'])
                is_updated = Goal.chain_update(pk, description, deadline, color)

                if is_updated[0] is True:
                    goal_family = is_updated[1]
                    json_family = []
                    for member in goal_family:
                        json_family.append(jsonize_goal(member))

                    return HttpResponse(
                        json.dumps(ResponseWrapper.of(json_family, ResponseWrapper.OBJECT_RESPONSE)))
                else:
                    return HttpResponse(json.dumps(ResponseWrapper.error(is_updated[1])))

            except (ValueError, TypeError, MultiValueDictKeyError):
                return HttpResponse(json.dumps(ResponseWrapper.error('Improper data')))
            except ObjectDoesNotExist:
                return HttpResponse(json.dumps(ResponseWrapper.error('Invalid id')))
        else:
            return HttpResponse(json.dumps(ResponseWrapper.error('Invalid request')))
    else:
        return HttpResponse(json.dumps(ResponseWrapper.error('Invalid session')))


@csrf_exempt
def delete_if_single(request):
    if is_session_active(request.session):
        if request.method == 'POST':
            try:
                pk = request.POST['id']

                is_deleted = Goal.delete_if_single(pk)

                if is_deleted is True:
                    return HttpResponse(json.dumps(ResponseWrapper.of(0, ResponseWrapper.NUMBER_RESPONSE)))
                else:
                    return HttpResponse(json.dumps(ResponseWrapper.error(is_deleted[1])))

            except (ValueError, TypeError, MultiValueDictKeyError):
                return HttpResponse(json.dumps(ResponseWrapper.error('Improper data')))
            except ObjectDoesNotExist:
                return HttpResponse(json.dumps(ResponseWrapper.error('Invalid id')))
        else:
            return HttpResponse(json.dumps(ResponseWrapper.error('Invalid request')))
    else:
        return HttpResponse(json.dumps(ResponseWrapper.error('Invalid session')))


@csrf_exempt
def add_relation(request):
    if is_session_active(request.session):
        if request.method == 'POST':
            try:
                parent_id = int(request.POST['parent_id'])
                child_id = int(request.POST['child_id'])

                was_relation_added = Goal.add_relation(parent_id, child_id)

                if was_relation_added[0] is True:
                    goal_family = was_relation_added[1]
                    json_family = []
                    for member in goal_family:
                        json_family.append(jsonize_goal(member))

                    return HttpResponse(json.dumps(ResponseWrapper.of(json_family, ResponseWrapper.ARRAY_RESPONSE)))
                else:
                    return HttpResponse(json.dumps(ResponseWrapper.error(was_relation_added[1])))

            except (ValueError, TypeError, MultiValueDictKeyError):
                return HttpResponse(json.dumps(ResponseWrapper.error('Improper data')))
            except ObjectDoesNotExist:
                return HttpResponse(json.dumps(ResponseWrapper.error('Invalid id')))
        else:
            return HttpResponse(json.dumps(ResponseWrapper.error('Invalid request')))
    else:
        return HttpResponse(json.dumps(ResponseWrapper.error('Invalid session')))


@csrf_exempt
def remove_relation(request):
    if is_session_active(request.session):
        if request.method == 'POST':
            try:
                parent_id = int(request.POST['parent_id'])
                child_id = int(request.POST['child_id'])

                goal_families = Goal.remove_relation(parent_id, child_id)

                json_families = []
                for goal_family in goal_families:
                    json_family = []
                    for member in goal_family:
                        json_family.append(jsonize_goal(member))
                    json_families.append(json_family)

                return HttpResponse(
                    json.dumps(ResponseWrapper.of(json_families, ResponseWrapper.ARRAY_RESPONSE)))
            except (ValueError, TypeError, MultiValueDictKeyError):
                return HttpResponse(json.dumps(ResponseWrapper.error('Improper data')))
            except ObjectDoesNotExist:
                return HttpResponse(json.dumps(ResponseWrapper.error('Invalid id')))
        else:
            return HttpResponse(json.dumps(ResponseWrapper.error('Invalid request')))
    else:
        return HttpResponse(json.dumps(ResponseWrapper.error('Invalid session')))


@csrf_exempt
def toggle_is_achieved(request, pk):
    if is_session_active(request.session):
        if request.method == 'POST':
            try:
                is_saved = Goal.toggle_is_achieved(pk)
                if is_saved[0] is True:
                    return HttpResponse(json.dumps(ResponseWrapper.of(is_saved[1], ResponseWrapper.OBJECT_RESPONSE)))
                else:
                    return HttpResponse(json.dumps(ResponseWrapper.error(is_saved[1])))
            except ObjectDoesNotExist:
                return HttpResponse(json.dumps(ResponseWrapper.error('No goal with such id')))
        else:
            return HttpResponse(json.dumps(ResponseWrapper.error('Invalid request')))
    else:
        return HttpResponse(json.dumps(ResponseWrapper.error('Invalid session')))
