#!/bin/bash
site put-api -n card -f openapi.edn
site put-asset --file templates/index-template.html --type 'text/html;charset=utf-8' --path /templates/card/index.html
site post-resources --file resources/resources.edn
site post-resources --file resources/permissions.edn

if [ -f public/js/main.js ]
then
    site put-asset --file public/js/main.js --type 'application/javascript' --path /card/js/main.js
fi
