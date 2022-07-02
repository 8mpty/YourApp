(function() {
    'use strict';

    // Your code here...
    //MutationObserver
    const elementToObserve = document.querySelector("#main-panel");
    //Remove
    const observer = new MutationObserver(function() {
        elementToObserve.remove();
    });

    observer.observe(elementToObserve, {subtree: true, childList: true});

    // Old Code. Still Works BTW
    //document.getElementById('main-panel').style.display='none';
    //document.getElementsByClassName('style-scope ytd-button-renderer')[0].style.display='none';

})();