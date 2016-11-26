import React from 'react'
import $ from 'jquery'

export default class extends React.Component {
    
    login() {
        console.log('clicked');

        console.log("email: " + this.refs.emailText.value);

        $.ajax({
            method: "POST",
            url: "http://localhost:8080/api/auth",
            data: JSON.stringify({ email: this.refs.emailText.value, password: this.refs.passwordText.value }),
            contentType: "application/json"
        })
        .done(function( data ) {
            //hashHistory.push('/messages');
            this.props.history.
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
