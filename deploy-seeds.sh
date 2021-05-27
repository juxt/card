#!/bin/bash
for card in $(ls seeds);
do
    site put-json --file seeds/$card -u "{{base-uri}}/card/cards/$(basename -s .edn $card)";
done
