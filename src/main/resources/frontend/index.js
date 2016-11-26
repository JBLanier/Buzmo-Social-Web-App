import React from 'react'
import { render } from 'react-dom'
import { Router, Route, hashHistory, IndexRoute } from 'react-router'
import App from './app/App'
import InApp from './app/InApp'
import Login from './app/Login'
import Messages from './app/Messages'
import MyCircle from './app/MyCircle'

render((
    <Router history={hashHistory}>
        <Route path="/" component={App}>
            <IndexRoute component={Login}/>
            <Route component={InApp} >
                <Route path="/messages" component={Messages}/>
                <Route path="/mycircle" component={MyCircle}/>
            </Route>
        </Route>
    </Router>
), document.getElementById('app'));