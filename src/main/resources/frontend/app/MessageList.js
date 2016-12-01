import React from 'react'
import Message from './Message'
import Store from './Store'
import { Router, Route, hashHistory, IndexRoute, browserHistory} from 'react-router'

function sortByKeyLowestToHighest(array, key) {
    return array.sort(function(a, b) {
        var x = a[key]; var y = b[key];
        //Highest first, lowest last
        return ((x < y) ? -1 : ((x > y) ? 1 : 0));
    });
}

const MESSAGEFETCHLIMIT = 5;

export default class extends React.Component {



    constructor() {
        super();
        this.state = {
            messageList: [],
            offset: 0,
            loadMoreMessages: false,
            isGroupOwner: false
        }
        this.lastRenderedConv = 0;
        this.lastCheckedPMode = false;

    }

    componentDidMount() {
        console.log("----Mount");
        if (this.props.activeConvId != "0") {
            this.loadMessagesInConversation(this.state.offset, this.props.activeConvId);
        }

    }

    checkGroupOwnerShip() {
        new Store().getAuth(function (auth) {
            console.log("Checking group ownership");
            $.ajax({
                method: "POST",
                url: "http://localhost:8080/api/chatgroups/checkownerhip?cgid=" + this.props.activeConvId,
                beforeSend: function (request)
                {
                    request.setRequestHeader("auth_token", auth);
                },
                data: null,
                contentType: null
            })
                .done(function () {
                    console.log("Is owner of chatgroup");
                    this.setState({
                        messageList: this.state.messageList,
                        offset: this.state.offset,
                        loadMoreMessages: this.state.loadMoreMessages,
                        isGroupOwner: true
                    });
                }.bind(this))
                .fail(function (err) {
                    console.log("Did not return as owner of chatgroup");
                    this.setState({
                        messageList: this.state.messageList,
                        offset: this.state.offset,
                        loadMoreMessages: this.state.loadMoreMessages,
                        isGroupOwner: false
                    });
                });

        },this);
    }

    loadMessagesInConversation(offset, otherid) {
        if(otherid == "0") {
            this.setState({
                messageList: [],
                offset: 0,
                loadMoreMessages: false,
                isGroupOwner: this.state.isGroupOwner
            })
        }

        let url = "";

        if (this.props.pmMode) {
            url = "http://" + new Store().getHost() + "/api/messages/conversation?user=" + otherid;
        } else {
            url = "http://" + new Store().getHost() + "/api/chatgroups/conversation?cgid=" + otherid;
        }

        new Store().getAuth(function (auth) {
            $.ajax({
                method: "GET",
                url: url,
                beforeSend: function (request)
                {
                    request.setRequestHeader("auth_token", auth);
                },
                data: null,
                contentType: "application/json"
            })
                .done(function( data ) {
                    console.log("RECEIVED Messages: \n");
                    //data = sortByKeyLowestToHighest(data, 'utc');
                    console.log(data);
                    if (offset > 0) {
                        const oldmsgs = this.state.messageList.slice(0,offset);
                        console.log(oldmsgs);
                        data = oldmsgs.concat(data);
                        console.log(data);
                    }
                    console.log("DATA:");
                    console.log(data);
                    this.setState({
                        messageList: data,
                        offset: data.length,
                        loadMoreMessages: data.length >= MESSAGEFETCHLIMIT,
                        isGroupOwner: this.state.isGroupOwner});

                }.bind(this))
                .fail(function(err) {
                    console.log("error retrieving conversations, returning to login screen");
                    hashHistory.push('#')
                });

        },this);



    }

    loadMoreMessagesInConversation() {

        let otherid = this.props.activeConvId;

        if(otherid == "0") {
            this.setState({
                messageList: [],
                offset: 0,
                loadMoreMessages: false,
                isGroupOwner: this.state.isGroupOwner
            })
        }

        let utc = this.state.messageList[0].utc;
        let url = "";

        if (this.props.pmMode) {
            url = "http://" + new Store().getHost() + "/api/messages/conversation?user=" + otherid + "&before=" + utc;
        } else {
            url = "http://" + new Store().getHost() + "/api/chatgroups/conversation?cgid=" + otherid + "&before=" + utc;
        }

        new Store().getAuth(function (auth) {
            $.ajax({
                method: "GET",
                url: url,
                beforeSend: function (request)
                {
                    request.setRequestHeader("auth_token", auth);
                },
                data: null,
                contentType: "application/json"
            })
                .done(function( data ) {
                    console.log("RECEIVED Messages: \n");
                    //data = sortByKeyLowestToHighest(data, 'utc');
                    console.log(data);

                    let numRecieved = data.length;

                    const oldmsgs = this.state.messageList;
                    console.log(oldmsgs);
                    data = data.concat(oldmsgs);
                    console.log(data);

                    console.log("DATA:");
                    console.log(data);
                    this.setState({
                        messageList: data,
                        offset: data.length,
                        loadMoreMessages: numRecieved >= MESSAGEFETCHLIMIT,
                        isGroupOwner: this.state.isGroupOwner});

                }.bind(this))
                .fail(function(err) {
                    console.log("error retrieving conversations, returning to login screen");
                    hashHistory.push('#')
                });

        },this);



    }

    renderMessages(convid) {

        var msgComponents = this.state.messageList.map(function(msg) {
            return <Message msg={msg.msg}
                                 screenname={msg.screenname}
                                 id={msg.userid}
                                 mid={msg.mid}
                                 time={msg.utc}
                                 activeConvId = {this.props.activeConvId}
                                 isFromUser = {this.props.recipient == msg.userid}
                                 pmMode = {this.props.pmMode}
                                 />;
        }.bind(this));


        this.lastRenderedConv = convid;

        return <div>{msgComponents}</div>;

    }

    getLoadMoreMessagesButton() {
        if (this.state.loadMoreMessages) {
            return (
                <div className="panel panel-default btn load-more-button" onClick={this.loadMoreMessagesInConversation.bind(this)}>
                    <div className="panel-body">
                        Load More
                    </div>
                </div>
            )
        }
    }

    render() {
        if (this.lastRenderedConv != this.props.activeConvId || this.lastCheckedPMode != this.props.pmMode) {
            console.log("CALLING LOAD MESSAGES");
            this.loadMessagesInConversation(0, this.props.activeConvId);
            this.lastCheckedPMode = this.props.pmMode;
        }

        console.log("RENDER Messages!");
        // setTimeout(function(){this.loadMessagesInConversation(this.state.offset,
        //     this.props.activeConvId);}.bind(this),5000);

        return (


            <div className="pre-scrollable main-scrollable-content" id="scrollable-conversation-content">

                {this.getLoadMoreMessagesButton()}

                {this.renderMessages(this.props.activeConvId)}

            </div>
        )
    }
}
