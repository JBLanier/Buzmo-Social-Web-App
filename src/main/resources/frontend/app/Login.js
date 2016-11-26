import React from 'react'
import $ from 'jquery'
import {hashHistory} from 'react-router'

export default class extends React.Component {
    
    login() {
        console.log('clicked');

        console.log("email: " + this.refs.emailText.value);

        $.post( "http://localhost:8080/api/auth", { email: this.refs.emailText.value, password: this.refs.passwordText.value })
        .done(function( data ) {
            //hashHistory.push('/messages');
            console.log("logged in!!!");
        })
        .fail(function(err) {
            alert("Could not login: " + JSON.stringify(err));
        });
    }
    
    render() {
        return (
            <div className="loginScreen">
                <div className="panel loginPanel centered">
                    <div className="panel-body">
                        <h1>Buzmo</h1>
                        <div className="form-group">
                            <label>Email address</label>
                            <input ref="emailText" type="email" className="form-control" placeholder="Email" />
                        </div>
                        <div className="form-group">
                            <label>Password</label>
                            <input ref="passwordText" type="password" className="form-control" placeholder="Password" />
                        </div>
                        <button onClick={this.login.bind(this)} href="#" className="btn btn-default" style={{width:"100%"}}>Login</button>
                    </div>
                </div>
            </div>
        )
    }
}
