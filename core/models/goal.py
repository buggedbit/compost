from __future__ import unicode_literals

from django.db import models


class Goal(models.Model):
    # Relational fields
    id = models.AutoField(primary_key=True)
    parents = models.ManyToManyField('Goal', related_name='children')
    # Core fields
    description = models.TextField(default='')
    deadline = models.DateTimeField(blank=True, null=True)
    is_achieved = models.BooleanField(default=False)
    # Auxiliary fields
    color = models.CharField(max_length=7, default='#000000')

    def save(self, force_insert=False, force_update=False, using=None, update_fields=None):
        is_valid = self.is_valid()
        if is_valid is True:
            super(Goal, self).save(force_insert=False, force_update=False, using=None, update_fields=None)
            return True
        else:
            return is_valid

    def is_valid(self):
        """
        Checks the following conditions
        1. is the family of the current goal acyclic
        2. are the deadlines of goals in current family proper (child_deadline >= parent_deadline)
        3. is the achievement of the goals proper (child achieved only after all its parents achieved)
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

    @classmethod
    def set_deadline_for_child_chain(cls, goal, deadline):
        for child in goal.get_children():
            # pruning
            if child.deadline < deadline:
                cls.set_deadline_for_child_chain(child, deadline)

        goal.deadline = deadline
        goal.save()

    @classmethod
    def set_deadline_for_parent_chain(cls, goal, deadline):
        for parent in goal.get_parents():
            # pruning
            if parent.deadline > deadline:
                cls.set_deadline_for_parent_chain(parent, deadline)

        goal.deadline = deadline
        goal.save()

    def is_deadline_valid(self):
        # Not saved yet
        if self.id is not None:
            for parent in self.get_parents():
                if self.deadline is None and parent.deadline is None:
                    continue
                if self.deadline is None and parent.deadline is not None:
                    continue
                if self.deadline is not None and parent.deadline is None:
                    return False, 'Deadline before parent'
                if self.deadline is not None and parent.deadline is not None and self.deadline < parent.deadline:
                    return False, 'Deadline before parent'

            for child in self.get_children():
                if self.deadline is None and child.deadline is None:
                    continue
                if self.deadline is None and child.deadline is not None:
                    return False, 'Deadline after child'
                if self.deadline is not None and child.deadline is None:
                    continue
                if self.deadline is not None and child.deadline is not None and self.deadline > child.deadline:
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

    @classmethod
    def _dfs_for_checking_cycles(cls, node, origin_id, at_root=True):
        if not at_root and node.id == origin_id:
            return True
        else:
            for child in node.get_children().all():
                if cls._dfs_for_checking_cycles(child, origin_id, False) is True:
                    return True

    def is_acyclically_valid(self):
        if self.id is not None:
            if Goal._dfs_for_checking_cycles(self, self.id, True) is not True:
                return True
            else:
                return False, 'Forms cycle'
        else:
            return True

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

    @classmethod
    def _dfs_for_family(cls, node, family):
        family.add(node.id)
        for parent in node.get_parents():
            if parent.id not in family:
                cls._dfs_for_family(parent, family)

        for child in node.get_children():
            if child.id not in family:
                cls._dfs_for_family(child, family)

    def get_family_id_set(self):
        family = set()
        Goal._dfs_for_family(self, family)
        return family

    def __str__(self):
        return str(self.id) + ' ' + self.description
