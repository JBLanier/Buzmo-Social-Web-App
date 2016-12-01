import React from 'react'
import $ from 'jquery'
import Store from './Store'
import {UTCToString} from './Toolbox'

export default class extends React.Component {

    constructor() {
        super();
        this.state = {
        };
    }
    
    loadReport() {
        new Store().getAuth((auth) => {
            console.log("Getting group chat invites for user.");
            $.ajax({
                method: "GET",
                url: "http://localhost:8080/api/report",
                beforeSend: function (request) {
                    request.setRequestHeader("auth_token", auth);
                }
            })
                .done((report) => {
                    report.startUTC =  UTCToString(report.startUTC) + " UTC:  " + report.startUTC;
                    report.endUTC =  UTCToString(report.endUTC) + " UTC: " + report.endUTC;
                    report.lowActivityUsers.unshift("Amount: " + report.lowActivityUsers.length)
                    this.setState(report);
                })
                .fail(function (err) {
                    console.log("Could not load group invites: " + JSON.stringify(err));
                });
        });
    }

    componentDidMount() {
        this.loadReport();
    }
    
    render() {
        return (
            <div className="container mainContainer ">
                <h1>Your 7 Day Report</h1>
                <pre style={{maxHeight:"71vh"}}>
                    {JSON.stringify(this.state, null, 2)}
                </pre>
            </div>
        )
    }
}