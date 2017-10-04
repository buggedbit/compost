from core.models.goal import Goal
import re


class GoalDAC:
    @staticmethod
    def read_regex(regex, global_search):
        re.compile(regex)
        if global_search == '1':
            matched_goals = Goal.objects.filter(description__iregex=regex).order_by('is_achieved', 'deadline')
        else:
            matched_goals = Goal.objects.filter(description__iregex=regex,
                                                is_achieved__exact=False).order_by('is_achieved', 'deadline')
        return matched_goals

    @staticmethod
    def read_family(pk):
        goal = Goal.objects.get(pk=pk)
        family_of_ids = goal.get_family_id_set()
        goal_family = []
        for member_id in family_of_ids:
            goal_family.append(Goal.objects.get(pk=member_id))
        return goal_family

    @staticmethod
    def create(description, deadline):
        new_goal = Goal(description=description, deadline=deadline)
        is_created = new_goal.save()
        if is_created is True:
            return True, new_goal
        else:
            return is_created

    @staticmethod
    def update(pk, description, deadline):
        existing_goal = Goal.objects.get(pk=pk)
        existing_goal.description = description
        existing_goal.deadline = deadline
        is_updated = existing_goal.save()
        if is_updated is True:
            return True, existing_goal
        else:
            return is_updated

    @staticmethod
    def delete_if_single(pk):
        existing_goal = Goal.objects.get(pk=pk)
        if len(existing_goal.get_parents()) == 0 and len(existing_goal.get_children()) == 0:
            existing_goal.delete()
            return True
        else:
            return False, 'Goal is not single'

    @staticmethod
    def add_relation(parent_id, child_id):
        parent = Goal.objects.get(pk=parent_id)
        child = Goal.objects.get(pk=child_id)
        was_relation_added = parent.add_child(child)

        if was_relation_added is True:
            new_family_id_set = parent.get_family_id_set()
            return True, new_family_id_set
        else:
            return was_relation_added

    @staticmethod
    def remove_relation(parent_id, child_id):
        parent = Goal.objects.get(pk=parent_id)
        child = Goal.objects.get(pk=child_id)
        parent.remove_child(child)

        parent_family_id_set = parent.get_family_id_set()
        family_id_sets_after_breakup = [parent_family_id_set]
        if child_id not in parent_family_id_set:
            child_family_id_set = child.get_family_id_set()
            family_id_sets_after_breakup.append(child_family_id_set)
        return family_id_sets_after_breakup

    @staticmethod
    def toggle_is_achieved(pk):
        goal = Goal.objects.get(pk=pk)
        goal.is_achieved = not goal.is_achieved
        is_saved = goal.save()
        if is_saved is True:
            return True, goal.is_achieved
        else:
            return is_saved

    def __init__(self):
        pass
