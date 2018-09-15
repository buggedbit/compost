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
        let _now = moment();
        let bgColor = this.getColorOfDay(pr.day === _now.date() && pr.month - 1 === _now.month() && pr.year === _now.year());
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
                <div><b>{moment().date(pr.day).month(pr.month - 1).year(pr.year).format('Do ddd')}</b></div>
                <div className="collection">
                    {goalNames}
                </div>
            </div>
        );
    }
});

let GoalSnapshotMonth = React.createClass({
    currMonth: moment().month() + 1,
    currYear: moment().year(),
    getInitialState: function () {
        return {perDayGoals: {}, rowMajorRendering: true,};
    },
    switchLayoutRendering: function () {
        this.setState((prevState, props) => {
            return {rowMajorRendering: !prevState.rowMajorRendering};
        });
    },
    fetchSnapshotOfMonth: function (year, month) {
        let self = this;
        $.post(this.props.snapShotMonthUrl, {
            year: year,
            month: month,
        }).done((r) => {
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
    },
    shiftCurrentSnapshot: function (numMonths) {
        let prev = moment().date(1).month(this.currMonth - 1).year(this.currYear);
        let now = prev.add(numMonths, 'month');
        this.currMonth = now.month() + 1;
        this.currYear = now.year();
        this.fetchSnapshotOfMonth(this.currYear, this.currMonth);
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
                                    month={this.currMonth}
                                    year={this.currYear}
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
                                    month={this.currMonth}
                                    year={this.currYear}
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
        let numDaysInMonth = moment().year(this.currYear).month(this.currMonth - 1).date(1).daysInMonth();
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
                    <button className="waves-effect waves-light btn-large">
                        <i className="material-icons left"
                           onClick={() => this.shiftCurrentSnapshot(-1)}>arrow_back_ios</i>
                        {moment().year(this.currYear).month(this.currMonth - 1).date(1).format('MMM YYYY')}
                        <i className="material-icons right"
                           onClick={() => this.shiftCurrentSnapshot(1)}>arrow_forward_ios</i>
                    </button>
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
        this.fetchSnapshotOfMonth(this.currYear, this.currMonth);
    }
});

/* Export */
window.GoalSnapshotMonth = GoalSnapshotMonth;
