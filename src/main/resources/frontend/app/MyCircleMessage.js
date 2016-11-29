import React from 'react'

export default class extends React.Component {

    getTopics() {
        return this.props.topics.map((topic, idx) => {
            return (<span key={idx} className="label label-default">{topic}</span>);
        });
    }
    render() {
        return (
        <div className="media mycircle-message">
            <div className="media-left">
                <span className="glyphicon glyphicon-user"></span>
            </div>
            <div className="media-body">
                <h4 className="media-heading">{this.props.screenname}</h4>
                <p className="time">{this.props.utc}</p>
                <p className="media-body">{this.props.msg}</p>
                <p className="topics">
                    {this.getTopics()}
                </p>
            </div>
        </div>
        );
    }
}