import { Router, Route, hashHistory, IndexRoute, browserHistory} from 'react-router'
/*
 * Setting up block level variable to store class state
 * , set's to null by default.
 */
let instance = null;

export default class Store {
    constructor() {
        if(!instance){
            instance = this;
        }

        return instance;
    }

    getUser() {
       if (this.user != undefined) {
           return this.user;
       } else {

           $.ajax({
               method: "GET",
               url: "http://localhost:8080/api/user/profile",
               data: null,
               contentType: null
           })
               .done(function( data ) {
                   if(data === undefined) {
                       hashHistory.push('#')
                   } else {
                       this.user = data;
                       return this.user;
                   }
               })
               .fail(function(err) {
                   hashHistory.push('#')
               });
       }
    }

    getHost() {
        const host =  window.location.hostname + ":" + 8080;
        console.log("HOST IS : " + host);
        return host;
    }
}

