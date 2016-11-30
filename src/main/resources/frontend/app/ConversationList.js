import React from 'react'
import Conversation from './Conversation'
import Store from './Store'

function sortByKeyHighestToLowest(array, key) {
    return array.sort(function(a, b) {
        let x = a[key]; let y = b[key];
        //Highest first, lowest last
        return ((x > y) ? -1 : ((x < y) ? 1 : 0));
    });
}

export default class extends React.Component {


    constructor() {
        super();
        this.state={conversationList : []};

        this.lastFetchPms = true;
    }

    componentDidMount() {
        this.loadAllConversations();
    }

    loadAllConversations() {

        let url = "";

        if (this.props.pmMode) {
            url = "http://" + new Store().getHost() + "/api/messages/list";
        } else {
            url = "http://" + new Store().getHost() + "/api/chatgroups/list";
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
                        var messages = [];
                        // $.each(data, function (key, val) {
                        //     messages.push(key + " : " + val + "\n");
                        // });
                        console.log("RECEIVED CONVERSATIONS: \n");
                        data = sortByKeyHighestToLowest(data,'utc');
                        console.log(data);
                        this.setState({conversationList: data});
                    }.bind(this))
                    .fail(function(err) {
                        console.log("error retrieving conversations, returning to login screen");
                        hashHistory.push('#')
                    });

            },this);

    }


    getloadMoreConversationsButton() {
        // We always load all conversations at once

        // if (this.state.loadMoreConversations) {
        //     return (
        //         <div className="panel panel-default btn load-more-button">
        //             <div className="panel-body">
        //                 Load More
        //             </div>
        //         </div>
        //     )
        // }
    }

    renderConversations() {

            var convComponents = this.state.conversationList.map(function (conv) {
                console.log(conv);
                return <Conversation name={conv.name}
                                     onClick={this.props.setNewActiveConversation}
                                     id={conv.uniqueId}
                                     active={conv.uniqueId == this.props.activeConvId}
                                     pmMode={conv.pm}/>;
            }.bind(this));
            return <div>{convComponents}</div>;

    }

    renderNewMessageButton() {
        if (this.props.pmMode) {
            return (
                <div className="btn-group btn-group-justified" role="group">
                    <div className="btn-group" role="group">
                        <button className="btn btn-default" id="new-message-button"
                                data-toggle="modal" data-target="#myModal">New Message</button>
                    </div>
                </div>
            )
        }
    }

    sendMessage() {
        let email = this.refs.recipient.value;

        if (email == "" || this.refs.msginput.value == "") {
            return;
        }

        this.refs.recipient.value="";
        this.refs.msginput.value="";

        this.getUserInfoFromEmail(email,function(data){
            if (data != undefined && data !=null) {
                this.props.sendMessage(this.refs.msginput.value,data.userid);
            } else {
                alert("Sorry, " + email + "isn't on Buzmo.");
            }
        },this)

    }

    getUserInfoFromEmail(email, callback, context) {
        new Store().getAuth(function (auth) {
            $.ajax({
                method: "GET",
                url: "http://localhost:8080/api/user?email=" + email,
                beforeSend: function (request)
                {
                    request.setRequestHeader("auth_token", auth);
                },
                data: null,
                contentType: null
            })
                .done(function (data) {
                    callback.call(context, data);
                })
                .fail(function (err) {
                    callback.call(context, undefined);
                });

        },this);
    }


    render() {
        if (this.props.pmMode != this.lastFetchPms) {
            this.lastFetchPms = this.props.pmMode;
            this.loadAllConversations();
        }

        console.log("RENDER LIST");
        return (

            <div className="pre-scrollable main-scrollable-content"id="scrollable-conversations-list">

                {this.renderNewMessageButton()}

                <div className="modal fade" id="myModal" role="dialog">
                    <div className="modal-dialog modal-lg">
                        <div className="modal-content">
                            <div className="modal-header">
                                <button type="button" className="close" data-dismiss="modal">&times;</button>
                                <h4 className="modal-title">New Message</h4>
                            </div>
                            <div className="modal-body">
                                <label for="recipient">Name:</label>
                                <input type="text" className="form-control" ref="recipient" placeholder="johnsmith@internet.gov" id="recipient"/>
                                    <label for="msginput">Message:</label>
                                    <input type="text" className="form-control" ref="msginput" placeholder="Type Message Here..." id="msginput"/>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-default" data-dismiss="modal" onClick={this.sendMessage.bind(this)}>Send</button>
                            </div>
                        </div>
                    </div>
                </div>

                {this.renderConversations()}

                {this.getloadMoreConversationsButton()}

            </div>
        )
    }
}
