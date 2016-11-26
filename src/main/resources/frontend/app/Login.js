import React from 'react'

export default class extends React.Component {
    render() {
        return (
            <div className="loginScreen">
                <div className="panel loginPanel centered">
                    <div className="panel-body">
                        <h1>Buzmo</h1>
                        <div className="form-group">
                            <label>Email address</label>
                            <input type="email" className="form-control" placeholder="Email" />
                        </div>
                        <div className="form-group">
                            <label>Password</label>
                            <input type="password" className="form-control" placeholder="Password" />
                        </div>
                        <a href="#" className="btn btn-default" style={{width:"100%"}}>Login</a>
                    </div>
                </div>
            </div>
        )
    }
}
