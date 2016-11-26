import React from 'react'


export default class extends React.Component {
    render() {
        return (
            <div className="container-fluid mainContainer">
                <div className="row full-height">
                    <div className="container full-height">
                        <h3>MyCircle</h3>
                        <div className="row">
                            <div className="col-lg-6">
                                <div className="input-group">
                                    <div className="input-group-btn">
                                        <button type="button" className="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">this is default text <span className="caret"></span></button>
                                        <ul className="dropdown-menu">
                                            <li><a href="#">Matching Any Topics</a></li>
                                            <li><a href="#">Matching All topics</a></li>
                                        </ul>
                                    </div>
                                    <input type="text" className="form-control" placeholder="Search MyCircle Messages"/>
                                    <span className="input-group-btn">
                                        <button className="btn btn-default" type="button">Go!</button>
                                    </span>
                                </div>
                            </div>
                        </div>
                        <div className="pre-scrollable main-scrollable-content" id="scrollable-mycircle-list">
            

            
            
            
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