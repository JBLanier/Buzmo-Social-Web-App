import React from 'react'
import Store from './Store'
import $ from 'jquery'
import {UTCToString} from './Toolbox'

export default class extends React.Component {

    constructor() {
        super();

        this.state = {isGroupOwner: false};
        this.lastCheckedConv = 0;
        this.lastCheckedPMMode = true;
    }

    componentDidMount() {
        this._mounted = true;
    }

    componentWillUnmount() {
        this._mounted = false;
    }

    checkGroupOwnerShip() {


        new Store().getAuth(function (auth) {
            console.log("Checking group ownership");
            $.ajax({
                method: "POST",
                url: "http://localhost:8080/api/chatgroups/checkownerhip?cgid=" + this.props.activeConvId,
                beforeSend: function (request) {
                    request.setRequestHeader("auth_token", auth);
                },
                data: null,
                contentType: null
            })
                .done(function () {
                    console.log("Is owner of chatgroup");
                    if (this._mounted) {
                        this.setState({
                            isGroupOwner: true
                        });
                    }
                }.bind(this))
                .fail(function (err) {
                    console.log("Did not return as owner of chatgroup");
                    if (this._mounted) {
                        this.setState({
                            isGroupOwner: false
                        });
                    }
                }.bind(this));

        }, this);

    }

    getMessageStyle() {
        return {
            float: this.props.isFromUser ? "right" : "left",
            textAlign: this.props.isFromUser ? "right" : "left",
            backgroundColor: this.props.isFromUser ? "#888888" : "#444444"
        };
    }

    getTimeStyle() {
        return {
            textAlign: this.props.isFromUser ? "right" : "left"
        };
    }

    getScreennameStyle() {
        return {
            textAlign : this.props.isFromUser ? "right" : "left"
        };
    }

    deleteMessage(){
        if (confirm("Delete Message?")) {
            let url = "";

            if (this.props.pmMode) {
                url = "http://localhost:8080/api/messages/delete?mid=" + this.props.mid;
            } else {
                url = "http://localhost:8080/api/chatgroups/conversation/delete?mid=" + this.props.mid;
            }

            new Store().getAuth(function (auth) {
                console.log("deleting message...");
                $.ajax({
                    method: "POST",
                    url: url,
                    beforeSend: function (request)
                    {
                        request.setRequestHeader("auth_token", auth);
                    },
                    data: null,
                    contentType: null
                })
                    .done(function () {
                        if (this.props.pmMode) {
                            console.log("Message deleted as private message");
                        } else {
                            console.log("Message deleted as group chat message");
                        }
                    }.bind(this))
                    .fail(function (err) {
                        alert("Could not login: " + JSON.stringify(err));
                    });

            },this);

        }
    }

    renderDeleteButton(){
        if (this.props.pmMode || this.state.isGroupOwner) {
            return (
                <button className="delete-button" onClick={this.deleteMessage.bind(this)}><span
                    className="glyphicon glyphicon-remove-circle"/></button>
            )
        }
    }

    render() {
        if (this.lastCheckedConv != this.props.activeConvId || this.lastCheckedPMMode != this.props.pmMode) {
            this.lastCheckedConv = this.props.activeConvId;
            this.lastCheckedPMMode = this.props.pmMode;
            if (this.props.pmMode == false) {
                this.checkGroupOwnerShip();
            }
        }

        return (
            <div className="message" >
                <div className="row message-screenname" style={this.getScreennameStyle()}>
                    {this.renderDeleteButton()}
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
                    {UTCToString(this.props.time)}
                </div>
            </div>
        )
    }
}