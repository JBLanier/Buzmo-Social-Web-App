import React from 'react'

export default class extends React.Component {
    constructor(bisFromUser, msg_content_string, time_sent_string) {
        super();
        this.isFromUser = bisFromUser;
        this.message = msg_content_string;
        this.time = time_sent_string;
    }


    getMessageStyle() {
        return {
            float: this.isFromUser ? "right" : "left",
            textAlign: this.isFromUser ? "right" : "left",
            backgroundColor: this.isFromUser ? "#888888" : "#444444"
        };
    }

    getTimeStyle() {
        return {
            float: this.isFromUser ? "right" : "left",
        };
    }

    render() {
        return (
            <div className="message">
                <div className="row">
                    <div className="panel message-panel">
                        <div className="panel-body message-body" style={this.getMessageStyle()}>
                            {this.message}
                        </div>
                    </div>
                </div>
                <div className="row message-time" style={this.getTimeStyle()}>
                    {this.time}
                </div>
            </div>
        )
    }
}