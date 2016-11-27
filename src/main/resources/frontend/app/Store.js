import { Router, Route, hashHistory, IndexRoute, browserHistory} from 'react-router'
/*
 * Setting up block level variable to store class state
 * , set's to null by default.
 */
let instance = null;

const notLoggedInUser = {
    auth_token : "Not Logged In",
    email : "Not Logged In",
    full_name : "Not Logged In",
    isManager : false,
    phone : 0,
    screenname : "Not Logged In",
    userid : 0
}

export default class Store {
    constructor() {
        if(!instance){
            instance = this;
        }

        return instance;
    }

    setUser(user) {
        this.user = user;
        localStorage.setItem('auth_token', user.auth_token );
    }

    //Will return undefined is not in memory;
    getUserImmediately() {
        return this.user;
    }

    getUser(callback, context) {
       if (this.user != undefined) {
           callback.call(context, this.user);
       } else {
           console.log("Get User called and user was undefined, recovering profile...");

           this.getAuth(function(auth) {
               $.ajax({
                   method: "GET",
                   beforeSend: function (request)
                   {
                       request.setRequestHeader("auth_token", auth);
                   },
                   url: "http://localhost:8080/api/user/profile",
                   data: null,
                   contentType: null
               })
                   .done(function( data ) {
                       if(data.userid == undefined) {
                           console.log("data returned for profile didn't have a userid, returning to login screen")
                           hashHistory.push('#')
                       } else {
                           console.log("Succesfully recovered user's profile");
                           console.log(data);
                           this.user = data;
                           this.user.auth_token = auth;
                           callback.call(context, this.user);
                       }
                   })
                   .fail(function(err) {
                       console.log("Unsuccesful with recovering user's profile");
                       hashHistory.push('#');
                       callback.call(context, notLoggedInUser);
                   });
           }, this);


       }
    }

    getAuth(callback, context) {
        if (this.user != undefined) {
            if (this.user.auth_token != undefined) {
                callback.call(context, this.user.auth_token);
            }
        } else {
            const token = localStorage.getItem('auth_token') || "No_Auth_In_Local_Storage";
            callback.call(context, token);
        }

    }

    getHost() {
        const host =  window.location.hostname + ":" + 8080;
        console.log("HOST IS : " + host);
        return host;
    }

    flush() {
        this.user = undefined;
        localStorage.clear();
    }
}

