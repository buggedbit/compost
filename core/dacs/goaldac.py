from core.models.goal import Goal
import re


# Data access class
class GoalDAC:
    @staticmethod
    def _group_by_family(goal_iterable):
        goal_family_subset_list = []
        augmented_goal_list = []
        # add picked flag to each goal
        for goal in goal_iterable:
            augmented_goal_list.append([goal, False])

        for i in range(len(augmented_goal_list)):
            # ith goal not picked
            if not augmented_goal_list[i][1]:
                # pick this goal
                augmented_goal_list[i][1] = True
                ith_goal_family_subset = [augmented_goal_list[i][0]]
                ith_goal_family_id_set = augmented_goal_list[i][0].get_family_id_set()
                for j in range(i + 1, len(augmented_goal_list)):
                    # if jth goal (i < j < N) belongs to family of ith goal and is not picked
                    if not augmented_goal_list[j][1] and augmented_goal_list[j][0].id in ith_goal_family_id_set:
                        # pick this goal
                        augmented_goal_list[j][1] = True
                        ith_goal_family_subset.append(augmented_goal_list[j][0])
                # append ith goal family subset
                goal_family_subset_list.append(ith_goal_family_subset)

        return goal_family_subset_list

    @staticmethod
    def _push_deadline_nulls_to_last(goal_iterable):
        goals_count = len(goal_iterable)
        nulls_last_goals_list = [None for i in range(goals_count)]
        next_index_from_start = 0
        next_index_from_end = goals_count - 1
        for i in range(goals_count):
            if goal_iterable[i].deadline is not None:
                nulls_last_goals_list[next_index_from_start] = goal_iterable[i]
                next_index_from_start = next_index_from_start + 1
            else:
                nulls_last_goals_list[next_index_from_end] = goal_iterable[i]
                next_index_from_end = next_index_from_end - 1
        return nulls_last_goals_list

    @staticmethod
    def read_regex(regex, is_global_search):
        """
        :return: matched goals sorted wrt deadline with earliest deadline first, all non-deadlines at last grouped by
            family
        """
        re.compile(regex)
        if is_global_search == '1':
            not_achieved_goals = Goal.objects.filter(description__iregex=regex,
                                                     is_achieved__exact=False).order_by('deadline')
            achieved_goals = Goal.objects.filter(description__iregex=regex,
                                                 is_achieved__exact=True).order_by('deadline')
            not_achieved_goals = GoalDAC._push_deadline_nulls_to_last(not_achieved_goals)
            achieved_goals = GoalDAC._push_deadline_nulls_to_last(achieved_goals)
            matched_goals_query_set = not_achieved_goals + achieved_goals
        else:
            matched_goals_query_set = Goal.objects.filter(description__iregex=regex,
                                                          is_achieved__exact=False).order_by('deadline')
            matched_goals_query_set = GoalDAC._push_deadline_nulls_to_last(matched_goals_query_set)

        return GoalDAC._group_by_family(matched_goals_query_set)

    @staticmethod
    def read_family(pk):
        goal = Goal.objects.get(pk=pk)
        family_of_ids = goal.get_family_id_set()
        goal_family = []
        for member_id in family_of_ids:
            goal_family.append(Goal.objects.get(pk=member_id))
        return goal_family

    @staticmethod
    def create(description, deadline, color):
        new_goal = Goal(description=description, deadline=deadline, color=color)
        is_created = new_goal.save()
        if is_created is True:
            return True, new_goal
        else:
            return is_created

    @staticmethod
    def update(pk, description, deadline, color):
        existing_goal = Goal.objects.get(pk=pk)
        existing_goal.description = description
        existing_goal.deadline = deadline
        existing_goal.color = color
        is_updated = existing_goal.save()
        if is_updated is True:
            return True, existing_goal
        else:
            return is_updated

    @staticmethod
    def _set_child_chain_deadlines(goal, deadline):
        for child in goal.get_children():
            # pruning
            if Goal.DeadlineUtils.is_lesser(child.deadline, deadline):
                GoalDAC._set_child_chain_deadlines(child, deadline)

        goal.deadline = deadline
        goal.save()

    @staticmethod
    def _set_parent_chain_deadlines(goal, deadline):
        for parent in goal.get_parents():
            # pruning
            if Goal.DeadlineUtils.is_greater(parent.deadline, deadline):
                GoalDAC._set_parent_chain_deadlines(parent, deadline)

        goal.deadline = deadline
        goal.save()

    @staticmethod
    def chain_update(pk, description, deadline, color):
        # fixme: handle error after partial save or implement rollback
        existing_goal = Goal.objects.get(pk=pk)
        existing_goal.description = description
        existing_goal.color = color
        GoalDAC._set_child_chain_deadlines(existing_goal, deadline)
        GoalDAC._set_parent_chain_deadlines(existing_goal, deadline)
        is_updated = existing_goal.save()
        if is_updated is True:
            family_of_ids = existing_goal.get_family_id_set()
            goal_family = []
            for member_id in family_of_ids:
                goal_family.append(Goal.objects.get(pk=member_id))
            return True, goal_family
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
            goal_family = []
            for member_id in new_family_id_set:
                goal_family.append(Goal.objects.get(pk=member_id))
            return True, goal_family
        else:
            return was_relation_added

    @staticmethod
    def remove_relation(parent_id, child_id):
        parent = Goal.objects.get(pk=parent_id)
        child = Goal.objects.get(pk=child_id)
        parent.remove_child(child)

        parent_family_id_set = parent.get_family_id_set()
        id_sets_after_breakup = [parent_family_id_set]
        if child_id not in parent_family_id_set:
            child_family_id_set = child.get_family_id_set()
            id_sets_after_breakup.append(child_family_id_set)

        goal_families = []
        for id_set in id_sets_after_breakup:
            family = []
            for member_id in id_set:
                family.append(Goal.objects.get(pk=member_id))
            goal_families.append(family)

        return goal_families

    @staticmethod
    def toggle_is_achieved(pk):
        goal = Goal.objects.get(pk=pk)
        goal.is_achieved = not goal.is_achieved
        is_saved = goal.save()
        if is_saved is True:
            return True, goal.is_achieved
        else:
            return is_saved
