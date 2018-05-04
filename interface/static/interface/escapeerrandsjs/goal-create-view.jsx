/**
 * Dependencies:
 *  react.js
 *  react-dom.js
 *  babel.js
 * */

/**
 * @propFunctions: onGoalCreate
 * */
let GoalCreateView = React.createClass({
    render: function () {
        let style = this.props.isOpen === true ? {display: 'block'} : {display: 'none'};
        return (
            <div className="create-goal-view" ref="entireView" style={style}>
                <button className="btn white black-text z-depth-0 create-goal-view-hide-btn"
                        title="Create a goal"
                        onClick={(e) => {
                            $(this.refs.entireView).hide()
                        }}>
                    <i className="material-icons">close</i></button>
                <label>what?
                    <textarea ref="description" className="create-goal-view-description"></textarea>
                </label>
                <button className="btn-floating right purple darken-4 z-depth-1"
                        title="Fill deadline with current timestamp"
                        onClick={() => {
                            this.refs.deadline.value = TimeFormatter.formatNow()
                        }}>
                    <i className="material-icons">format_color_fill</i>
                </button>
                <label>by when?
                    <input ref="deadline" className="create-goal-view-deadline"
                           title="d -> day, m -> month, y -> year, h -> hour, t -> min, s -> sec, u -> microsec"
                           onDoubleClick={() => {
                               this.refs.deadline.value = TimeFormatter.formatNow()
                           }}/>
                </label>
                <button className="btn-floating right z-depth-1 create-goal-view-submit-btn"
                        title="Submit"
                        onClick={(e) => {
                            let description = this.refs.description.value;
                            let deadline = TimeFormatter.parse(this.refs.deadline.value);
                            if (deadline !== false) {
                                this.props.onGoalCreate(description, deadline);
                                this.refs.description.value = '';
                                this.refs.deadline.value = '';
                            }
                            else {
                                toastr.error('Incorrect datetime format, try again');
                            }
                        }}>
                    <i className="material-icons">send</i></button>
            </div>
        );
    }
});

/* Export */
window.GoalCreateView = GoalCreateView;
