import React from 'react'
import Store from './Store'
import {UTCToString} from './Toolbox'

function isNormalPositiveInteger(str) {
    let n = ~~Number(str);
    return String(n) === str && n > 0;
}

export default class extends React.Component {

    constructor() {
        super();
        this.state = {screenname: "",
                        isManager: false,
                        time: ""};
    }

    getClassNameActiveMyCircle() {
        if (window.location.href.includes("#/mycircle")) {
            return "active";
        } else {
            return "";
        }
    }

    getClassNameActiveMessages() {
        if (window.location.href.includes("#/messages")) {
            return "active";
        } else {
            return "";
        }
    }

    getClassNameActiveFriends() {
        if (window.location.href.includes("#/friends")) {
            return "active";
        } else {
            return "";
        }
    }

    componentDidMount() {
        new Store().getUser(function (user) {
            this.screennameSet = true;
            this.setState({screenname: user.screenname,
                            isManager: user.isManager,
                            time: this.state.time});
        }, this);

    }

    renderManagerOps() {
        if (this.state.isManager) {
            return (
                    <li>
                        <a><span className="glyphicon glyphicon-console"
                                 data-toggle="modal" data-target="#managerModal" onClick={this.getTime.bind(this)}></span></a>

                    </li>

            )
        }
    }

    getTime(){
        new Store().getAuth(function (auth) {
            $.ajax({
                method: "GET",
                url: "http://localhost:8080/api/time/get",
                beforeSend: function (request)
                {
                    request.setRequestHeader("auth_token", auth);
                },

                data: null,
                contentType: null
            })
                .done(function (data) {
                    this.setState({
                        screenname: this.state.screenname,
                        isManager: this.state.isManager,
                        time: data
                    })
                }.bind(this))
                .fail(function (err) {
                    alert("Something went wrong with getting the time");
                });

        },this);
    }

    setNewTime(){

        console.log(this.refs);

        let newTime = this.refs.settime.value;

        if(newTime == "") {
            alert("You have to type something");
            return;
        }

        if (isNormalPositiveInteger(newTime)) {
            alert("setting new time...");
            new Store().getAuth(function (auth) {
                $.ajax({
                    method: "POST",
                    url: "http://localhost:8080/api/time/set?utc="+newTime,
                    beforeSend: function (request)
                    {
                        request.setRequestHeader("auth_token", auth);
                    },

                    data: null,
                    contentType: null
                })
                    .done(function () {
                        alert("New Time " + newTime + " has been set!");
                    })
                    .fail(function (err) {
                        alert("Something went wrong with setting the time");
                    });

            },this);

        } else {
            alert("sorry the time entered wasn't a normal positive integer");

        }

        this.refs.settime.value = "";

    }

    render() {



        return (
            <div>
                <nav className="navbar navbar-default navbar-fixed-top">
                <div className="container-fluid">
                { /* Brand and toggle get grouped for better mobile display --> */ }
                <div className="navbar-header">
                    <button type="button" className="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                        <span className="icon-bar"></span>
                        <span className="icon-bar"></span>
                        <span className="icon-bar"></span>
                    </button>
                    <a className="navbar-brand" href="#">Buzmo</a>
                </div>
                    {/* Collect the nav links, forms, and other content for toggling --> */}
                <div className="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                    <ul className="nav navbar-nav">
                        <li className={this.getClassNameActiveMessages()} ><a href="#/messages"><span className="glyphicon glyphicon-envelope"></span></a></li>
                        <li className={this.getClassNameActiveMyCircle()} ><a href="#/mycircle"><span className="glyphicon glyphicon-globe"></span></a></li>
                        <li className={this.getClassNameActiveFriends()} ><a href="#/friends"><span className="glyphicon glyphicon-user"></span></a></li>
                    </ul>
                    <ul className="nav navbar-nav navbar-right">
                        {this.renderManagerOps()}
                        <li className="navbar-text">{this.state.screenname}</li>
                        <li><a href="#"><span className="glyphicon glyphicon-log-out" onClick={function() {new Store().flush();}}></span></a></li>
                    </ul>
                </div>{ /* <!-- /.navbar-collapse --> */ }
            </div> { /* <!-- /.container-fluid --> */ }

            </nav>


            <div className="modal fade" id="managerModal" role="dialog">
                <div className="modal-dialog modal-lg">
                    <div className="modal-content">
                        <div className="modal-header">
                            <button type="button" className="close" data-dismiss="modal">&times;</button>
                            <h4 className="modal-title">Manager Ops</h4>
                        </div>
                        <div className="modal-body">

                            <label for="settime">Set New Time, Current time is: {this.state.time + " "}
                                {UTCToString(this.state.time)}</label>
                            <div className="input-group">
                                <input type="text" className="form-control" ref="settime" placeholder="UTC in milliseconds" id="settime"/>
                                <span className="input-group-btn">
                                                <button className="btn btn-default" type="button" onClick={this.setNewTime.bind(this)}>Set Time</button>
                                            </span>
                            </div>


                            <span className="input-group-btn">
                                <button className="btn btn-default" type="button">Generate Report</button>
                            </span>
                        </div>
                        <div className="modal-footer">
                            <button type="button" className="btn btn-default" data-dismiss="modal">Exit</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        )
    }
}
