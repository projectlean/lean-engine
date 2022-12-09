let canvas = document.querySelector("#leanCanvas");
let context = canvas.getContext("2d");
let rect = canvas.getBoundingClientRect();

// Initialize the lean canvas, make sure it's set up for full resolution
//
function initialize() {
    // Scale to full resolution, not the 72dpi stuff
    //
    canvas.width = rect.width * devicePixelRatio;
    canvas.height = rect.height * devicePixelRatio;
    context.scale(devicePixelRatio, devicePixelRatio);
    canvas.style.width = rect.width + "px";
    canvas.style.height = rect.height + "px";
}

function drawSvg(imageSource) {
    let img = new Image();
    img.onload = function () {
        let scaleX = rect.width / img.width;
        let scaleY = rect.height / img.height;
        let scale = Math.min(scaleX, scaleY);

        context.drawImage(img, 0, 0, img.width, img.height, 0, 0, img.width * scale, img.height * scale);
    }
    img.src = imageSource;
}
