#!/bin/bash
site put-json --file seeds/checklist-1.edn -u {{base-uri}}/card/cards/checklist-1
site put-json --file seeds/task-1a.edn -u {{base-uri}}/card/cards/task-1a
site put-json --file seeds/task-1b.edn -u {{base-uri}}/card/cards/task-1b
site put-json --file seeds/section-containing-checklist-1.edn -u {{base-uri}}/card/cards/section-containing-checklist-1
