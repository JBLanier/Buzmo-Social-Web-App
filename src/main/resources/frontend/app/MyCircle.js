import React from 'react'
import $ from 'jquery'
import Store from './Store'
import MyCircleMessage from './MyCircleMessage'

export default class extends React.Component {
    
    constructor() {
        super();
        this.state = {
            "qMatching": "any",
            "qTopics": "",
            "messages" : []
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
    search() {
        let url;
        if (this.state.qMatching === "any") {
            url = "http://localhost:8080/api/mycircle/search/any";
        } else {
            url = "http://localhost:8080/api/mycircle/search/all";
        }

        //prepare topics
        let topics = this.state.qTopics.replace(/[^a-zA-Z0-9,]/g, "");
        topics = topics.split(",");
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
                    this.setState({messages});
                })
                .fail(function (err) {
                    alert("Could not search topics: " + JSON.stringify(err));
                });

        },this);
    }

    getMessages() {
        return this.state.messages.map((msg) => {
            return (<MyCircleMessage key={msg.mid} msg={msg.msg} utc={msg.utc} screenname={msg.screenname} topics={msg.topics} />);
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
                    this.setState({messages});
                })
                .fail(function (err) {
                    alert("Could not load mycircle messages: " + JSON.stringify(err));
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
                        <h3>MyCircle</h3>
                        <div className="row">
                            <div className="col-lg-6">
                                <div className="input-group">
                                    <div className="input-group-addon">Given topics</div>
                                    <input type="text" className="form-control" placeholder="topic1,topic2,topic3" onChange={this.onTopicsChange.bind(this)} />
                                    <div className="input-group-addon">find messages</div>
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
                            <div className="panel panel-default btn load-more-button">
                                <div className="panel-body">
                                    Load More
                                </div>
                            </div>
                        </div>
                        <div className="input-group" id="mycircle-input-box">
                            <input type="text" className="form-control" placeholder="Type Message..."/>
                            <span className="input-group-btn">
                                        <button className="btn btn-default" type="button">Send</button>
                                    </span>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}