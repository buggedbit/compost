/**
 * Dependencies:
 *  react.js
 *  react-dom.js
 *  babel.js
 * */

let GoalGlanceView = React.createClass({
    getInitialState: function () {
        return {
            society: [],
            goalDetailView: {
                id: undefined,
                description: undefined,
                deadline: undefined,
                isAchieved: undefined,
                isOpen: undefined,
            },
            goalCreateView: {
                isOpen: undefined,
            },
        };
    },

    // state methods
    // does not validate params
    // WARNING: each state method may change graph before returning
    //          most probably you don't want to use a state method in a loop or in consecutive repetition
    s_pointToGoalInSociety: function (goalId) {
        let answer = [false, -1, -1];
        // little redundant
        // check continues even after finding the goal
        // but clean code
        // anyways society wont be so big to cause any real change
        for (let i = 0; i < this.state.society.length; i++) {
            let family = this.state.society[i];
            for (let j = 0; j < family.length; j++) {
                let goal = family[j];
                if (goal.id === goalId) {
                    answer = [true, i, j];
                    return answer;
                }
            }
        }
        return answer;
    },
    s_setSocietyToFamily: function (family) {
        this.setState((prevState, props) => {
            let society = [family];
            return {society: society};
        });
    },
    s_addFamily: function (family) {
        this.setState((prevState, props) => {
            let society = prevState.society;
            society.push(family);
            return {society: society};
        });
    },
    s_updateGoalInSociety: function (goalId, goal) {
        let goalIndex = this.s_pointToGoalInSociety(goalId);
        if (goalIndex[0]) {
            this.setState((prevState, props) => {
                let society = prevState.society;
                society[goalIndex[1]][goalIndex[2]] = goal;
                return {society: society};
            });
        }
    },
    s_setGoalAchievement: function (goalId, isAchieved) {
        let goalPos = this.s_pointToGoalInSociety(goalId);
        if (goalPos[0]) {
            this.setState((prevState, props) => {
                let society = prevState.society;
                society[goalPos[1]][goalPos[2]].is_achieved = isAchieved;
                return {society: society};
            });
        }
    },
    s_removeFamilyIfExists: function (goalId) {
        let goalPos = this.s_pointToGoalInSociety(goalId);
        if (goalPos[0]) {
            this.setState((prevState, props) => {
                let society = prevState.society;
                society.splice(goalPos[1], 1);
                return {society: society};
            });
        }
    },
    s_makeRelation: function (parentId, childId, newFamily) {
        let removeFamilyIfExists = function (society, goalId) {
            let familyIndex = -1;
            society.forEach((family, fi) => {
                family.forEach((goal, gi) => {
                    if (goal.id === goalId) {
                        familyIndex = fi;
                    }
                });
            });
            if (familyIndex > -1)
                society.splice(familyIndex, 1);
        };
        this.setState((prevState, props) => {
            let society = prevState.society;
            removeFamilyIfExists(society, parentId);
            removeFamilyIfExists(society, childId);
            society.push(newFamily);
            return {society: society};
        });
    },
    s_breakRelation: function (parentId, childId, setOfNewFamilies) {
        let removeFamilyIfExists = function (society, goalId) {
            let familyIndex = -1;
            society.forEach((family, fi) => {
                family.forEach((goal, gi) => {
                    if (goal.id === goalId) {
                        familyIndex = fi;
                    }
                });
            });
            if (familyIndex > -1)
                society.splice(familyIndex, 1);
        };
        this.setState((prevState) => {
            let society = prevState.society;
            removeFamilyIfExists(society, parentId);
            removeFamilyIfExists(society, childId);
            society.push(setOfNewFamilies[0]);
            // if by breaking relation the family was split into two
            if (setOfNewFamilies.length === 2) {
                society.push(setOfNewFamilies[1]);
            }
            return {society: society};
        });
    },
    s_clearSociety: function () {
        this.setState((prevState, props) => {
            let society = [];
            return {society: society};
        });
    },
    s_openGoalDetailView: function (goalId) {
        let goalPos = this.s_pointToGoalInSociety(goalId);
        if (goalPos[0]) {
            this.setState((prevState, props) => {
                let society = prevState.society;
                let goalDetailView = prevState.goalDetailView;
                let goalToBeOpened = society[goalPos[1]][goalPos[2]];
                goalDetailView.id = goalToBeOpened.id;
                goalDetailView.description = goalToBeOpened.description;
                goalDetailView.deadline = goalToBeOpened.deadline;
                goalDetailView.isAchieved = goalToBeOpened.isAchieved;
                goalDetailView.isOpen = true;
                return {goalDetailView: goalDetailView};
            });
        }
    },
    s_closeGoalDetailView: function () {
        this.setState((prevState, props) => {
            let goalDetailView = prevState.goalDetailView;
            goalDetailView.isOpen = false;
            return {goalDetailView: goalDetailView};
        });
    },
    s_toggleGoalCreateView: function () {
        this.setState((prevState, props) => {
            let goalCreateView = prevState.goalCreateView;
            goalCreateView.isOpen = !goalCreateView.isOpen;
            return {goalCreateView: goalCreateView};
        });
    },

    // api
    // validate params
    // does ajax calls if needed
    // use state methods for CUD operations
    // preferably use these to state methods
    api_softSelectFamilyOfGoal: function (goalId, force_select = false) {
        goalId = Number(goalId);
        if (force_select || !this.s_pointToGoalInSociety(goalId)[0]) {
            let self = this;
            $.post(this.props.readFamilyUrl.replace('1729', String(goalId))
            ).done((r) => {
                let json = JSON.parse(r);
                if (json.status === -1) {
                    toastr.error(json.error);
                } else {
                    let family = json.data;
                    self.s_addFamily(family);
                }
            }).fail(() => {
                toastr.error('Server Error');
            });
        }
    },
    api_hardSelectFamilyOfGoal: function (goalId) {
        goalId = Number(goalId);
        if (!this.s_pointToGoalInSociety(goalId)[0]) {
            let self = this;
            $.post(this.props.readFamilyUrl.replace('1729', String(goalId))
            ).done((r) => {
                let json = JSON.parse(r);
                if (json.status === -1) {
                    toastr.error(json.error);
                }
                else {
                    let family = json.data;
                    self.s_setSocietyToFamily(family);
                    self.s_openGoalDetailView(goalId);
                }
            }).fail(() => {
                toastr.error('Server Error');
            });
        }
    },
    api_deselectFamilyOfGoal: function (goalId) {
        goalId = Number(goalId);
        this.s_removeFamilyIfExists(goalId);
        this.s_closeGoalDetailView();
    },
    api_makeRelation: function (parentId, childId) {
        parentId = Number(parentId);
        childId = Number(childId);
        if (this.s_pointToGoalInSociety(parentId)[0] && this.s_pointToGoalInSociety(childId)[0]) {
            let self = this;
            $.post(this.props.addRelationUrl, {
                parent_id: parentId,
                child_id: childId
            }).done((r) => {
                let json = JSON.parse(r);
                if (json.status === -1) {
                    toastr.error(json.error);
                } else {
                    let newFamily = json.data;
                    self.s_makeRelation(parentId, childId, newFamily);
                }
            }).fail(() => {
                toastr.error('Server Error');
            });
        }
    },
    api_breakRelation: function (parentId, childId) {
        parentId = Number(parentId);
        childId = Number(childId);
        if (this.s_pointToGoalInSociety(parentId)[0] && this.s_pointToGoalInSociety(childId)[0]) {
            let self = this;
            $.post(this.props.removeRelationUrl, {
                parent_id: parentId,
                child_id: childId
            }).done((r) => {
                let json = JSON.parse(r);
                if (json.status === -1) {
                    toastr.error(json.error);
                } else {
                    let setOfNewFamilies = json.data;
                    self.s_breakRelation(parentId, childId, setOfNewFamilies);
                }
            }).fail(() => {
                toastr.error('Server Error');
            });
        }
    },
    api_clearCanvas: function () {
        this.s_clearSociety();
    },
    api_createGoal: function (description, deadline) {
        let self = this;
        $.post(this.props.createUrl, {
            description: description,
            deadline: JSON.stringify(deadline),
        }).done((r) => {
            let json = JSON.parse(r);
            if (json.status === -1) {
                toastr.error(json.error);
            } else {
                self.s_addFamily([json.data]);
            }
        }).fail(() => {
            toastr.error('Server Error');
        });
    },
    api_updateGoal: function (goalId, description, deadline) {
        goalId = Number(goalId);
        let self = this;
        if (this.s_pointToGoalInSociety(goalId)[0]) {
            $.post(this.props.updateUrl, {
                id: goalId,
                description: description,
                deadline: JSON.stringify(deadline)
            }).done((r) => {
                let json = JSON.parse(r);
                if (json.status === -1) {
                    toastr.error(json.error);
                } else {
                    self.s_updateGoalInSociety(goalId, json.data);
                    self.s_openGoalDetailView(goalId);
                }
            }).fail(() => {
                toastr.error('Server Error');
            });
        }
    },
    api_chainUpdateGoal: function (goalId, description, deadline) {
        goalId = Number(goalId);
        let self = this;
        if (this.s_pointToGoalInSociety(goalId)[0]) {
            $.post(this.props.chainUpdateUrl, {
                id: goalId,
                description: description,
                deadline: JSON.stringify(deadline)
            }).done((r) => {
                let json = JSON.parse(r);
                if (json.status === -1) {
                    toastr.error(json.error);
                } else {
                    // todo update whole family here
                    self.api_deselectFamilyOfGoal(goalId);
                    self.api_softSelectFamilyOfGoal(goalId, true);
                }
            }).fail(() => {
                toastr.error('Server Error');
            });
        }
    },
    api_toggleGoalAchievement: function (goalId) {
        goalId = Number(goalId);
        if (this.s_pointToGoalInSociety(goalId)[0]) {
            let self = this;
            $.post(this.props.toggleIsAchievedUrl.replace('1729', goalId)
            ).done((r) => {
                let json = JSON.parse(r);
                if (json.status === -1) {
                    toastr.error(json.error);
                } else {
                    self.s_setGoalAchievement(goalId, json.data);
                }
            }).fail(() => {
                toastr.error('Server Error');
            });
        }
    },
    api_setGoalAchievement: function (goalId, isAchieved) {
        goalId = Number(goalId);
        this.s_setGoalAchievement(goalId, isAchieved);
    },
    api_deleteGoalIfSingle: function (goalId) {
        goalId = Number(goalId);
        let self = this;
        if (this.s_pointToGoalInSociety(goalId)[0]) {
            $.post(this.props.deleteIfSingleUrl, {
                id: goalId
            }).done((r) => {
                let json = JSON.parse(r);
                if (json.status === -1) {
                    toastr.error(json.error);
                } else {
                    self.s_removeFamilyIfExists(goalId);
                    self.s_closeGoalDetailView();
                }
            }).fail(() => {
                toastr.error('Server Error');
            });
        }
    },
    api_openGoalDetailView: function (goalId) {
        goalId = Number(goalId);
        this.s_openGoalDetailView(goalId);
    },
    api_closeGoalDetailView: function () {
        this.s_closeGoalDetailView();
    },
    api_toggleGoalCreateView: function () {
        this.s_toggleGoalCreateView();
    },

    render: function () {
        return (
            <div>
                <GoalSearchView
                    readRegexUrl={this.props.readRegexUrl}
                    toggleGoalIsAchievedUrl={this.props.toggleIsAchievedUrl}
                    onToggleGoalAchievement={this.api_setGoalAchievement}
                    onGoalSelect={this.api_hardSelectFamilyOfGoal}/>
                <GoalCanvasController society={this.state.society}
                                      onGoalDrop={this.api_softSelectFamilyOfGoal}
                                      onEmptySpaceClick={this.api_closeGoalDetailView}
                                      onGoalSelect={this.api_openGoalDetailView}
                                      onRelationSelect={() => {
                                      }}
                                      onDoubleClick={() => {
                                      }}
                                      onGoalDoubleClick={(goalId) => {
                                      }}
                                      onEdgeDoubleClick={this.api_breakRelation}
                                      onEmptySpaceDoubleClick={this.api_toggleGoalCreateView}
                                      onTwoGoalsConsecutiveDoubleClick={this.api_makeRelation}
                                      onGoalContext={this.api_toggleGoalAchievement}
                                      onRelationContext={() => {
                                      }}
                                      onGoalShowPopup={() => {
                                      }}/>
                <button className="btn-floating red z-depth-0 goal-canvas-clear-btn"
                        title="Clear canvas"
                        onClick={this.api_clearCanvas}>
                    <i className="material-icons">clear</i></button>
                <GoalDetailView
                    id={this.state.goalDetailView.id}
                    description={this.state.goalDetailView.description}
                    deadline={this.state.goalDetailView.deadline}
                    isAchieved={this.state.goalDetailView.is_achieved}
                    onGoalFamilyDeselect={this.api_deselectFamilyOfGoal}
                    onGoalUpdate={this.api_updateGoal}
                    onGoalChainUpdate={this.api_chainUpdateGoal}
                    onGoalDelete={this.api_deleteGoalIfSingle}
                    isOpen={this.state.goalDetailView.isOpen}/>
                <GoalCreateView
                    onGoalCreate={this.api_createGoal}
                    isOpen={this.state.goalCreateView.isOpen}/>
                <button className="btn-floating green z-depth-0 create-goal-view-toggle-btn"
                        title="Create a goal"
                        onClick={this.api_toggleGoalCreateView}>
                    <i className="material-icons">add</i></button>
            </div>
        );
    },
});

/* Export */
window.GoalGlanceView = GoalGlanceView;
