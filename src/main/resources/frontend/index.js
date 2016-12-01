import React from 'react'
import { render } from 'react-dom'
import { Router, Route, hashHistory, IndexRoute, browserHistory} from 'react-router'
import App from './app/App'
import InApp from './app/InApp'
import Login from './app/Login'
import Messages from './app/Messages'
import MyCircle from './app/MyCircle'
import SignUp from './app/SignUp'
import Friends from './app/Friends'
import Report from './app/Report'

render((
    <Router history={hashHistory}>
        <Route path="/" component={App}>
            <IndexRoute component={Login}/>
            <Route component={InApp} >
                <Route path="/messages" component={Messages}/>
                <Route path="/mycircle" component={MyCircle}/>
                <Route path="/friends" component={Friends}/>
                <Route path="/report" component={Report}/>
            </Route>
            <Route path="/signup" component={SignUp}/>
        </Route>
    </Router>
), document.getElementById('app'));