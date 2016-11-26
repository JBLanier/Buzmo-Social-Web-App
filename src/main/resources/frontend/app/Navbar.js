import React from 'react'

export default class extends React.Component {
    render() {
        return (
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
                    <li className="active"><a href="#"><span className="glyphicon glyphicon-envelope"></span></a></li>
                    <li><a href="#"><span className="glyphicon glyphicon-globe"></span></a></li>
                    <li><a href="#"><span className="glyphicon glyphicon-user"></span></a></li>
                </ul>
                <ul className="nav navbar-nav navbar-right">
                    <li><a href="#"><span className="glyphicon glyphicon-log-out"></span></a></li>
                </ul>
            </div>{ /* <!-- /.navbar-collapse --> */ }
        </div> { /* <!-- /.container-fluid --> */ }
        </nav>
        )
    }
}
