import React from 'react'
import $ from 'jquery'
import Store from './Store'
import { hashHistory } from 'react-router'
import {CSVSplit} from './Toolbox'

export default class extends React.Component {
    
    constructor() {
        super();
        this.state = {
            "friends": [],
            "friendIds": {},
            "requests": [],
            "invites": [], //group chats
            "email":"",
            "topics": "",
            "n":"",
            "m":"",
            "results": [],
            "showResults": false,
            "userId": 0
        };
    }

    search() {
        let url = "http://localhost:8080/api/friends/search";
        
        new Store().getUser(function(user) {
            this.setState({userId: user.userid});
            //prepare topics
            let topics = this.state.topics.replace(/[^a-z\sA-Z0-9,]/g, "");
            topics = CSVSplit(topics);
            //send request
            new Store().getAuth(function (auth) {
                $.ajax({
                    method: "POST",
                    url: url,
                    beforeSend: function (request)
                    {
                        request.setRequestHeader("auth_token", auth);
                    },
                    data: JSON.stringify({
                        topics: topics.length > 0 ? topics : null,
                        email: this.state.email || null,
                        n: this.state.n || null,
                        m: this.state.m || null
                    }),
                    contentType: "application/json"
                })
                    .done((results) => {
                        console.log("Results obtained!");
                        this.setState({results, showResults: true});
                    })
                    .fail(function (err) {
                        alert("Could not search topics: " + JSON.stringify(err));
                    });

            },this);
        }, this);
    }

    getFriends() {
        return this.state.friends.map((fri, idx) => {
            return (<li key={idx}>{fri.screenname} ({fri.email})</li>);
        });
    }
    
    loadList() {
        new Store().getAuth((auth) => {
            console.log("Getting friends for user.");
            $.ajax({
                method: "GET",
                url: "http://localhost:8080/api/friends/list",
                beforeSend: function (request) {
                    request.setRequestHeader("auth_token", auth);
                }
            })
                .done((friends) => {
                    console.log("Results obtained!");
                    let friendIds = {};
                    friends.map((friend) => {
                        friendIds[friend.userid] = true;
                    });
                    this.setState({friends, friendIds});
                })
                .fail(function (err) {
                    alert("Could not load friends: " + JSON.stringify(err));
                });
        });
    }

    respond(mid, accept) {
        new Store().getAuth((auth) => {
            console.log("responding to friend request");
            $.ajax({
                method: "POST",
                url: `http://localhost:8080/api/friends/request/respond?mid=${mid}&accept=${accept}`,
                beforeSend: function (request) {
                    request.setRequestHeader("auth_token", auth);
                }
            })
                .done(() => {
                    if (accept === "true") {
                        alert('Friendship Created!');
                    } else {
                        alert('Friendship offer rejected!');
                    }
                    hashHistory.push('/friends');
                })
                .fail(function (err) {
                    alert("Could not load friends: " + JSON.stringify(err));
                });
        });
    }

    getRequests() {
        return this.state.requests.map((req, idx) => {
            return (<li key={idx}>{req.sender_name} <br /><button onClick={() => {this.respond(req.mid, "true")}} className="btn btn-default btn-xs"><span className="glyphicon glyphicon-ok"></span></button> <button onClick={() => {this.respond(req.mid, "false")}} className="btn btn-xs btn-default"><span className="glyphicon glyphicon-remove"></span></button></li>);
        });
    }

    addFriend(userid) {
        new Store().getAuth((auth) => {
            console.log("sending friend request");
            $.ajax({
                method: "POST",
                url: `http://localhost:8080/api/friends/request/create?other=${userid}`,
                beforeSend: function (request) {
                    request.setRequestHeader("auth_token", auth);
                }
            })
                .done(() => {
                    alert('Friend request sent!');
                })
                .fail(function (err) {
                    alert("Could not send request. " + JSON.stringify(err));
                });
        });
    }

    getResults() {
        return this.state.results.map((user,idx) => {
            return (<li key={idx}>
                {user.screenname} ({user.email}) {(this.state.friendIds[user.userid] ?
                <span className="glyphicon glyphicon-user"></span> :
                ((this.state.userId !== user.userid) ? <button onClick={() => {this.addFriend(user.userid)}} className="btn btn-default btn-xs"><span className="glyphicon glyphicon-plus"></span></button> : "(That's you!)")
            )}
            </li>);
        });
    }

