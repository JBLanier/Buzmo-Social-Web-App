
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

    setUserInformation(uid, screenname, email, phone, is_manager) {
        instance.userobject = {uid, screenname, email, phone, is_manager};
    }

    getHost() {
        const host =  window.location.hostname + ":" + 8080;
        console.log("HOST IS : " + host);
        return host;
    }
}

