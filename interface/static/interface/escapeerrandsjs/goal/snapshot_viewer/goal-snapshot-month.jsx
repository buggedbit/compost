/**
 * Dependencies:
 *  react.js
 *  react-dom.js
 *  babel.js
 *  utils.jsx
 * */

/**
 * @propFunctions:
 * */
let GoalSnapshotDay = React.createClass({
    getDefaultProps: function () {
        return {
            day: null,
            goals: []
        }
    },
    render: function () {
        let pr = this.props;
        let goalNames = pr.goals.map((e) => {
            return <div key={e.id}>{e.description}</div>
        });
        return (
            <blockquote>{goalNames}</blockquote>
        );
    }
});

let GoalSnapshotMonth = React.createClass({
    getInitialState: function () {
        return {perDayGoals: {}};
    },
    render: function () {
        let st = this.state;
        let daySnapshots = [];
        for (let day in st.perDayGoals) {
            if (st.perDayGoals.hasOwnProperty(day)) {
                let dict = st.perDayGoals;
                daySnapshots.push(<GoalSnapshotDay key={dict[day].map((e) => e.id).join('-')} day={day} goals={dict[day]}/>);
            }
        }
        return (
            <div>Snapshot of month
                {daySnapshots}
            </div>
        );
    },
    componentDidMount: function () {
        let self = this;
        $.post(this.props.snapShotMonthUrl, {}).done((r) => {
            let json = JSON.parse(r);
            if (json.status === -1) {
                toastr.error(json.error);
            } else {
                self.setState((prevState, props) => {
                    return {perDayGoals: json.data};
                });
            }
        }).fail(() => {
            toastr.error('Server Error');
        });
    }
});

/* Export */
window.GoalSnapshotMonth = GoalSnapshotMonth;
