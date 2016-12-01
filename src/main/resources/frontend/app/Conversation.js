import React from 'react'
import Store from './Store'

function isNormalPositiveInteger(str) {
    let n = ~~Number(str);
    return String(n) === str && n > 0;
}

export default class extends React.Component {

    constructor() {
        super();
        this.state = {isGroupOwner: false,
                      groupName: "Group Name",
                       duration: "duration"};

        this.queriedForGroupInfo = false;
    }

    checkGroup() {
        console.log(this.props);
        this.queriedForGroupInfo = true;
        console.log("---check group" + this.props.id);
        new Store().getUser(function(user){
            new Store().getAuth(function (auth) {
                $.ajax({
                    method: "GET",
                    url: "http://localhost:8080/api/chatgroups/?cgid=" + this.props.id,
                    beforeSend: function (request)
                    {
                        request.setRequestHeader("auth_token", auth);
                    },
                    data: null,
                    contentType: "application/json"
                })
                    .done(function (data) {
                            console.log(data);
                            if (user.userid == data.owner) {
                                this.setState({isGroupOwner: true,
                                    groupName: data.name,
                                    duration: data.duration});
                            } else {
                                this.setState({isGroupOwner: false,
                                    groupName: data.name,
                                    duration: data.duration});
                            }
                    }.bind(this))
                    .fail(function (err) {
                        console.log("checkGroup failed");
                    });

            },this)
        },this);
    }

    checkGroupMembership(cgid, userid, callback, context) {
        new Store().getAuth(function (auth) {
            $.ajax({
                method: "POST",
                url: "http://localhost:8080/api/chatgroups/checkmembership?cgid=" + cgid + "&userid="+ userid,
                beforeSend: function (request)
                {
                    request.setRequestHeader("auth_token", auth);
                },
                data: null,
                contentType: null
            })
                .done(function (data) {
                        callback.call(context, true);
                }.bind(this))
                .fail(function (err) {
                    callback.call(context, false);
                });

        },this)
    }

    getStyle() {
        if (this.props.active) {
            console.log("CONV KNOWS THAT ACTIVE IS: " + this.props.active);
            return {backgroundColor: "#555555", color : "#EEEEEE"};
        } return {};
    }

    onClick() {
        this.props.onClick(this);
    }

    getUserInfoFromEmail(email, callback, context) {
        new Store().getAuth(function (auth) {
            $.ajax({
                method: "GET",
                url: "http://localhost:8080/api/user?email=" + email,
                beforeSend: function (request)
                {
                    request.setRequestHeader("auth_token", auth);
                },
                data: null,
                contentType: null
            })
                .done(function (data) {
                    callback.call(context, data);
                })
                .fail(function (err) {
                    callback.call(context, undefined);
                });

        },this);
    }

    sendGroupInvite(){
        let email = this.refs.sendinv.value;

        if (email == "") {
            return;
        }

        this.refs.sendinv.value="";

        this.getUserInfoFromEmail(email,function(data){
            this.friendCheck(email,function(friend){
                this.checkGroupMembership(this.props.id,data.userid, function(inGroup) {
                    if (data != undefined && data !=null) {
                        if (friend == true) {
                            if(inGroup == false) {
                                new Store().getAuth(function (auth) {
                                    $.ajax({
                                        method: "POST",
                                        url: "http://localhost:8080/api/chatgroups/invite/create",
                                        beforeSend: function (request) {
                                            request.setRequestHeader("auth_token", auth);
                                        },

                                        //     private long mid;
                                        // private long recipient;
                                        // private String msg;
                                        // private long msg_timestamp;
                                        // private long sender;
                                        // private String sender_name;
                                        // private long cgid;


                                        data: JSON.stringify({
                                            recipient: data.userid,
                                            msg: "You're invited to join " + this.props.name,
                                            cgid: this.props.id
                                        }),
                                        contentType: "application/json"
                                    })
                                        .done(function (data) {
                                            alert("Message Sent!");
                                        })
                                        .fail(function (err) {
                                            alert("Something went wrong with sending the invite.\n" +
                                                "It's likely because they're already invited");
                                        });

                                }, this);
                            } else {
                                alert(email + " is already in " + this.props.name + ".");
                            }
                        } else {
                            alert("Sorry, you must be friends with " + email + " to invite them.");
                        }
                    } else {
                        alert("Sorry, " + email + "isn't on Buzmo.");
                    }
                },this)
            },this)
        },this)
    }

    friendCheck(email, callback, context) {
        new Store().getAuth(function (auth) {
            $.ajax({
                method: "POST",
                url: "http://localhost:8080/api/friends/check",
                beforeSend: function (request)
                {
                    request.setRequestHeader("auth_token", auth);
                },
                data: JSON.stringify([email]),
                contentType: "application/json"
            })
                .done(function (data) {
                    callback.call(context, data);
                })
                .fail(function (err) {
                    callback.call(context, undefined);
                });

        },this);
    }

