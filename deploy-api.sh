#!/bin/bash
site put-api -n card -f openapi.edn
cd graphql
./deploy.sh
