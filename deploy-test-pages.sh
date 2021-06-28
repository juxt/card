#!/bin/bash
site post-resources --file resources/web-pages.edn
site put-asset --file resources/test-template.html --path /test-template.html --type "text/html;charset=utf-8"
