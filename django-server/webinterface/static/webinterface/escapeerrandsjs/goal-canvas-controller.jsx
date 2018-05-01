/**
 * Dependencies:
 *  react.js
 *  react-dom.js
 *  babel.js
 * */

/**
 * @propFunctions:
 * onGoalDrop,
 * onEmptySpaceClick,
 * onGoalSelect,
 * onRelationSelect,
 * onDoubleClick,
 * onGoalDoubleClick,
 * onEdgeDoubleClick,
 * onEmptySpaceDoubleClick,
 * onGoalContext,
 * onRelationContext,
 * onGoalShowPopup,
 * onTwoGoalsConsecutiveDoubleClick,
 * */
let GoalCanvasController = React.createClass({
    prevSocietyString: undefined,
    getDefaultProps: function () {
        return {
            wrappingDivId: 'goal-canvas-wrapper',
            graphSettings: {
                MAX_LABEL_LENGTH: 15,
                colors: {
                    UN_ACHIEVED: {
                        background: 'pink',
                        border: 'purple'
                    },
                    ACHIEVED: {
                        background: 'lightgreen',
                        border: 'green'
                    },
                    MARKED: {
                        background: 'orange',
                        border: 'red'
                    }
                },
                visOptions: {
                    nodes: {
                        shape: 'dot',
                        borderWidth: 5,
                        size: 10,
                        font: {size: 15, color: 'black', background: 'white', face: 'Roboto'}
                    },
                    layout: {
                        hierarchical: {
                            sortMethod: "directed"
                        }
                    },
                    interaction: {
                        hover: true,
                        navigationButtons: true
                    }
                }
            },
        };
    },

    // graph fields & methods
    graphNodeDataSet: undefined,
    graphEdgeDataSet: undefined,
    getNodeSet: function (society) {
        const self = this;
        let array = [];
        society.forEach((family, fi) => {
            family.forEach((goal, gi) => {
                let color;
                if (goal.is_achieved === true) {
                    color = self.props.graphSettings.colors.ACHIEVED;
                }
                else {
                    color = self.props.graphSettings.colors.UN_ACHIEVED;
                }
                array.push({
                    id: goal.id,
                    color: color,
                    label: truncate(goal.description, self.props.graphSettings.MAX_LABEL_LENGTH),
                    title: goal.description
                });
            });
        });
        return array;
    },
    getEdgeSet: function (society) {
        let array = [];
        society.forEach((family, fi) => {
            family.forEach((goal, gi) => {
                goal.child_ids.forEach((child, ci) => {
                    array.push({
                        from: goal.id,
                        to: child,
                        arrows: 'to'
                    });
                })
            });
        });
        return array;
    },

    render: function () {
        return (<div>
                <div id={this.props.wrappingDivId}
                     onDrop={(e) => {
                         this.props.onGoalDrop(e.dataTransfer.getData('goalId'));
                     }}
                     onDragOver={(e) => {
                         e.preventDefault();
                     }}>
                </div>
            </div>
        );
    },
    componentDidMount: function () {
        let canvasDiv = document.getElementById(this.props.wrappingDivId);
        // store node and edge vis data sets for future use
        let nds = this.graphNodeDataSet = new vis.DataSet(this.getNodeSet(this.props.society));
        let eds = this.graphEdgeDataSet = new vis.DataSet(this.getEdgeSet(this.props.society));
        let data = {
            nodes: nds,
            edges: eds,
        };
        let options = this.props.graphSettings.visOptions;
        let graph = new vis.Network(canvasDiv, data, options);

        // listeners
        let self = this;
        let NewEdgeMaker = {
            fromId: undefined,
            toId: undefined,
            fromColor: undefined,
            reset: function () {
                this.fromId = undefined;
                this.toId = undefined;
                this.fromColor = undefined;
            },
            startOrEnd: function (nodeId) {
                // Start
                if (this.fromId === undefined && this.toId === undefined) {
                    this.fromId = nodeId;
                    this.fromColor = self.graphNodeDataSet.get(nodeId).color;
                    self.graphNodeDataSet.update({
                        id: NewEdgeMaker.fromId,
                        color: self.props.graphSettings.colors.MARKED
                    });
                }
                // End
                else if (this.fromId !== undefined && this.toId === undefined) {
                    self.graphNodeDataSet.update({
                        id: NewEdgeMaker.fromId,
                        color: NewEdgeMaker.fromColor
                    });
                    this.toId = nodeId;
                    self.props.onTwoGoalsConsecutiveDoubleClick(this.fromId, this.toId);
                    this.reset();
                }
            },
            discardOperation: function () {
                if (this.fromId !== undefined && this.toId === undefined) {
                    self.graphNodeDataSet.update({
                        id: NewEdgeMaker.fromId,
                        color: NewEdgeMaker.fromColor
                    });
                    this.reset();
                }
            },
        };
        graph.on('click', function (params) {
            // on empty space
            if (params.nodes.length === 0 && params.edges.length === 0) {
                self.props.onEmptySpaceClick();
                NewEdgeMaker.discardOperation();
            }
        });
        graph.on('selectNode', function (params) {
            self.props.onGoalSelect(params.nodes[0]);
        });
        graph.on('selectEdge', function (params) {
            self.props.onRelationSelect(params.nodes[0]);
        });
        graph.on('doubleClick', function (params) {
            self.props.onDoubleClick();
            // On node
            if (params.nodes.length === 1) {
                let nodeId = params.nodes[0];
                NewEdgeMaker.startOrEnd(nodeId);
                self.props.onGoalDoubleClick(nodeId);
            }
            // On Edge
            else if (params.edges.length === 1) {
                let edge = self.graphEdgeDataSet.get(params.edges[0]);
                let fromGoalId = edge.from;
                let toGoalId = edge.to;
                self.props.onEdgeDoubleClick(fromGoalId, toGoalId);
            }
            // On empty space
            else if (params.nodes.length === 0 && params.edges.length === 0) {
                self.props.onEmptySpaceDoubleClick();
            }
        });
        graph.on('oncontext', function (params) {
            // On node
            if (params.nodes.length === 1) {
                self.props.onGoalContext(params.nodes[0]);
            }
            // On edge
            else if (params.edges.length === 1) {
                self.props.onRelationContext(params.nodes[0]);
            }
        });
        graph.on('showPopup', function (nodeId) {
            self.props.onGoalShowPopup(nodeId);
        });
    },
    componentDidUpdate: function () {
        // if previous society's graph is different from this ones then re render whole graph
        if (this.prevSocietyString !== JSON.stringify(this.props.society)) {
            this.graphNodeDataSet.clear();
            this.graphNodeDataSet.add(this.getNodeSet(this.props.society));
            this.graphEdgeDataSet.clear();
            this.graphEdgeDataSet.add(this.getEdgeSet(this.props.society));
            this.prevSocietyString = JSON.stringify(this.props.society);
        }
    },
});

/* Export */
window.GoalCanvasController = GoalCanvasController;
