// ==UserScript==
// @name YouTube Music - Disable are you sure dialog
// @description Simply disables the window.beforeunload handler by hooking the handler
// @namespace http://www.jackdye.co.uk/
// @match        *://music.youtube.com/*
// @license MIT
// @grant none
// @run-at document-start
// @version 1.1
// ==/UserScript==
(function(){
    var placeToReplace;
    if (window.EventTarget && EventTarget.prototype.addEventListener) {
      placeToReplace = EventTarget;
    } else {
      placeToReplace = Element;
    }

    placeToReplace.prototype.oldaddEventListener = placeToReplace.prototype.addEventListener;
    placeToReplace.prototype.addEventListener = function(event, handler, placeholder) {
      if(event == "beforeunload") {
          console.log("Youtube hook - disabling exit page handler");
          return;
      }
      if (arguments.length < 3) {
        this.oldaddEventListener(event, handler, false);
      } else {
        this.oldaddEventListener(event, handler, placeholder);
      }
    }
})();