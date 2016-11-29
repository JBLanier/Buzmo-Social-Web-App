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

    }

    componentDidMount() {
        console.log("----Mount");
        if (this.props.activeConvId != "0") {
            this.loadAllMessagesInConversation(this.state.offset, this.props.activeConvId);
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
                });

        },this);
    }

    loadAllMessagesInConversation(offset, otherid) {
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
            url = "http://" + new Store().getHost() + "/api/messages/conversation?offset=" + offset + "&user=" + otherid;
        } else {
            url = "http://" + new Store().getHost() + "/api/chatgroups/conversation?offset=" + offset + "&cgid=" + otherid;
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
                    data = sortByKeyLowestToHighest(data, 'utc');
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
                        loadMoreMessages: false,
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
                                 isFromUser = {this.props.recipient == msg.userid}
                                 pmMode = {this.props.pmMode}
                                 isGroupOwner = {this.state.isGroupOwner}/>;
        }.bind(this));


        this.lastRenderedConv = convid;

        return <div>{msgComponents}</div>;

    }

    getLoadMoreMessagesButton() {
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
        if (this.lastRenderedConv != this.props.activeConvId) {
            console.log("CALLING LOAD MESSAGES");
            this.loadAllMessagesInConversation(0, this.props.activeConvId);
            if (this.props.pmMode == false) {
                this.checkGroupOwnerShip();
            }
        }

        console.log("RENDER Messages!");
        // setTimeout(function(){this.loadAllMessagesInConversation(this.state.offset,
        //     this.props.activeConvId);}.bind(this),5000);

        return (


            <div className="pre-scrollable main-scrollable-content" id="scrollable-conversation-content">

                {this.renderMessages(this.props.activeConvId)}

                {this.getLoadMoreMessagesButton()}

            </div>
        )
    }
}