    updateGroup(){

        let newName = this.refs.changename.value;
        let newDuration = this.refs.changeduration.value;

        if (newName == "" && newDuration == "") {
            alert("You didn't actually enter anything...");
            return;
        }

        if (newDuration != "" && !isNormalPositiveInteger(newDuration)) {
            alert("Sorry! Please enter a normal positive integer.");
            return;
        }

        if (newDuration == "") {
            newDuration = this.state.duration;
        }

        if (newName == "") {
            newName = this.state.groupName;
        }

        this.refs.changename.placeholder=newName;
        this.refs.changeduration.placeholer=newDuration;

        this.refs.changename.value="";
        this.refs.changeduration.value="";


        new Store().getAuth(function (auth) {
            $.ajax({
                method: "POST",
                url: "http://localhost:8080/api/chatgroups/update",
                beforeSend: function (request)
                {
                    request.setRequestHeader("auth_token", auth);
                },
                data: JSON.stringify({ cgid: this.props.id,
                                       name: newName,
                                       duration: newDuration}),
                contentType: "application/json"
            })
                .done(function (data) {
                    alert(this.props.name + " Updated!");
                }.bind(this))
                .fail(function (err) {
                    alert("Updating the group failed");
                });

        },this);
    }

    deleteGroup(){
        new Store().getAuth(function (auth) {
            $.ajax({
                method: "POST",
                url: "http://localhost:8080/api/chatgroups/delete?cgid=" + this.props.id,
                beforeSend: function (request)
                {
                    request.setRequestHeader("auth_token", auth);
                },
                data: null,
                contentType: null
            })
                .done(function (data) {
                    alert(this.props.name + " has been deleted!");
                }.bind(this))
                .fail(function (err) {
                    alert("Deleting the group failed");
                });

        },this);
    }


    renderGroupOwnerOperations(){
        if (this.state.isGroupOwner) {
            return (
                <div className="well">
                    <div className="row">
                        <h3>Owner Operations</h3>
                        <label for={"changename"+this.props.id}>Change group name:</label>
                        <input type="text" className="form-control" ref="changename"
                               placeholder={this.props.name} id={"changename"+this.props.id}
                               maxLength="20"/>

                        <label for={"changeduration"+this.props.id}>Change Message Duration:</label>
                        <input type="text" className="form-control" ref="changeduration"
                               placeholder={this.state.duration} id={"changeduration"+this.props.id}
                               maxLength="5"/>

                        <span className="input-group-btn">
                            <button className="btn btn-default" type="button"
                                    style={{marginTop: '15px', marginBottom: '15px'}}
                                    onClick={this.updateGroup.bind(this)}>Change</button>
                        </span>
                    </div>

                    <div className="row">
                        <span className="input-group-btn">
                            <button className="btn btn-warning" type="button"
                                    onClick={this.deleteGroup.bind(this)}>Delete Group</button>
                        </span>
                    </div>

                </div>
            )
        }
    }

    renderGroupOperationsButton() {
        if (this.props.pmMode==false) {
            return (
                <div>
                    <button className="cog-button" style={{float: 'right'}}><span
                    className="glyphicon glyphicon-cog" data-toggle="modal"
                    data-target={"#groupModel" + this.props.id}/>
                    </button>



                    <div className="modal fade" id={"groupModel" + this.props.id} role="dialog" style={{color: '#333'}}>
                        <div className="modal-dialog modal-lg">
                            <div className="modal-content">
                                <div className="modal-header">
                                    <button type="button" className="close" data-dismiss="modal">&times;</button>
                                    <h4 className="modal-title">{this.state.groupName}</h4>
                                </div>
                                <div className="modal-body">

                                    {this.renderGroupOwnerOperations()}

                                    <label for={"sendinv"+this.props.id}>Send Invite to:</label>
                                    <div className="input-group">
                                            <input type="text" className="form-control" ref="sendinv"
                                                    placeholder="guy@computernet.net" id={"sendinv"+this.props.id}
                                                   maxLength="20"/>
                                            <span className="input-group-btn">
                                                <button className="btn btn-default" type="button" onClick={this.sendGroupInvite.bind(this)}>Send</button>
                                            </span>
                                    </div>


                                </div>
                                <div className="modal-footer">
                                    <button type="button" className="btn btn-default" data-dismiss="modal">Done</button>

                                </div>
                            </div>
                        </div>
                    </div>

                </div>


            )
        }
    }

    render() {
        if (this.queriedForGroupInfo == false && this.props.pmMode == false) {
            this.checkGroup();
        }
        console.log("RENDER CONV");
        return (
            <div className="panel conversation" style={this.getStyle()}>
                {this.renderGroupOperationsButton()}
                <div className="panel-body" onClick={this.onClick.bind(this)}>
                    <h4>{this.props.name}</h4>
                </div>
            </div>
        )
    }

}