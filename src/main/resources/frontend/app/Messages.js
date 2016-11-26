import React from 'react'
import Conversation from './Conversation'
import Message from './Message'


export default class extends React.Component {

    constructor() {
        super();

        this.state = {

            conversationList: [
                <Conversation name="Henry FitzGerald" onClick={this.onConversationClicked.bind(this)} id="43"/>,
                <Conversation name="Dude Guy" onClick={this.onConversationClicked.bind(this)} id="57"/>],


            messageList: [new Message(true, "hard coded message 1 !", "June 13, 2017 23:43:21").render(),
                new Message(false, "hard coded message 2 !", "June 13, 2017 23:43:21").render(),
                new Message(true, "hard coded message 3 !", "June 13, 2017 23:43:21").render()],


            //If true, load more button is rendered
            loadMoreConversations: false,
            loadMoreMessages: false,

            currentConversationNameString: "Choose a Conversation to Start",

        }
        this.activeConversation = null;

    }

    setNewActiveConversation(conv) {
        console.log(conv.props.name + ", " + conv.props.id + " is the new active conversation");
        this.activeConversation = conv;

    }

    GETallMessagesInConversation() {

    }

    sendMessage(msg_string) {
        console.log("sending message: " + msg_string);
    }

    onConversationClicked(t){
        t.setState({isActive: true});
        if (this.activeConversation == null) {
            this.setNewActiveConversation(t);
        } else if(this.state.activeConversation != t) {
            this.activeConversation.setState({isActive: false});
            this.setNewActiveConversation(t);

        }

    }


    onMessageInputChange(e) {
        const input = e.target.value;
        console.log(input);
        this.messageInput = input;

    }

    onMessageSendButtonClick(){
        if (this.messageInput != "") {
            this.sendMessage(this.messageInput);
        }
        this.refs.textInput.value = "";

    }

    getloadMoreConversationsButton() {
        if (this.state.loadMoreConversations) {
            return (
                <div className="panel panel-default btn load-more-button">
                    <div className="panel-body">
                        Load More
                    </div>
                </div>
            )
        }
    }

    getloadMoreMessagesButton() {
        if (this.state.loadMoreMessages) {
            return (
                <div className="panel panel-default btn load-more-button">
                    <div className="panel-body">
                        Load More
                    </div>
                </div>
            )
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
                                            <button className="btn btn-default" id="pm-button">PM</button>
                                        </div>
                                        <div className="btn-group" role="group">
                                            <button className="btn btn-default" id="gc-button">GC</button>
                                        </div>
                                    </div>

                                    <div className="pre-scrollable main-scrollable-content"id="scrollable-conversations-list">
                                        <div className="btn-group btn-group-justified" role="group">
                                            <div className="btn-group" role="group">
                                                <button className="btn btn-default" id="new-message-button">New Message</button>
                                            </div>
                                        </div>


                                        {this.state.conversationList}

                                        {this.getloadMoreConversationsButton()}

                                    </div>
                                </div>
                            </div>
                        </div>
                        <div className="col-sm-9 ">
                            <div className="panel panel-default ">
                                <div className="panel-body ">
                                    <div className="text-center" id="current-conversation-name">{this.state.currentConversationNameString}</div>
                                        <div className="pre-scrollable main-scrollable-content" id="scrollable-conversation-content">

                                            {this.state.messageList}

                                            {this.getloadMoreMessagesButton()}

                                        </div>
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
