
/*
 * Setting up block level variable to store class state
 * , set's to null by default.
 */
let instance = null;

class UserDataManager{
    constructor() {
        if(!instance){
            instance = this;
        }

        return instance;
    }

    setUserInformation(uid, screenname, email, phone, is_manager) {
        instance.userobject = {uid, screenname, email, phone, is_manager};
    }
}

