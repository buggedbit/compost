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
            widthPx: 50,
            heightPx: 50,
            leftPx: 0,
            topPx: 0,
            day: 0,
            goals: []
        }
    },
    render: function () {
        let pr = this.props;
        let goalNames = pr.goals.map((e) => {
            return <div key={e.id}
                        className="truncate collection-item"
                        style={{padding: '4px', backgroundColor: e.is_achieved ? '#a5d6a7' : '#ffffff'}}>
                {e.is_achieved ? <span className="badge"><i className="material-icons">done</i></span> : ''}
                <span style={{color: e.color}}>{e.description}</span>
            </div>
        });
        let bgColor = pr.goals.length === 0 ? '#ffffff' : '#e91e63';
        return (
            <div style={{
                position: 'absolute',
                padding: '3px',
                left: pr.leftPx + 'px',
                top: pr.topPx + 'px',
                width: pr.widthPx + 'px',
                height: pr.heightPx + 'px',
                overflowY: 'auto',
                backgroundColor: bgColor,
            }}>
                <div>{moment().date(pr.day).format('Do ddd')}</div>
                <div className="collection">
                    {goalNames}
                </div>
            </div>
        );
    }
});

let GoalSnapshotMonth = React.createClass({
    getInitialState: function () {
        return {perDayGoals: {}};
    },
    getGoalSnapshotDay: function (day, goals) {
        let refs = this.refs;
        let W = $(refs.snapshotMonth).width();
        let H = $(refs.snapshotMonth).height();
        let w = W / 7;
        let h = H / 5;
        let l = ((day - 1) % 7) * w;
        let t = parseInt((day - 1) / 7) * h;
        return <GoalSnapshotDay key={[day, '-'] + goals.map((e) => e.id)}
                                day={day}
                                widthPx={w}
                                heightPx={h}
                                leftPx={l}
                                topPx={t}
                                goals={goals}/>
    },
    render: function () {
        // aliases
        let st = this.state;
        // day wise snapshots
        let daySnapshots = [];
        let numDaysInMonth = moment().daysInMonth();
        for (let day = 1; day <= numDaysInMonth; ++day) {
            daySnapshots.push(this.getGoalSnapshotDay(day, st.perDayGoals[day] === undefined ? [] : st.perDayGoals[day]));
        }
        return (
            <div style={{
                position: 'relative',
                width: '100%',
                height: '100%',
            }} ref="snapshotMonth">
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
