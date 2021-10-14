#!/bin/bash
site put-api -n card -f openapi.edn
site post-resources --file resources/permissions.edn
cd graphql
./deploy.sh
