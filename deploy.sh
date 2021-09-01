#!/bin/bash
site put-api -n card -f openapi.edn
site put-asset --file templates/index-template.html --type 'text/html;charset=utf-8' --path /templates/card/index.html
site put-asset --file public/index.html --type 'text/html;charset=utf-8' --path /card/index.html
site post-resources --file resources/resources.edn
site post-resources --file resources/permissions.edn

if [ -f public/js/app.js ]
then
    site put-asset --file target/js/app.js --type 'application/javascript' --path /js/app.js
    site put-asset --file target/css/main.css --type 'text/css' --path /css/main.css
fi
