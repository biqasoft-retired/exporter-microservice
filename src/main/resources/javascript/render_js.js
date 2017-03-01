/**
 * @author Nikita Bakaev, ya@nbakaev.ru
 * Date: 5/23/2016
 * All Rights Reserved
 */

var fs = require('fs');

var page = require('webpage').create(),
    system = require('system'),
    address, output, size, pageWidth, pageHeight;

if (system.args.length < 3 || system.args.length > 5) {
    console.log('Usage: render_js.js URL filename [paperwidth*paperheight|paperformat] [zoom]');
    console.log('  paper (pdf output) examples: "5in*7.5in", "10cm*20cm", "A4", "Letter"');
    console.log('  image (png/jpg output) examples: "1920px" entire page, window width 1920px');
    console.log('                                   "800px*600px" window, clipped to 800x600');
    phantom.exit(1);
} else {
    address = system.args[1];
    output = system.args[2];

    page.open(address, function (status) {
        if (status !== 'success') {
            console.log('Unable to load the address!');
            phantom.exit(1);
        } else {
            window.setTimeout(function () {

                var p = page.evaluate(function () {
                    return document.getElementsByTagName('html')[0].innerHTML
                });
                // console.log(p);
                fs.write(output, p);

                // page.render(output);
                phantom.exit();
            }, 200);
        }
    });

}