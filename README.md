# Convert html to pdf, images, word

## Requirements
Following soft must be installed and accessible in $PATH
 - installed [phantomjs](http://phantomjs.org/)PATH
 - installed [pandoc](http://pandoc.org/)

## REST Endpoints
 - `POST /export/from/html/to/[html, pdf, png, jpg]` convert from html page in byte[] request to image/pdf/html with executed JS
 - `POST /export/from/html/to/pandoc?mime_type=application/vnd.openxmlformats-officedocument.wordprocessingml.document&extension=.docx` convert using pandoc