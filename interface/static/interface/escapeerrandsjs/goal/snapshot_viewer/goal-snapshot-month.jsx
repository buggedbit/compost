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
    getColorOfDay: function (dayisToday) {
        if (dayisToday) {
            return "#42a5f5";
        }
        let colors = ['#ffffff', '#ffcdd2', '#ef9a9a', '#e57373', '#ef5350', '#f44336', '#e53935'];
        let numGoals = this.props.goals.length;
        let numColors = colors.length;
        return colors[numGoals >= numColors ? numColors - 1 : numGoals];
    },
    render: function () {
        let pr = this.props;
        let goalNames = pr.goals.map((e) => {
            return <div key={e.id}
                        className="truncate collection-item"
                        style={{
                            padding: '5px',
                            backgroundColor: e.is_achieved ? '#a5d6a7' : '#ffffff',
                        }}>
                {e.is_achieved ? <span className="badge"><i className="material-icons">done</i></span> : ''}
                <span style={{color: e.color}}>{e.description}</span>
            </div>
        });
        let bgColor = this.getColorOfDay(pr.day === moment().date());
        return (
            <div style={{
                position: 'absolute',
                padding: '15px',
                left: pr.leftPx + 'px',
                top: pr.topPx + 'px',
                width: pr.widthPx + 'px',
                height: pr.heightPx + 'px',
                overflowY: 'auto',
                backgroundColor: bgColor,
            }}>
                <div><b>{moment().date(pr.day).format('Do ddd')}</b></div>
                <div className="collection">
                    {goalNames}
                </div>
            </div>
        );
    }
});

let GoalSnapshotMonth = React.createClass({
    getInitialState: function () {
        return {perDayGoals: {}, rowMajorRendering: true};
    },
    switchLayoutRendering: function () {
        this.setState((prevState, props) => {
            return {rowMajorRendering: !prevState.rowMajorRendering};
        });
    },
    getGoalSnapshotDay: function (day, goals) {
        let refs = this.refs;
        let st = this.state;
        let W = $(refs.snapshotMonth).width();
        let H = $(refs.snapshotMonth).height();
        if (st.rowMajorRendering) {
            // row major layout
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
        } else {
            // column major layout
            let w = W / 5;
            let h = H / 7;
            let t = ((day - 1) % 7) * h;
            let l = parseInt((day - 1) / 7) * w;
            return <GoalSnapshotDay key={[day, '-'] + goals.map((e) => e.id)}
                                    day={day}
                                    widthPx={w}
                                    heightPx={h}
                                    leftPx={l}
                                    topPx={t}
                                    goals={goals}/>
        }
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
                <div style={{
                    position: 'absolute',
                    right: '20px',
                    bottom: '100px',
                }}>
                    <button className="waves-effect waves-light btn-large">{moment().format('MMMM')}</button>
                </div>
                <div style={{
                    position: 'absolute',
                    right: '20px',
                    bottom: '20px',
                }}>
                    <button className="btn btn-floating btn-large"><i
                        className="material-icons"
                        title="Switch layout"
                        onClick={this.switchLayoutRendering}>{st.rowMajorRendering ? "view_week" : "view_stream"}</i>
                    </button>
                </div>
            </div>
        );
    },
    componentDidMount: function () {
        let self = this;
        $.post(this.props.snapShotMonthUrl, {}).done((r) => {
            let json = JSON.parse(r);
            if (json.status === -1) {
                toastr.error(json.error);
                self.setState((prevState, props) => {
                    return {perDayGoals: {}};
                });
            } else {
                self.setState((prevState, props) => {
                    return {perDayGoals: json.data};
                });
            }
        }).fail(() => {
            toastr.error('Server Error');
            self.setState((prevState, props) => {
                return {perDayGoals: {}};
            });
        });
    }
});

/* Export */
window.GoalSnapshotMonth = GoalSnapshotMonth;