    respondInvite(cgid, accept) {
        new Store().getAuth((auth) => {
            console.log("responding to chat group invite.");
            $.ajax({
                method: "POST",
                url: `http://localhost:8080/api/chatgroups/invite/respond?cgid=${cgid}&accept=${accept}`,
                beforeSend: function (request) {
                    request.setRequestHeader("auth_token", auth);
                }
            })
                .done(() => {
                    if (accept === "true") {
                        alert('You are now a member!');
                    } else {
                        alert('Invite rejected.');
                    }
                })
                .fail(function (err) {
                    alert("Could not respond to chat group invite: " + JSON.stringify(err));
                });
        });
    }

    getInvites() {
        return this.state.invites.map((invite,idx) => {
            return (
                <li key={idx}>
                    {invite.groupName} from {invite.invitedBy}
                    <br />
                    <button onClick={() => {this.respondInvite(invite.cgid, "true")}} className="btn btn-default btn-xs"><span className="glyphicon glyphicon-ok"></span></button> <button onClick={() => {this.respondInvite(invite.cgid, "false")}} className="btn btn-xs btn-default"><span className="glyphicon glyphicon-remove"></span></button>
                </li>
            );
        });
    }

    loadRequests(){
        new Store().getAuth((auth) => {
            console.log("Getting friend requests for user.");
            $.ajax({
                method: "GET",
                url: "http://localhost:8080/api/friends/requests",
                beforeSend: function (request) {
                    request.setRequestHeader("auth_token", auth);
                }
            })
                .done((requests) => {
                    console.log("Results obtained!");
                    this.setState({requests});
                })
                .fail(function (err) {
                    console.log("Could not load friend requests: " + JSON.stringify(err));
                });
        });
    }

    loadInvites(){
        new Store().getAuth((auth) => {
            console.log("Getting group chat invites for user.");
            $.ajax({
                method: "GET",
                url: "http://localhost:8080/api/chatgroups/invite/list",
                beforeSend: function (request) {
                    request.setRequestHeader("auth_token", auth);
                }
            })
                .done((invites) => {
                    console.log("Results obtained!");
                    this.setState({invites});
                })
                .fail(function (err) {
                    console.log("Could not load group invites: " + JSON.stringify(err));
                });
        });
    }

    componentDidMount() {
        this.loadList();
        this.loadRequests();
        this.loadInvites();
    }
    
    onEmailChange(e){
        this.setState({email:e.target.value});
    }
    onTopicsChange(e){
        this.setState({topics:e.target.value});
    }
    onNChange(e){
        this.setState({n:e.target.value});
    }
    onMChange(e){
        this.setState({m:e.target.value});
    }
    render() {
        return (
            <div className="container mainContainer">
                <div className="row">
                    <div className="container">
                        <h3>Friendship Center</h3>
                        <div className="row">
                            <div className="col-sm-3">
                                <h4>Friend Requests</h4>
                                <ul>
                                    {this.getRequests()}
                                </ul>
                                <h4>GroupChat Invites</h4>
                                <ul>
                                    {this.getInvites()}
                                </ul>
                                <h4>My Circle of Friends</h4>
                                <ul>
                                    {this.getFriends()}
                                </ul>
                            </div>
                            <div className="col-sm-9">
                                <h4>Find Friends</h4>
                                <p>Leave a field blank to ignore a filter.</p>
                                <div className="form-group">
                                    <label>Email</label>
                                    <input onChange={this.onEmailChange.bind(this)} type="text" className="form-control" placeholder="email@buzmo.com"/>
                                </div>
                                <div className="form-group">
                                    <label>Topics</label>
                                    <input onChange={this.onTopicsChange.bind(this)} type="text" className="form-control" placeholder="topic1,topic2,..."/>
                                </div>
                                <div className="form-group">
                                    <label>Most recent public posting within last n days.</label>
                                    <input onChange={this.onNChange.bind(this)} type="number" min="1" step="1" className="form-control" placeholder="n"/>
                                </div>
                                <div className="form-group">
                                    <label>m or more public messages sent within last 7 days.</label>
                                    <input onChange={this.onMChange.bind(this)} type="number" min="0" step="1" className="form-control" placeholder="m"/>
                                </div>
                                <button onClick={this.search.bind(this)} className="btn btn-default">Search!</button>
                                { this.state.showResults ? 
                                    <div>
                                        <h4>Search Results</h4>
                                        <ul>
                                            {this.getResults()}
                                        </ul>
                                    </div> : "" }
                                
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}