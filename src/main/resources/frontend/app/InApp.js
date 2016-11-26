import React from 'react'
import Navbar from './Navbar'

export default class extends React.Component {
    render() {
        return (
            <div>
                <Navbar></Navbar>
                {this.props.children}
            </div>
        )
    }
}
