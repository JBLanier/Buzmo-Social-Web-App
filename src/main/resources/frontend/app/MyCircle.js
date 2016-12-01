import React from 'react'
import $ from 'jquery'
import Store from './Store'
import MyCircleMessage from './MyCircleMessage'
import {CSVSplit} from './Toolbox'
import {uniq} from './Toolbox'

const MC_FETCH_SIZE = 7;

export default class extends React.Component {
    
    constructor() {
        super();
        this.state = {
            "qMatching": "any",
            "qTopics": "",
            "messages" : [],
            "isMore": true,
            "sMsg":"",
            "sPublic": false,
            "sRecipients": "",
            "sTopics": ""
        };
    }

    onTopicsChange(e) {
        this.setState({"qTopics": e.target.value});
    }
    setAllTopics() {
        this.setState({"qMatching": "all"})
    }
    setAnyTopics() {
        this.setState({"qMatching": "any"})
    }
    
    setPublic(e) {
        this.setState({"sPublic": (e.target.value === "on")});
    }
    setMsg(e) {
        this.setState({"sMsg": e.target.value});
    }
    setRecipients(e) {
        this.setState({"sRecipients": e.target.value});
    }
    setPostTopics(e) {
        this.setState({"sTopics": e.target.value});
    }
    search() {
        let url;
        if (this.state.qMatching === "any") {
            url = "http://localhost:8080/api/mycircle/search/any";
        } else {
            url = "http://localhost:8080/api/mycircle/search/all";
        }

        //prepare topics
        let topics = this.state.qTopics.replace(/[^a-zA-Z0-9\s,]/g, "");
        topics = CSVSplit(topics);
        topics = uniq(topics);
        //send request
        new Store().getAuth(function (auth) {
            console.log("Searching my circle messages for topics: ", topics);
            $.ajax({
                method: "POST",
                url: url,
                beforeSend: function (request)
                {
                    request.setRequestHeader("auth_token", auth);
                },
                data: JSON.stringify({topics}),
                contentType: "application/json"
            })
                .done((messages) => {
                    console.log("Results obtained!");
                    this.setState({messages, isMore: false});
                })
                .fail(function (err) {
                    alert("Could not search topics: " + JSON.stringify(err));
                });

        },this);
    }
    
    post() {
        if (this.state.sMsg === "") return;
        let recipients = this.state.sRecipients.replace(/[^a-zA-Z0-9@.\-,]/g, "");
        let topics = this.state.sTopics.replace(/[^a-z\sA-Z0-9,]/g, "");
        topics = CSVSplit(topics);

        topics = uniq(topics);
        recipients = uniq(recipients);

        for (var i = 0, len = topics.length; i < len; i++) {
            if (topics[i].length > 30) {
                alert(topics[i] + " is too long for a topic (30 characters max)");
                return;
            }
        }

        recipients = CSVSplit(recipients);
        new Store().getAuth(function (auth) {
            console.log("Posting a new message.");
            $.ajax({
                method: "POST",
                url: "http://localhost:8080/api/friends/check",
                beforeSend: function (request)
                {
                    request.setRequestHeader("auth_token", auth);
                },
                data: JSON.stringify(recipients),
                contentType: "application/json"
            })
                .done((result) => {
                    if (!result) {
                        alert ("You are not friends with all those users!")
                    } else {
                        $.ajax({
                            method: "POST",
                            url: "http://localhost:8080/api/mycircle/create",
                            beforeSend: function (request)
                            {
                                request.setRequestHeader("auth_token", auth);
                            },
                            data: JSON.stringify({
                                recipients,
                                utc: (new Date()).getTime(),
                                topics,
                                public: this.state.sPublic,
                                msg: this.state.sMsg
                            }),
                            contentType: "application/json"
                        })
                            .done(() => {
                                alert("Message Posted!");
                            })
                            .fail(function (err) {
                                alert("Could not post message: " + JSON.stringify(err));
                            });
                    }
                })
                .fail(function (err) {
                    alert("Could not post message: " + JSON.stringify(err));
                });

        },this);
    }

    getMessages() {
        return this.state.messages.map((msg) => {
            return (<MyCircleMessage userid={msg.userid} public={msg.public} key={msg.mid} mid={msg.mid} msg={msg.msg} utc={msg.utc} screenname={msg.screenname} readCount={msg.readCount} topics={msg.topics} />);
        });
    }
    
