import React from 'react'
import $ from 'jquery'
import {hashHistory} from 'react-router'
import {CSVSplit} from './Toolbox'

export default class extends React.Component {
    
    constructor() {
        super();
        this.state = {
            "full_name": "",
            email: "",
            screenname: "",
            phone: "",
            passwd: "",
            topics: "",
            fnValid: false,
            emValid: false,
            snValid: false,
            phValid: false,
            psValid: false,
            tpValid: false
        }
    }

    signup() {
        if (!this.isReady()) return;
        let topics = this.state.topics.replace(/[^a-zA-Z0-9,]/g, "");
        topics = CSVSplit(topics);
        $.ajax({
            method: "POST",
            url: "http://localhost:8080/api/auth/signup",
            data: JSON.stringify({
                "full_name": this.state["full_name"],
                email: this.state.email,
                screenname: this.state.screenname,
                phone: this.state.phone,
                passwd: this.state.passwd,
                topics
            }),
            contentType: "application/json"
        })
            .done(function() {
                hashHistory.push('/');
            })
            .fail(function(err) {
                alert("Could not signup: " + JSON.stringify(err));
            });
    }
    
    isReady() {
        return this.state.fnValid && this.state.snValid && this.state.phValid && this.state.psValid && this.state.tpValid;
    }

    onFnChange(e) {
        this.setState({"full_name": e.target.value, fnValid: (e.target.value.length > 0 && e.target.value.length <= 20)});
    }

    onSnChange(e) {
        this.setState({screenname: e.target.value, snValid: (e.target.value.length > 0 && e.target.value.length <= 20)});
    }

    onPhChange(e) {
        this.setState({phone: e.target.value, phValid: (e.target.value.length == 10 && /^[0-9]+$/.test(e.target.value))});
    }

    onPsChange(e) {
        this.setState({passwd: e.target.value, psValid: (e.target.value.length >= 2 && e.target.value.length <= 10)});
    }

    onTpChange(e) {
        this.setState({topics: e.target.value, tpValid: (e.target.value.length > 0 && /^[0-9a-zA-Z,]+$/.test(e.target.value))});
    }
    
    onEmChange(e) {
        this.setState({email: e.target.value, emValid: (e.target.value.length > 0 && /^[0-9a-zA-Z\-.@]+$/.test(e.target.value))});
    }
    
    fvClass(isValid) {
        if (isValid) return "has-success";
        return "";
    }
    render() {
        return (
            <div className="loginScreen">
                <div className="container">
                    <div className="panel signupPanel">
                        <div className="panel-body">
                            <h1>Welcome :)</h1>
                            <div className={"form-group " + this.fvClass(this.state.fnValid)} >
                                <label>Name</label>
                                <input onChange={this.onFnChange.bind(this)} type="text" className="form-control" placeholder="Johnathan Smith" />
                            </div>
                                <div className={"form-group " + this.fvClass(this.state.emValid)} >
                                <label>Email address</label>
                                <input onChange={this.onEmChange.bind(this)} type="email" className="form-control" placeholder="jsmith@netscape.net" />
                            </div>
                            <div className={"form-group " + this.fvClass(this.state.snValid)} >
                                <label>Screenname</label>
                                <input onChange={this.onSnChange.bind(this)} type="text" className="form-control" placeholder="JSmith1" />
                            </div>
                            <div className={"form-group " + this.fvClass(this.state.phValid)} >
                                <label>Phone Number</label>
                                <input onChange={this.onPhChange.bind(this)} type="text" className="form-control" placeholder="8055555555" />
                            </div>
                            <div className={"form-group " + this.fvClass(this.state.psValid)} >
                                <label>Password</label>
                                <input onChange={this.onPsChange.bind(this)} type="password" className="form-control" placeholder="passw3rdz" />
                            </div>
                            <div className={"form-group " + this.fvClass(this.state.tpValid)} >
                                <label>Topic Words</label>
                                <input onChange={this.onTpChange.bind(this)} type="text" className="form-control" placeholder="topic1,topic2,... (CSV)" />
                            </div>
                            <button disabled={!this.isReady()} onClick={this.signup.bind(this)} className="btn btn-default" style={{width:"100%"}}>Signup</button>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}
