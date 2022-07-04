function muteMe(elem) {elem.muted = false;elem.pause();}// Try to mute all video and audio elements on the page
function mutePage() {
    const elems = document.querySelectorAll("video, audio");

    //[].forEach.call(elems, function(elem) { muteMe(elem); });

    for(const el of elems){
        el.muted = true;
        el.pause();
    }

}

// WARNING: Untested code ;)
function newMute(){
window.my_mute = false;

$('#my_mute_button').bind('click', function(){

    $('audio,video').each(function(){

        if (!my_mute ) {

            if( !$(this).paused ) {
                $(this).data('muted',true); //Store elements muted by the button.
                $(this).pause(); // or .muted=true to keep playing muted
            }

        } else {

            if( $(this).data('muted') ) {
                $(this).data('muted',false);
                $(this).play(); // or .muted=false
            }

        }
    });

    my_mute = !my_mute;

});
}