    loadList() {
        new Store().getAuth((auth) => {
            console.log("Getting my circle messages for user.");
            $.ajax({
                method: "GET",
                url: "http://localhost:8080/api/mycircle/list",
                beforeSend: function (request) {
                    request.setRequestHeader("auth_token", auth);
                }
            })
                .done((messages) => {
                    console.log("Results obtained!");
                    let isMore = true;
                    if (messages.length < MC_FETCH_SIZE) {
                        isMore = false;
                    }
                    this.setState({messages, isMore});
                })
                .fail(function (err) {
                    alert("Could not load mycircle messages: " + JSON.stringify(err));
                });
        });
    }

    loadMoreList() {
        //Get utc of oldest message.
        let utc = this.state.messages[this.state.messages.length - 1].utc;
        console.log("Loading more messages...");
        new Store().getAuth((auth) => {
            $.ajax({
                method: "GET",
                url: `http://localhost:8080/api/mycircle/list?before=${utc}`,
                beforeSend: function (request) {
                    request.setRequestHeader("auth_token", auth);
                }
            })
                .done((messages) => {
                    console.log("Results obtained!");
                    console.log(messages);
                    let isMore = true;
                    if (messages.length < MC_FETCH_SIZE) {
                        isMore = false;
                    }
                    let old = this.state.messages.slice();
                    let newMessages = old.concat(messages);
                    this.setState({messages:newMessages, isMore});
                })
                .fail(function (err) {
                    alert("Could not load more mycircle messages: " + JSON.stringify(err));
                });
        });
    }
    
    componentDidMount() {
        this.loadList();
    }
    render() {
        return (
            <div className="container-fluid mainContainer">
                <div className="row full-height">
                    <div className="container full-height">
                        <h3>MyCircle <button onClick={this.loadList.bind(this)} className="btn btn-default"><span className="glyphicon glyphicon-home"></span></button></h3>
                        <div className="row">
                            <div className="col-lg-12">
                                <div className="input-group">
                                    <div className="input-group-addon">Given topics</div>
                                    <input type="text" className="form-control" placeholder="topic1,topic2,topic3"
                                           onChange={this.onTopicsChange.bind(this)} maxLength="2256"/>
                                    <div className="input-group-addon">find at most 7 recent public messages</div>
                                    <div className="input-group-btn">
                                        <button type="button" className="btn btn-default dropdown-toggle" data-toggle="dropdown">matching <strong>{this.state.qMatching}</strong> of these topics. <span className="caret"></span></button>
                                        <ul className="dropdown-menu">
                                            <li><a onClick={this.setAnyTopics.bind(this)}>matching <strong>any</strong> of these topics.</a></li>
                                            <li><a onClick={this.setAllTopics.bind(this)}>matching <strong>all</strong> of these topics.</a></li>
                                        </ul>
                                    </div>
                                    <span className="input-group-btn">
                                        <button className="btn btn-default" type="button" onClick={this.search.bind(this)}><span className="glyphicon glyphicon-search"></span></button>
                                    </span>
                                </div>
                            </div>
                        </div>
                        <div className="pre-scrollable main-scrollable-content" id="scrollable-mycircle-list">
                            {this.getMessages()}
                            {
                                this.state.isMore ? 
                                <div onClick={this.loadMoreList.bind(this)} className="panel panel-default btn load-more-button">
                                    <div className="panel-body">
                                        Load More
                                    </div>
                                </div> : null
                            }
                        </div>
                        <div id="mycircle-input-box">
                            <div className="input-group">
                                <div className="input-group-addon">
                                    <span className="glyphicon glyphicon-globe"></span> <input onChange={this.setPublic.bind(this)} type="checkbox" />
                                </div>
                                <input type="text" onChange={this.setRecipients.bind(this)} className="form-control"
                                       placeholder="email1@buzmo.com,email2@buzmo.com,... (default is all your friends)"
                                       maxLength="2256"/>
                            </div>
                            <input type="text" onChange={this.setPostTopics.bind(this)} className="form-control"
                                   placeholder="topic1,topic2,... (default is all your topics)" maxLength="2256"/>
                            <div className="input-group">
                                <input type="text" onChange={this.setMsg.bind(this)} className="form-control"
                                       placeholder="Type Message..." maxLength="1400"/>
                                <span className="input-group-btn">
                                    <button onClick={this.post.bind(this)} className="btn btn-default" type="button">Post!</button>
                                </span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}