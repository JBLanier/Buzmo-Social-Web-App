import React from 'react'

export default class extends React.Component {

    constructor() {
        super();
    }

    render() {

        return (
            <input type="text" className="form-control" placeholder={this.props.placeholder} onChange={this.props.onChange}/>
        )
    }
}