from __future__ import unicode_literals

from django.db import models


class Goal(models.Model):
    class DeadlineUtils:
        @staticmethod
        def is_greater(d1, d2):
            if d1 is None and d2 is not None:
                return True
            if d1 is not None and d2 is not None and d1 > d2:
                return True
            return False

        @staticmethod
        def is_equal(d1, d2):
            if d1 is None and d2 is None:
                return True
            if d1 is not None and d2 is not None and d1 == d2:
                return True
            return False

        @staticmethod
        def is_lesser(d1, d2):
            return Goal.DeadlineUtils.is_greater(d2, d1)

    # Relational fields
    id = models.AutoField(primary_key=True)
    parents = models.ManyToManyField('Goal', related_name='children')
    # Core fields
    description = models.TextField(default='')
    deadline = models.DateTimeField(blank=True, null=True)
    is_achieved = models.BooleanField(default=False)
    # Auxiliary fields
    color = models.TextField(default='#000000')

    def __str__(self):
        return '{pk=%d, description=%s}' % (self.id, self.description)

    def save(self, force_insert=False, force_update=False, using=None, update_fields=None):
        is_valid = self.is_valid()
        if is_valid is True:
            super(Goal, self).save(force_insert=False, force_update=False, using=None, update_fields=None)
            return True
        else:
            return is_valid

    # ################ Validators ################
    def is_valid(self):
        """
        Checks the following
        1. is the family of the current goal acyclic
        2. are the deadlines of goals in current family consistent (child_deadline >= parent_deadline)
        3. is the achievement of the goals consistent (child should be achieved only after all its parents are achieved)
        4. and any other validation that was not documented (check code to ensure) :)
        :return: if (all above conditions met):
                    True
                 else:
                    False, "reason for failure"
        """
        is_deadline_valid = self.is_deadline_valid()
        if is_deadline_valid is True:
            is_is_achieved_valid = self.is_is_achieved_valid()
            if is_is_achieved_valid is True:
                is_acyclically_valid = self.is_acyclically_valid()
                if is_acyclically_valid is True:
                    return True
                else:
                    error_message = is_acyclically_valid[1]
            else:
                error_message = is_is_achieved_valid[1]
        else:
            error_message = is_deadline_valid[1]

        return False, error_message

    def is_deadline_valid(self):
        # parent deadline should be < child deadline for a valid relation
        if self.id is not None:
            for parent in self.get_parents():
                if Goal.DeadlineUtils.is_greater(parent.deadline, self.deadline):
                    return False, 'Deadline before parent'

            for child in self.get_children():
                if Goal.DeadlineUtils.is_greater(self.deadline, child.deadline):
                    return False, 'Deadline after child'

            # No objection -> deadline valid
            return True
        else:
            return True

    def is_is_achieved_valid(self):
        if self.id is not None:
            if self.is_achieved is True:
                for parent in self.get_parents():
                    if parent.is_achieved is False:
                        return False, 'This goal is achieved before its parent'
            elif self.is_achieved is False:
                for child in self.get_children():
                    if child.is_achieved is True:
                        return False, 'Child goal is achieved before this'

            # No objection -> is_achieved valid
            return True
        else:
            return True

    @staticmethod
    def _cycle_exists(node, origin_id, is_node_a_root=True):
        if not is_node_a_root and node.id == origin_id:
            return True
        else:
            for child in node.get_children().all():
                if Goal._cycle_exists(child, origin_id, False) is True:
                    return True

    def is_acyclically_valid(self):
        if self.id is not None:
            if Goal._cycle_exists(self, self.id, True) is True:
                return False, 'Forms cycle'
            else:
                return True
        else:
            return True

    # ################ Getters & Setters ################

    # ######## Parent ########
    def get_parents(self):
        return self.parents.all()

    def add_parent(self, parent):
        self.parents.add(parent)
        is_valid = self.is_valid()
        if is_valid is not True:
            self.parents.remove(parent)
        return is_valid

    def remove_parent(self, parent):
        self.parents.remove(parent)

    # ######## Child ########
    def get_children(self):
        return self.children.all()

    def add_child(self, child):
        self.children.add(child)
        is_valid = self.is_valid()
        if is_valid is not True:
            self.children.remove(child)
        return is_valid

    def remove_child(self, child):
        self.children.remove(child)

    # ######## Family ########
    @staticmethod
    def _generate_family_ids_set(node, family_ids_result_set):
        family_ids_result_set.add(node.id)
        for parent in node.get_parents():
            if parent.id not in family_ids_result_set:
                Goal._generate_family_ids_set(parent, family_ids_result_set)

        for child in node.get_children():
            if child.id not in family_ids_result_set:
                Goal._generate_family_ids_set(child, family_ids_result_set)

    def get_family_id_set(self):
        family_ids_result_set = set()
        Goal._generate_family_ids_set(self, family_ids_result_set)
        return family_ids_result_set
