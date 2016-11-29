import React from 'react'

export default class extends React.Component {

    getMessageStyle() {
        return {
            float: this.props.isFromUser ? "right" : "left",
            textAlign: this.props.isFromUser ? "right" : "left",
            backgroundColor: this.props.isFromUser ? "#888888" : "#444444"
        };
    }

    getTimeStyle() {
        return {
            "text-align": this.props.isFromUser ? "right" : "left"
        };
    }

    getScreennameStyle() {
        return {
            "text-align" : this.props.isFromUser ? "right" : "left"
        };
    }

    render() {
        return (
            <div className="message" >
                <div className="row message-screenname" style={this.getScreennameStyle()}>
                    {this.props.screenname}
                </div>
                <div className="row">
                    <div className="panel message-panel">
                        <div className="panel-body message-body" style={this.getMessageStyle()}>
                            {this.props.msg}
                        </div>
                    </div>
                </div>
                <div className="row message-time" style={this.getTimeStyle()}>
                    {this.props.time}
                </div>
            </div>
        )
    }
}