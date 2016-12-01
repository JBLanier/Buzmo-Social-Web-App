import React from 'react'
import $ from 'jquery'
import Store from './Store'
import {UTCToString} from './Toolbox'

export default class extends React.Component {
    
    constructor() {
        super();
        this.state = {
            showDelete: false
        };
    }

    getTopics() {
        return this.props.topics.map((topic, idx) => {
            return (<span key={idx} className="label label-default">{topic}</span>);
        });
    }
    
    componentDidMount() {
        new Store().getUser((user) => {
            if (user.userid === this.props.userid) {
                this.setState({
                    showDelete: true
                });
            }
        });
    }
    
    deleteMessage() {
        new Store().getAuth((auth) => {
            console.log("Deleting message.");
            $.ajax({
                method: "POST",
                url: `http://localhost:8080/api/mycircle/delete?mid=${this.props.mid}`,
                beforeSend: function (request)
                {
                    request.setRequestHeader("auth_token", auth);
                }
            })
                .done(() => {
                    alert("Message Deleted!");
                })
                .fail(function (err) {
                    alert("Could not delete message: " + JSON.stringify(err));
                });
        });
    }
    
    render() {
        return (
        <div className="media mycircle-message">
            <div className="media-left">
                {
                    this.props.public ?
                    <span className="glyphicon glyphicon-globe"></span>
                    :
                    <span className="glyphicon glyphicon-user"></span>
                }
            </div>
            <div className="media-body">
                <h4 className="media-heading">{this.props.screenname} {this.state.showDelete ? <button className="delete-button" onClick={this.deleteMessage.bind(this)}><span
                    className="glyphicon glyphicon-remove-circle"/></button> : "" }</h4>
                <p><span className="time">{UTCToString(this.props.utc)}</span> - {this.props.readCount} views</p>
                <p className="media-body">{this.props.msg}</p>
                <p className="topics">
                    {this.getTopics()}
                </p>
            </div>
        </div>
        );
    }
}