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
    render: function () {
        return (
            <div>Snapshot of day</div>
        );
    }
});

/**
 * @propFunctions:
 * */
let GoalSnapshotMonth = React.createClass({
    render: function () {
        return (
            <div>Snapshot of month
                <GoalSnapshotDay/>
                <GoalSnapshotDay/>
                <GoalSnapshotDay/>
            </div>
        );
    },
});

/* Export */
window.GoalSnapshotMonth = GoalSnapshotMonth;
