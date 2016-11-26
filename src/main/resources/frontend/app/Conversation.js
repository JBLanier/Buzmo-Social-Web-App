import React from 'react'

export default class extends React.Component {

    constructor() {
        super();
        this.state = {isActive:false};
    }

    getStyle() {
        if (this.state.isActive) {
            return {backgroundColor: "#555555", color : "#EEEEEE"};
        } return {};
    }

    onClick() {
        this.props.onClick(this);
    }

    render() {
        return (
            <div className="panel conversation" onClick={this.onClick.bind(this)} style={this.getStyle()}>
                <div className="panel-body">
                    <h4>{this.props.name}</h4>
                </div>
            </div>
        )
    }

}