/**
 * Dependencies:
 *  react.js
 *  react-dom.js
 *  babel.js
 * */

/**
 * @propFunctions: onGoalUpdate, onGoalDelete, onGoalFamilyDeselect
 * */
let GoalDetailView = React.createClass({
    render: function () {
        let style = this.props.isOpen === true ? {display: 'block'} : {display: 'none'};
        return (
            <div className="goal-detail-view" ref="entireView"
                 style={style}>
                <button className="btn white black-text z-depth-0 goal-detail-view-hide-btn"
                        title="Close"
                        onClick={(e) => {
                            $(this.refs.entireView).hide()
                        }}>
                    <i className="material-icons">close</i></button>
                <label>Description
                    <textarea className="goal-detail-view-description"
                              defaultValue={this.props.description === undefined ? '' : this.props.description}
                              ref="description">
                </textarea>
                </label>
                <label>
                    Deadline: t -> minute, m -> month, u -> microsecond
                    <input className="goal-detail-view-deadline"
                           ref="deadline"
                           defaultValue={this.props.deadline === undefined ? '' : TimeFormatter.format(this.props.deadline)}
                           onDoubleClick={(e) => {
                               e.target.value = TimeFormatter.formatNow()
                           }}/>
                </label>
                <button className="btn-floating blue z-depth-0 goal-detail-view-update-btn"
                        title="Update"
                        onClick={(e) => {
                            let id = this.props.id;
                            let description = this.refs.description.value;
                            let deadline = TimeFormatter.parse(this.refs.deadline.value);
                            if (deadline !== false) {
                                this.props.onGoalUpdate(id, description, deadline);
                            }
                            else {
                                toastr.error('Incorrect datetime format, try again');
                            }
                        }}>
                    <i className="material-icons">sync</i></button>
                <button className="btn-floating black z-depth-0 goal-detail-view-deselect-btn"
                        title="Remove from canvas"
                        onClick={(e) => {
                            this.props.onGoalFamilyDeselect(this.props.id)
                        }}>
                    <i className="material-icons">vertical_align_bottom</i></button>
                <button className="btn-floating red z-depth-0 goal-detail-view-delete-btn"
                        title="Delete"
                        onClick={(e) => {
                            this.props.onGoalDelete(this.props.id);
                        }}>
                    <i className="material-icons">delete</i></button>
            </div>
        );
    },
    componentDidUpdate: function () {
        this.refs.description.value = this.props.description === undefined ? '' : this.props.description;
        this.refs.deadline.value = this.props.deadline === undefined ? '' : TimeFormatter.format(this.props.deadline);
    },
});

/* Export */
window.GoalDetailView = GoalDetailView;
