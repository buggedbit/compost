/**
 * Dependencies:
 *  react.js
 *  react-dom.js
 *  babel.js
 *  utils.jsx
 * */

/**
 * @propFunctions: onDescriptionClick, onDeadlineClick, onAchievementClick
 * */
let GoalSearchResult = React.createClass({
        deadlineRepr: function (deadline) {
            if (deadline === null) {
                return ".";
            }
            else {
                return moment()
                    .year(deadline.year)
                    .month(deadline.month - 1)
                    .date(deadline.day)
                    .hour(deadline.hour)
                    .minute(deadline.minute)
                    .second(deadline.second)
                    .millisecond(deadline.microsecond / 1000).fromNow();
            }
        },
        urgencyColor: function (deadline, isAchived) {
            if (isAchived) {
                return '#9e9e9e'
            }
            if (deadline === null) {
                return '#ffffff';
            }
            let duration = moment.duration(moment()
                .year(deadline.year)
                .month(deadline.month - 1)
                .date(deadline.day)
                .hour(deadline.hour)
                .minute(deadline.minute)
                .second(deadline.second)
                .millisecond(deadline.microsecond / 1000).diff(moment()));
            let noHours = duration.as('hours');
            if (noHours <= 0) {
                return '#ffebee'
            } else if (noHours > 0 && noHours < 24) {
                return '#fff8e1'
            } else {
                return '#ffffff'
            }
        },
        render: function () {
            let isAchieved = 'crop_square';
            if (this.props.isAchieved === true)
                isAchieved = 'done';
            let description = this.props.description === '' ? '@no_description' : truncate(this.props.description, this.props.maxDescLength);
            let deadline = this.deadlineRepr(this.props.deadline);
            let color = this.urgencyColor(this.props.deadline, this.props.isAchieved);

            return (
                <li draggable="true"
                    className="collection-item goal-search-result"
                    id={this.props.id}
                    title={this.props.description}
                    onDragStart={(e) => {
                        e.dataTransfer.setData('goalId', this.props.id);
                    }}>
                    <div className="row">
                        <div className="col l5 m5 s5 pointer data"
                             style={{
                                 color: this.props.color,
                             }}
                             onClick={(e) => {
                                 this.props.onDescriptionClick(this.props.id);
                             }}>
                            {description}
                        </div>
                        <div className="col l5 m5 s5 red-text pointer data"
                             style={{backgroundColor: color}}
                             onClick={(e) => {
                                 this.props.onDeadlineClick(this.props.id);
                             }}>
                            {deadline}
                        </div>
                        <div className="col l2 m2 s2 pointer data"
                             style={{paddingBottom: '0px'}}
                             onClick={(e) => {
                                 this.props.onAchievementClick(this.props.id);
                             }}>
                            <i className="material-icons">{isAchieved}</i>
                        </div>
                    </div>
                </li>
            );
        }
    })
;

/**
 * @propFunctions: onGoalSelect, setGoalAchievement, setResultSet
 * */
let GoalSearchView = React.createClass({
    maxDescLength: 15,
    onRegexInputKeyDown: function (e) {
        let self = this;
        switch (e.keyCode || e.which) {
            // Enter Key
            case 13:
                let achievedAlso = this.refs.achievedAlso.checked === true ? 1 : 0;
                $.post(this.props.readRegexUrl, {
                    regex: e.target.value,
                    is_global_search: achievedAlso,
                }).done((r) => {
                    let json = JSON.parse(r);
                    if (json.status === -1) {
                        toastr.error(json.error);
                    } else {
                        self.props.setResultSet(json.data);
                        $(self.refs.resultSetContainer).show();
                    }
                }).fail(() => {
                    toastr.error('Server Error');
                });
                break;
            // Escape key
            case 27:
                e.preventDefault();
                $(this.refs.resultSetContainer).hide();
                break;
            default:
                break;
        }
    },
    onToggleGoalAchievement: function (id) {
        let self = this;
        $.post(this.props.toggleGoalIsAchievedUrl.replace('1729', id)
        ).done((r) => {
            let json = JSON.parse(r);
            if (json.status === -1) {
                toastr.error(json.error);
            } else {
                let isAchieved = json.data;
                // call back to parent
                self.props.setGoalAchievement(id, isAchieved);
            }
        }).fail(() => {
            toastr.error('Server Error');
        });
    },
    render: function () {
        let resultSet = this.props.resultSet.map((goalFamilySubset) => {
            let familyKey = [];
            let goalFamilySubsetView = goalFamilySubset.map((goal) => {
                familyKey.push(goal.id);
                return (
                    <GoalSearchResult
                        key={goal.id}
                        id={goal.id}
                        description={goal.description}
                        deadline={goal.deadline}
                        isAchieved={goal.is_achieved}
                        color={goal.color}

                        maxDescLength={this.maxDescLength}

                        onAchievementClick={this.onToggleGoalAchievement}
                        onDescriptionClick={this.props.onGoalSelect}
                        onDeadlineClick={this.props.onGoalSelect}/>
                );
            });
            return (
                <ul key={familyKey.join()} className="collection goal-search-family-subset">
                    {goalFamilySubsetView}
                </ul>
            );
        });
        let resultSetContainerStyle = {display: 'none'};
        return (
            <div ref="entireView" className="goal-search-app z-depth-1">
                <label>
                    <input className="goal-search-input"
                           ref="input"
                           autoFocus
                           onKeyDown={this.onRegexInputKeyDown}
                           onDoubleClick={(e) => {
                               $(this.refs.resultSetContainer).toggle();
                           }}
                           title="Search"
                           type="text"/>
                </label>
                <span className="goal-search-all-inclusive-checkbox"
                      onClick={(e) => {
                          $(this.refs.input).focus();
                      }}>
                                <input type="checkbox" ref="achievedAlso" className="filled-in"
                                       id="this-is-a-really-long-id-just-in-case-that-it-does-not-collide-with-some-other-id-that-would-come-in-future"/>
                                <label
                                    htmlFor="this-is-a-really-long-id-just-in-case-that-it-does-not-collide-with-some-other-id-that-would-come-in-future">
                                    <i className="material-icons">all_inclusive</i>
                                </label>
                            </span>
                <div ref="resultSetContainer"
                     className="goal-search-result-set-container"
                     style={resultSetContainerStyle}>
                    <ul className="collection goal-search-result-set">
                        {resultSet.length === 0 ? (
                            <li className="collection-item red white-text">No matching goals</li>
                        ) : resultSet}
                    </ul>
                </div>
            </div>
        );
    },
    componentDidMount: function () {
        let self = this;
        $(document).click(function (e) {
            let $entireView = $(self.refs.entireView);
            let $resultSetContainer = $(self.refs.resultSetContainer);
            // if the target of the click isn't the container nor a descendant of the container
            if (!$entireView.is(e.target) && $entireView.has(e.target).length === 0) {
                $resultSetContainer.hide();
            }
        });
    },
});

/* Export */
window.GoalSearchView = GoalSearchView;
