(function(){

    //document.getElementsByClassName('sign-in-link style-scope ytmusic-nav-bar')[0].style.display='none';

    // Hide SIGN IN button on YT Music
    if(document.getElementsByClassName('sign-in-link style-scope ytmusic-nav-bar')[0] !==undefined){
        let signIn = document.getElementsByClassName('sign-in-link style-scope ytmusic-nav-bar')[0];
        signIn.style.display="none";
    }

//    if (document.getElementsByClassName('mobile-topbar-header-sign-in-button')[0] !== undefined){
//        let msignIn = document.getElementsByClassName('mobile-topbar-header-sign-in-button')[0];
//        msignIn.style.display = "none";
//    }
    //.mobile-topbar-header-sign-in-button { display: none; }
})();