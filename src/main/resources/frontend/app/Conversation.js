import React from 'react'


export default class extends React.Component {

    constructor() {
        super();
    }

    getStyle() {
        if (this.props.active) {
            console.log("CONV KNOWS THAT ACTIVE IS: " + this.props.active);
            return {backgroundColor: "#555555", color : "#EEEEEE"};
        } return {};
    }

    onClick() {
        this.props.onClick(this);
    }

    render() {
        console.log("RENDER CONV");
        return (
            <div className="panel conversation" onClick={this.onClick.bind(this)} style={this.getStyle()}>
                <div className="panel-body">
                    <h4>{this.props.name}</h4>
                </div>
            </div>
        )
    }

}