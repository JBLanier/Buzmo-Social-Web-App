import React from 'react'
import MessageList from './MessageList'
import ConversationList from './ConversationList'
import Conversation from './Conversation'
import $ from 'jquery'
import Store from './Store'
import { Router, Route, hashHistory, IndexRoute, browserHistory} from 'react-router'


export default class extends React.Component {

    constructor() {
        super();

        this.state = {

            //false means we're in groupchat mode
            pmMode : true,

            activeConvName:"Choose a Conversation to Start",
            activeConvId:"0",
            user: {userid: 0}
        }

    }


    componentDidMount() {
        new Store().getUser(function(user) {
            this.setState({pmMode: this.state.pmMode,
                activeConvName: this.state.activeConvName,
                activeConvId: this.state.activeConvId,
                user: user});
        },this);
    }



    setNewActiveConversation(conv) {
        console.log(conv.props.name + ", " + conv.props.id + " is the new active conversation");
        this.setState({
            pmMode: this.state.pmMode,
            activeConvName: conv.props.name,
            activeConvId: conv.props.id,
            user: this.state.user});
    }

    sendMessage(msg_string, recipient) {

        let url = "";

        if (this.state.pmMode) {
            url = "http://localhost:8080/api/messages/send"
        } else {
            url = "http://localhost:8080/api/chatgroups/conversation/send"
        }

            new Store().getAuth(function (auth) {
                console.log("sending message: " + msg_string);
                $.ajax({
                    method: "POST",
                    url: url,
                    beforeSend: function (request)
                    {
                        request.setRequestHeader("auth_token", auth);
                    },
                    data: JSON.stringify({msg: msg_string, recipient: recipient}),
                    contentType: "application/json"
                })
                    .done(function () {
                        console.log("Message sent!");
                    })
                    .fail(function (err) {
                        alert("Could not login: " + JSON.stringify(err));
                    });

            },this);

    }

    onMessageInputChange(e) {
        const input = e.target.value;
        console.log(input);
        this.messageInput = input;

    }

    onMessageSendButtonClick(){
        if (this.messageInput != "") {
            this.sendMessage(this.messageInput, this.state.activeConvId);
        }
        this.refs.textInput.value = "";

    }

    onPMPressed() {
        if (!this.state.pmMode) {
            this.setState({
                pmMode: true,
                activeConvName: "Choose a Conversation to Start",
                activeConvId: 0,
                user: this.state.user
            });
        }
    }

    onGCPressed() {
        if (this.state.pmMode) {
            this.setState({
                pmMode: false,
                activeConvName: "Choose a Conversation to Start",
                activeConvId: 0,
                user: this.state.user
            });
        }
    }


    render() {
        return (
            <div>
                <div className="container-fluid mainContainer">
                    <div className="row " >
                        <div className="col-sm-3 ">
                            <div className="panel panel-default messagePanel ">
                                <div className="panel-body ">
                                    <div className="btn-group btn-group-justified" role="group">
                                        <div className="btn-group" role="group">
                                            <button className="btn btn-default" id="pm-button" onClick={this.onPMPressed.bind(this)}>PM</button>
                                        </div>
                                        <div className="btn-group" role="group">
                                            <button className="btn btn-default" id="gc-button" onClick={this.onGCPressed.bind(this)}>GC</button>
                                        </div>
                                    </div>

                                    <ConversationList activeConvId={this.state.activeConvId}
                                                      setNewActiveConversation={this.setNewActiveConversation.bind(this)}
                                                      pmMode = {this.state.pmMode}
                                                      sendMessage = {this.sendMessage.bind(this)}/>
                                </div>
                            </div>
                        </div>
                        <div className="col-sm-9 ">
                            <div className="panel panel-default ">
                                <div className="panel-body ">
                                    <div className="text-center" id="current-conversation-name">{this.state.activeConvName}</div>

                                        <MessageList activeConvId={this.state.activeConvId} pmMode = {this.state.pmMode} recipient={this.state.user.userid}/>

                                    </div>
                                    <div className="input-group" id="conversation-message-input-box">

                                            <input type="text" className="form-control" placeholder="Type Message..." onChange={this.onMessageInputChange.bind(this)} ref="textInput"/>
                                            <span className="input-group-btn">
                                                <button className="btn btn-default" type="button" onClick={this.onMessageSendButtonClick.bind(this)}>Send</button>
                                            </span>
                                    </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}
